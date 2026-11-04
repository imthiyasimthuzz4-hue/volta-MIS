package in.volta.mis.controller;
import in.volta.mis.repository.CustomerRepository; import in.volta.mis.repository.ProjectRepository;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.GetMapping;
@Controller public class PageController {
 private final CustomerRepository customers; private final ProjectRepository projects;
 public PageController(CustomerRepository c, ProjectRepository p){customers=c;projects=p;}
 @GetMapping("/") public String root(){return "redirect:/dashboard";}
 @GetMapping("/login") public String login(){return "login";}
 @GetMapping("/dashboard") public String dashboard(Model m){m.addAttribute("customerCount",customers.count());m.addAttribute("projectCount",projects.count());return "dashboard";}
}
