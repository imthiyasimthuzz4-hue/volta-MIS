package in.volta.mis.controller;
import in.volta.mis.repository.*; import in.volta.mis.model.Expense;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.GetMapping;
@Controller public class ReportsController {
 private final CustomerRepository customers; private final ProjectRepository projects; private final StaffRepository staff; private final MaterialRepository materials; private final ExpenseRepository expenses;
 public ReportsController(CustomerRepository c,ProjectRepository p,StaffRepository s,MaterialRepository m,ExpenseRepository e){customers=c;projects=p;staff=s;materials=m;expenses=e;}
 @GetMapping("/reports") public String reports(Model m){m.addAttribute("customerCount",customers.count());m.addAttribute("projectCount",projects.count());m.addAttribute("staffCount",staff.count());m.addAttribute("materialCount",materials.count());m.addAttribute("expenseCount",expenses.count());m.addAttribute("expenseTotal",expenses.findAll().stream().map(Expense::getAmount).filter(java.util.Objects::nonNull).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));m.addAttribute("expenses",expenses.findAll());return "reports";}
}
