package in.volta.mis.controller;
import in.volta.mis.model.Material; import in.volta.mis.repository.MaterialRepository;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/materials") public class MaterialController {
 private final MaterialRepository repo; public MaterialController(MaterialRepository r){repo=r;}
 @GetMapping public String list(Model m){m.addAttribute("records",repo.findAll());m.addAttribute("material",new Material());return "materials";}
 @PostMapping("/save") public String save(@ModelAttribute Material material,RedirectAttributes ra){repo.save(material);ra.addFlashAttribute("message","Material saved");return "redirect:/materials";}
 @PostMapping("/delete/{id}") public String delete(@PathVariable Long id,RedirectAttributes ra){repo.deleteById(id);ra.addFlashAttribute("message","Material deleted");return "redirect:/materials";}
}
