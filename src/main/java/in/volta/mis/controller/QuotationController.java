package in.volta.mis.controller;

import in.volta.mis.model.CompanyProfile;
import in.volta.mis.model.Quotation;
import in.volta.mis.repository.CompanyProfileRepository;
import in.volta.mis.repository.CustomerRepository;
import in.volta.mis.repository.ProjectRepository;
import in.volta.mis.repository.QuotationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Controller
@RequestMapping("/quotations")
public class QuotationController {
    private final QuotationRepository quotationRepo;
    private final CustomerRepository customerRepo;
    private final ProjectRepository projectRepo;
    private final CompanyProfileRepository companyRepo;

    public QuotationController(QuotationRepository q, CustomerRepository c, ProjectRepository p, CompanyProfileRepository comp) {
        this.quotationRepo = q;
        this.customerRepo = c;
        this.projectRepo = p;
        this.companyRepo = comp;
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("quotations", quotationRepo.findAll());
        Quotation newQt = new Quotation();
        newQt.setQuotationDate(LocalDate.now());
        newQt.setValidUntil(LocalDate.now().plusDays(30));
        newQt.setQuotationNumber("QT-" + LocalDate.now().getYear() + "-" + String.format("%03d", quotationRepo.count() + 101));
        m.addAttribute("quotation", newQt);
        m.addAttribute("customers", customerRepo.findAll());
        m.addAttribute("projects", projectRepo.findAll());
        return "quotations";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("quotations", quotationRepo.findAll());
        m.addAttribute("quotation", quotationRepo.findById(id).orElseThrow());
        m.addAttribute("customers", customerRepo.findAll());
        m.addAttribute("projects", projectRepo.findAll());
        m.addAttribute("editing", true);
        return "quotations";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Quotation quotation, RedirectAttributes ra) {
        if (quotation.getQuotationNumber() == null || quotation.getQuotationNumber().isBlank()) {
            quotation.setQuotationNumber("QT-" + LocalDate.now().getYear() + "-" + String.format("%03d", quotationRepo.count() + 101));
        }
        if (quotation.getQuotationDate() == null) {
            quotation.setQuotationDate(LocalDate.now());
        }
        if (quotation.getSubTotal() == null) {
            quotation.setSubTotal(BigDecimal.ZERO);
        }
        if (quotation.getGstPercent() == null) {
            quotation.setGstPercent(new BigDecimal("18.00"));
        }

        // Auto calculate GST and Grand Total
        BigDecimal gstAmount = quotation.getSubTotal()
                .multiply(quotation.getGstPercent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = quotation.getSubTotal().add(gstAmount);

        quotation.setGstAmount(gstAmount);
        quotation.setGrandTotal(grandTotal);

        quotationRepo.save(quotation);
        ra.addFlashAttribute("message", "Quotation " + quotation.getQuotationNumber() + " saved successfully");
        return "redirect:/quotations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        quotationRepo.deleteById(id);
        ra.addFlashAttribute("message", "Quotation deleted successfully");
        return "redirect:/quotations";
    }

    @GetMapping({"/print/{id}", "/{id}/print"})
    public String print(@PathVariable Long id, Model m) {
        Quotation qt = quotationRepo.findById(id).orElseThrow();
        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElse(new CompanyProfile());
        m.addAttribute("quotation", qt);
        m.addAttribute("company", company);
        return "quotation-print";
    }
}
