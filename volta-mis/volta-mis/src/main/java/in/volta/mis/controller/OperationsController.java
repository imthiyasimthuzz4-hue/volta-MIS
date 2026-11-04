package in.volta.mis.controller;
import in.volta.mis.model.*;
import in.volta.mis.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
@Controller
public class OperationsController {
 private final StaffRepository staffRepo; private final MaterialRepository materialRepo; private final ExpenseRepository expenseRepo;
 public OperationsController(StaffRepository s, MaterialRepository m, ExpenseRepository e){staffRepo=s;materialRepo=m;expenseRepo=e;}
 @GetMapping("/staff") public String staff(Model m){m.addAttribute("staffList",staffRepo.findAll());m.addAttribute("staff",new Staff());return "staff";}
 @GetMapping("/staff/edit/{id}") public String editStaff(@PathVariable Long id, Model m){m.addAttribute("staffList",staffRepo.findAll());m.addAttribute("staff",staffRepo.findById(id).orElseThrow());m.addAttribute("editing",true);return "staff";}
 @PostMapping("/staff/save") public String saveStaff(@ModelAttribute Staff staff, RedirectAttributes ra){if(staff.getSalary()==null)staff.setSalary(BigDecimal.ZERO);if(staff.getStatus()==null||staff.getStatus().isBlank())staff.setStatus("ACTIVE");staffRepo.save(staff);ra.addFlashAttribute("message","Staff member saved successfully");return "redirect:/staff";}
 @PostMapping("/staff/delete/{id}") public String deleteStaff(@PathVariable Long id,RedirectAttributes ra){staffRepo.deleteById(id);ra.addFlashAttribute("message","Staff member deleted");return "redirect:/staff";}
 @GetMapping("/materials") public String materials(Model m){m.addAttribute("materials",materialRepo.findAll());m.addAttribute("material",new Material());return "materials";}
 @GetMapping("/materials/edit/{id}") public String editMaterial(@PathVariable Long id, Model m){m.addAttribute("materials",materialRepo.findAll());m.addAttribute("material",materialRepo.findById(id).orElseThrow());m.addAttribute("editing",true);return "materials";}
 @PostMapping("/materials/save") public String saveMaterial(@ModelAttribute Material material,RedirectAttributes ra){if(material.getQuantity()==null)material.setQuantity(BigDecimal.ZERO);if(material.getUnitPrice()==null)material.setUnitPrice(BigDecimal.ZERO);materialRepo.save(material);ra.addFlashAttribute("message","Material saved successfully");return "redirect:/materials";}
 @PostMapping("/materials/delete/{id}") public String deleteMaterial(@PathVariable Long id,RedirectAttributes ra){materialRepo.deleteById(id);ra.addFlashAttribute("message","Material deleted");return "redirect:/materials";}
 @GetMapping("/expenses") public String expenses(Model m){m.addAttribute("expenses",expenseRepo.findAll());m.addAttribute("expense",new Expense());return "expenses";}
 @GetMapping("/expenses/edit/{id}") public String editExpense(@PathVariable Long id, Model m){m.addAttribute("expenses",expenseRepo.findAll());m.addAttribute("expense",expenseRepo.findById(id).orElseThrow());m.addAttribute("editing",true);return "expenses";}
 @PostMapping("/expenses/save") public String saveExpense(@ModelAttribute Expense expense,RedirectAttributes ra){if(expense.getAmount()==null)expense.setAmount(BigDecimal.ZERO);expenseRepo.save(expense);ra.addFlashAttribute("message","Expense saved successfully");return "redirect:/expenses";}
 @PostMapping("/expenses/delete/{id}") public String deleteExpense(@PathVariable Long id,RedirectAttributes ra){expenseRepo.deleteById(id);ra.addFlashAttribute("message","Expense deleted");return "redirect:/expenses";}
}
