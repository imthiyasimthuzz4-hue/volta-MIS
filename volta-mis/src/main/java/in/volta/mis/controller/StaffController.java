package in.volta.mis.controller;
import in.volta.mis.model.Staff; import in.volta.mis.repository.StaffRepository;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/staff") public class StaffController {
 private final StaffRepository repo; public StaffController(StaffRepository r){repo=r;}
 @GetMapping public String list(Model m){m.addAttribute("records",repo.findAll());m.addAttribute("staff",new Staff());return "staff";}
 @PostMapping("/save") public String save(@ModelAttribute Staff staff,RedirectAttributes ra){repo.save(staff);ra.addFlashAttribute("message","Staff record saved");return "redirect:/staff";}
 @PostMapping("/delete/{id}") public String delete(@PathVariable Long id,RedirectAttributes ra){repo.deleteById(id);ra.addFlashAttribute("message","Staff record deleted");return "redirect:/staff";}
}
