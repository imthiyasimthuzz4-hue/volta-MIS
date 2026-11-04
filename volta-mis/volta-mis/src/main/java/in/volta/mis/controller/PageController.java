package in.volta.mis.controller;
import in.volta.mis.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
@Controller public class PageController {
 private final CustomerRepository customers; private final ProjectRepository projects; private final StaffRepository staff; private final MaterialRepository materials; private final ExpenseRepository expenses;
 public PageController(CustomerRepository c, ProjectRepository p, StaffRepository s, MaterialRepository m, ExpenseRepository e){customers=c;projects=p;staff=s;materials=m;expenses=e;}
 @GetMapping("/") public String root(){return "redirect:/dashboard";}
 @GetMapping("/login") public String login(){return "login";}
 @GetMapping("/dashboard") public String dashboard(Model m){m.addAttribute("customerCount",customers.count());m.addAttribute("projectCount",projects.count());return "dashboard";}
 @GetMapping("/reports") public String reports(Model m){
   m.addAttribute("customerCount",customers.count());m.addAttribute("projectCount",projects.count());m.addAttribute("staffCount",staff.count());m.addAttribute("materialCount",materials.count());
   m.addAttribute("expenseCount",expenses.count());
   BigDecimal totalExpenses=expenses.findAll().stream().map(e->e.getAmount()==null?BigDecimal.ZERO:e.getAmount()).reduce(BigDecimal.ZERO,BigDecimal::add);
   BigDecimal totalProjectValue=projects.findAll().stream().map(p->p.getAmount()==null?BigDecimal.ZERO:p.getAmount()).reduce(BigDecimal.ZERO,BigDecimal::add);
   m.addAttribute("totalExpenses",totalExpenses);m.addAttribute("totalProjectValue",totalProjectValue);
   m.addAttribute("expenses",expenses.findAll());m.addAttribute("projects",projects.findAll());
   return "reports";
 }
}
