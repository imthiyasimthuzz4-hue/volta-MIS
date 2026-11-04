package in.volta.mis;

import jakarta.servlet.http.HttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class MisController {
  private final JdbcTemplate db;
  public MisController(JdbcTemplate db) { this.db = db; }

  record Field(String key, String label, String type) {}
  record Module(String key, String title, String table, List<Field> fields, List<String> columns) {}
  private final Map<String, Module> modules = createModules();
  private Map<String, Module> createModules() {
    Map<String, Module> m = new LinkedHashMap<>();
    m.put("customers", new Module("customers","Customers","customers",List.of(new Field("name","Customer name","text"),new Field("phone","Phone","tel"),new Field("email","Email","email"),new Field("address","Address","text")),List.of("name","phone","email","address")));
    m.put("projects", new Module("projects","Projects","projects",List.of(new Field("project_name","Project name","text"),new Field("customer_name","Customer","text"),new Field("location","Location","text"),new Field("start_date","Start date","date"),new Field("end_date","End date","date"),new Field("status","Status","select:Planned,In Progress,On Hold,Completed"),new Field("amount","Contract amount (₹)","number"),new Field("notes","Notes","text")),List.of("project_name","customer_name","location","start_date","end_date","status","amount")));
    m.put("staff", new Module("staff","Staff","staff",List.of(new Field("name","Staff name","text"),new Field("role","Designation","text"),new Field("phone","Phone","tel"),new Field("email","Email","email"),new Field("salary","Monthly salary (₹)","number"),new Field("status","Status","select:Active,Inactive")),List.of("name","role","phone","email","salary","status")));
    m.put("materials", new Module("materials","Materials & Stock","materials",List.of(new Field("item_name","Item name","text"),new Field("category","Category","text"),new Field("quantity","Quantity","number"),new Field("unit","Unit","text"),new Field("unit_price","Unit price (₹)","number"),new Field("supplier","Supplier","text"),new Field("reorder_level","Reorder level","number")),List.of("item_name","category","quantity","unit","unit_price","supplier","reorder_level")));
    m.put("expenses", new Module("expenses","Expenses","expenses",List.of(new Field("expense_date","Date","date"),new Field("category","Category","text"),new Field("description","Description","text"),new Field("project_name","Project","text"),new Field("amount","Amount (₹)","number"),new Field("paid_to","Paid to","text"),new Field("payment_method","Payment method","select:Cash,Bank,UPI,Cheque,Other")),List.of("expense_date","category","description","project_name","amount","paid_to","payment_method")));
    return m;
  }
  private boolean logged(HttpSession s) { return Boolean.TRUE.equals(s.getAttribute("voltaLoggedIn")); }
  private String loginGuard(HttpSession s) { return logged(s) ? null : "redirect:/login"; }
  @GetMapping("/") public String root(HttpSession s) { return logged(s) ? "redirect:/dashboard" : "redirect:/login"; }
  @GetMapping("/login") public String loginPage(HttpSession s) { return logged(s) ? "redirect:/dashboard" : "login"; }
  @PostMapping("/login") public String login(@RequestParam String username, @RequestParam String password, HttpSession s, Model model) {
    String expectedUser = System.getenv().getOrDefault("VOLTA_ADMIN_USER","admin");
    String expectedPass = System.getenv().getOrDefault("VOLTA_ADMIN_PASSWORD","Volta@12345");
    if (expectedUser.equals(username.trim()) && expectedPass.equals(password)) { s.setAttribute("voltaLoggedIn",true); s.setAttribute("voltaUser",username.trim()); return "redirect:/dashboard"; }
    model.addAttribute("error","Username അല്ലെങ്കിൽ password ശരിയല്ല."); return "login";
  }
  @PostMapping("/logout") public String logout(HttpSession s) { s.invalidate(); return "redirect:/login"; }
  @GetMapping("/dashboard") public String dashboard(HttpSession s, Model model) {
    String g=loginGuard(s); if(g!=null)return g;
    model.addAttribute("username",s.getAttribute("voltaUser"));
    model.addAttribute("customerCount",count("customers")); model.addAttribute("projectCount",count("projects")); model.addAttribute("staffCount",count("staff"));
    model.addAttribute("stockCount",count("materials")); model.addAttribute("expenseTotal",db.queryForObject("SELECT COALESCE(SUM(amount),0) FROM expenses",BigDecimal.class));
    model.addAttribute("recentProjects",db.queryForList("SELECT id,project_name,customer_name,status,amount FROM projects ORDER BY id DESC LIMIT 5"));
    return "dashboard";
  }
  private long count(String table) { return db.queryForObject("SELECT COUNT(*) FROM "+table,Long.class); }
  @GetMapping("/{module}") public String list(@PathVariable String module,HttpSession s,Model model) {
    String g=loginGuard(s); if(g!=null)return g; Module m=modules.get(module); if(m==null)return "redirect:/dashboard";
    model.addAttribute("module",m); model.addAttribute("rows",db.queryForList("SELECT * FROM "+m.table()+" ORDER BY id DESC LIMIT 500")); model.addAttribute("username",s.getAttribute("voltaUser")); return "module";
  }
  @PostMapping("/{module}/save") public String save(@PathVariable String module,@RequestParam Map<String,String> params,HttpSession s,RedirectAttributes flash) {
    String g=loginGuard(s); if(g!=null)return g; Module m=modules.get(module); if(m==null)return "redirect:/dashboard";
    List<String> cols=new ArrayList<>(); List<Object> vals=new ArrayList<>();
    for(Field f:m.fields()) { String v=params.getOrDefault(f.key(),"").trim(); if(!v.isEmpty()){cols.add(f.key()); vals.add(v);} }
    if(cols.isEmpty()){flash.addFlashAttribute("message","ആവശ്യമായ വിവരങ്ങൾ നൽകുക.");return "redirect:/"+module;}
    String sql="INSERT INTO "+m.table()+" ("+String.join(",",cols)+") VALUES ("+String.join(",",Collections.nCopies(cols.size(),"?"))+")";
    try { db.update(sql,vals.toArray()); flash.addFlashAttribute("message","വിവരങ്ങൾ വിജയകരമായി സേവ് ചെയ്തു."); }
    catch(Exception e){ flash.addFlashAttribute("message","സേവ് ചെയ്യാനായില്ല. തീയതി/നമ്പർ ഫീൽഡുകൾ പരിശോധിക്കുക."); }
    return "redirect:/"+module;
  }
  @PostMapping("/{module}/delete/{id}") public String delete(@PathVariable String module,@PathVariable long id,HttpSession s,RedirectAttributes flash) {
    String g=loginGuard(s); if(g!=null)return g; Module m=modules.get(module); if(m==null)return "redirect:/dashboard";
    try { db.update("DELETE FROM "+m.table()+" WHERE id=?",id); flash.addFlashAttribute("message","റെക്കോർഡ് ഡിലീറ്റ് ചെയ്തു."); }
    catch(Exception e){flash.addFlashAttribute("message","ഡിലീറ്റ് ചെയ്യാനായില്ല.");} return "redirect:/"+module;
  }
  @GetMapping("/reports") public String reports(HttpSession s,Model model) {
    String g=loginGuard(s);if(g!=null)return g;
    model.addAttribute("customerCount",count("customers"));model.addAttribute("projectCount",count("projects"));model.addAttribute("staffCount",count("staff"));
    model.addAttribute("expenseTotal",db.queryForObject("SELECT COALESCE(SUM(amount),0) FROM expenses",BigDecimal.class));
    model.addAttribute("projects",db.queryForList("SELECT project_name,customer_name,status,amount FROM projects ORDER BY id DESC"));
    model.addAttribute("expenses",db.queryForList("SELECT expense_date,category,description,project_name,amount,paid_to FROM expenses ORDER BY id DESC LIMIT 100"));return "reports";
  }
}
