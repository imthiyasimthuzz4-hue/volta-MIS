package in.volta.mis.controller;

import in.volta.mis.model.Customer;
import in.volta.mis.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerRepository repo;

    public CustomerController(CustomerRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("customers", repo.findAll());
        m.addAttribute("customer", new Customer());
        return "customers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("customers", repo.findAll());
        m.addAttribute("customer", repo.findById(id).orElseThrow());
        m.addAttribute("editing", true);
        return "customers";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Customer customer, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "redirect:/customers";
        }
        repo.save(customer);
        ra.addFlashAttribute("message", "Customer saved successfully");
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("message", "Customer deleted");
        return "redirect:/customers";
    }
}
