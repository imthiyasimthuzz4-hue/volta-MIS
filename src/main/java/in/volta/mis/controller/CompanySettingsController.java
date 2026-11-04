package in.volta.mis.controller;

import in.volta.mis.model.CompanyProfile;
import in.volta.mis.repository.CompanyProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company-settings")
public class CompanySettingsController {
    private final CompanyProfileRepository companyRepo;

    public CompanySettingsController(CompanyProfileRepository c) {
        this.companyRepo = c;
    }

    @GetMapping
    public String view(Model m) {
        CompanyProfile cp = companyRepo.findAll().stream().findFirst().orElseGet(() -> {
            CompanyProfile def = new CompanyProfile();
            return companyRepo.save(def);
        });
        m.addAttribute("company", cp);
        return "company-settings";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CompanyProfile company, RedirectAttributes ra) {
        companyRepo.save(company);
        ra.addFlashAttribute("message", "Company Profile & Official Details updated successfully");
        return "redirect:/company-settings";
    }
}
