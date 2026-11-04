package in.volta.mis.controller;

import in.volta.mis.model.Project;
import in.volta.mis.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository repo;

    public ProjectController(ProjectRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("projects", repo.findAll());
        m.addAttribute("project", new Project());
        return "projects";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("projects", repo.findAll());
        m.addAttribute("project", repo.findById(id).orElseThrow());
        m.addAttribute("editing", true);
        return "projects";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Project project, RedirectAttributes ra) {
        repo.save(project);
        ra.addFlashAttribute("message", "Project saved successfully");
        return "redirect:/projects";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("message", "Project deleted");
        return "redirect:/projects";
    }
}
