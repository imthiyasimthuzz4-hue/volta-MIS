package in.volta.mis.controller;

import in.volta.mis.model.*;
import in.volta.mis.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/material-requests")
public class SiteMaterialRequestController {

    private final SiteMaterialRequestRepository siteRequestRepo;
    private final MaterialRepository materialRepo;
    private final CompanyProfileRepository companyRepo;

    public SiteMaterialRequestController(SiteMaterialRequestRepository siteRequestRepo,
                                         MaterialRepository materialRepo,
                                         CompanyProfileRepository companyRepo) {
        this.siteRequestRepo = siteRequestRepo;
        this.materialRepo = materialRepo;
        this.companyRepo = companyRepo;
    }

    @GetMapping
    public String listRequests(@RequestParam(value = "status", required = false) String statusFilter, Model m) {
        List<SiteMaterialRequest> list;
        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
            list = siteRequestRepo.findByStatusOrderByRequestDateDescIdDesc(statusFilter.toUpperCase());
        } else {
            list = siteRequestRepo.findAllByOrderByRequestDateDescIdDesc();
        }

        m.addAttribute("requests", list);
        m.addAttribute("totalRequests", siteRequestRepo.count());
        m.addAttribute("pendingCount", siteRequestRepo.countByStatus("PENDING"));
        m.addAttribute("dispatchedCount", siteRequestRepo.countByStatus("DISPATCHED"));
        m.addAttribute("statusFilter", statusFilter != null ? statusFilter : "ALL");
        return "material-requests";
    }

    @PostMapping("/dispatch/{id}")
    public String dispatchRequest(@PathVariable Long id, RedirectAttributes ra) {
        SiteMaterialRequest req = siteRequestRepo.findById(id).orElse(null);
        if (req != null) {
            int stockDeductedCount = 0;
            StringBuilder log = new StringBuilder();

            for (SiteMaterialRequestItem itm : req.getItems()) {
                Material mat = null;
                if (itm.getMaterialId() != null) {
                    mat = materialRepo.findById(itm.getMaterialId()).orElse(null);
                }
                if (mat == null && itm.getMaterialName() != null) {
                    String name = itm.getMaterialName().trim();
                    mat = materialRepo.findAll().stream()
                            .filter(m -> m.getName().equalsIgnoreCase(name))
                            .findFirst().orElse(null);
                }

                if (mat != null && itm.getQuantity() != null && itm.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    mat.deductStock(itm.getQuantity());
                    materialRepo.save(mat);
                    stockDeductedCount++;
                    if (log.length() > 0) log.append(", ");
                    log.append(mat.getName()).append(" (-").append(itm.getQuantity()).append(" ").append(mat.getUnit()).append(")");
                }
            }

            req.setStatus("DISPATCHED");
            siteRequestRepo.save(req);

            if (stockDeductedCount > 0) {
                ra.addFlashAttribute("message", "Site Indent " + req.getIndentNumber() + " marked as DISPATCHED. Godown stock auto-deducted: " + log);
            } else {
                ra.addFlashAttribute("message", "Site Indent " + req.getIndentNumber() + " marked as DISPATCHED.");
            }
        }
        return "redirect:/material-requests";
    }

    @PostMapping("/delete/{id}")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes ra) {
        siteRequestRepo.deleteById(id);
        ra.addFlashAttribute("message", "Site Material Indent deleted successfully.");
        return "redirect:/material-requests";
    }

    @GetMapping({"/print", "/print/{id}", "/{id}/print"})
    public String printPackingSlip(@PathVariable(required = false) Long id, Model m) {
        SiteMaterialRequest req = null;
        if (id != null) {
            req = siteRequestRepo.findById(id).orElse(null);
        }
        if (req == null) {
            req = siteRequestRepo.findAllByOrderByRequestDateDescIdDesc().stream().findFirst().orElse(null);
        }
        if (req == null) {
            return "redirect:/material-requests";
        }

        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElse(new CompanyProfile());
        m.addAttribute("request", req);
        m.addAttribute("company", company);
        return "material-request-print";
    }
}
