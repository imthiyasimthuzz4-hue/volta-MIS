package in.volta.mis.controller;
import in.volta.mis.model.Expense; import in.volta.mis.repository.ExpenseRepository;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/expenses") public class ExpenseController {
 private final ExpenseRepository repo; public ExpenseController(ExpenseRepository r){repo=r;}
 @GetMapping public String list(Model m){m.addAttribute("records",repo.findAll());m.addAttribute("expense",new Expense());m.addAttribute("total",repo.findAll().stream().map(Expense::getAmount).filter(java.util.Objects::nonNull).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));return "expenses";}
 @PostMapping("/save") public String save(@ModelAttribute Expense expense,RedirectAttributes ra){if(expense.getExpenseDate()==null)expense.setExpenseDate(java.time.LocalDate.now());repo.save(expense);ra.addFlashAttribute("message","Expense saved");return "redirect:/expenses";}
 @PostMapping("/delete/{id}") public String delete(@PathVariable Long id,RedirectAttributes ra){repo.deleteById(id);ra.addFlashAttribute("message","Expense deleted");return "redirect:/expenses";}
}
