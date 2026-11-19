package in.volta.mis.controller;

import in.volta.mis.model.*;
import in.volta.mis.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/mobile")
public class MobileController {

    private final CustomerRepository customerRepo;
    private final ProjectRepository projectRepo;
    private final MaterialRepository materialRepo;
    private final InvoiceRepository invoiceRepo;
    private final StaffRepository staffRepo;
    private final WorkOrderRepository workOrderRepo;
    private final CompanyProfileRepository companyRepo;
    private final SiteMaterialRequestRepository siteRequestRepo;

    public MobileController(CustomerRepository customerRepo,
                            ProjectRepository projectRepo,
                            MaterialRepository materialRepo,
                            InvoiceRepository invoiceRepo,
                            StaffRepository staffRepo,
                            WorkOrderRepository workOrderRepo,
                            CompanyProfileRepository companyRepo,
                            SiteMaterialRequestRepository siteRequestRepo) {
        this.customerRepo = customerRepo;
        this.projectRepo = projectRepo;
        this.materialRepo = materialRepo;
        this.invoiceRepo = invoiceRepo;
        this.staffRepo = staffRepo;
        this.workOrderRepo = workOrderRepo;
        this.companyRepo = companyRepo;
        this.siteRequestRepo = siteRequestRepo;
    }

    /**
     * Mobile App Central Hub (Home screen of the VOLTA Electrical Associates PWA)
     */
    @RequestMapping(value = {"", "/hub"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String mobileHub(Model m, Authentication auth) {
        m.addAttribute("appName", "VOLTA Electrical Associates");
        m.addAttribute("isAuthenticated", auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser"));
        if (auth != null && auth.isAuthenticated()) {
            m.addAttribute("username", auth.getName());
            boolean isSupervisorOrAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_ENGINEER")
                                || a.getAuthority().equals("ROLE_ACCOUNTANT"));
            m.addAttribute("canBill", isSupervisorOrAdmin);
        } else {
            m.addAttribute("canBill", false);
        }

        m.addAttribute("totalMaterials", materialRepo.count());
        m.addAttribute("activeProjects", projectRepo.count());
        m.addAttribute("pendingIndentCount", siteRequestRepo.countByStatus("PENDING"));
        m.addAttribute("recentInvoices", invoiceRepo.findAllByOrderByInvoiceDateDesc().stream().limit(5).toList());
        return "mobile-hub";
    }

    /**
     * Mobile Quick Site Billing screen (Designed specifically for 1-hand thumb use on phones)
     */
    @GetMapping("/quick-bill")
    public String quickBillingForm(Model m) {
        List<Customer> customers = customerRepo.findAll();
        List<Project> projects = projectRepo.findAll();
        List<Material> materials = materialRepo.findAll();

        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setInvoiceNumber("VOLTA-SITE-" + LocalDate.now().getYear() + "-" + String.format("%03d", invoiceRepo.count() + 1));
        invoice.setPlaceOfSupply("32-Kerala");
        invoice.setSupplyType("INTRA_STATE");

        m.addAttribute("invoice", invoice);
        m.addAttribute("customers", customers);
        m.addAttribute("projects", projects);
        m.addAttribute("materials", materials);
        return "mobile-quick-bill";
    }

    /**
     * Save Quick Site Bill from Phone
     */
    @PostMapping(value = {"/quick-bill", "/quick-bill/save"})
    public String saveQuickBill(@ModelAttribute Invoice invoice,
                                @RequestParam(name = "selectedCustomerId", required = false) Long customerId,
                                @RequestParam(name = "newCustomerName", required = false) String newCustomerName,
                                @RequestParam(name = "newCustomerPhone", required = false) String newCustomerPhone,
                                @RequestParam(name = "itemMaterialId", required = false) List<Long> materialIds,
                                @RequestParam(name = "itemDescription", required = false) List<String> descriptions,
                                @RequestParam(name = "itemHsn", required = false) List<String> hsns,
                                @RequestParam(name = "itemQty", required = false) List<BigDecimal> quantities,
                                @RequestParam(name = "itemUnit", required = false) List<String> units,
                                @RequestParam(name = "itemRate", required = false) List<BigDecimal> rates,
                                @RequestParam(name = "itemGstRate", required = false) List<BigDecimal> gstRates,
                                RedirectAttributes ra) {

        // Customer handling
        if (newCustomerName != null && !newCustomerName.trim().isEmpty()) {
            Customer c = new Customer();
            c.setName(newCustomerName.trim());
            c.setPhone(newCustomerPhone != null ? newCustomerPhone.trim() : "");
            c = customerRepo.save(c);
            invoice.setCustomerName(c.getName());
            invoice.setCustomerPhone(c.getPhone());
        } else if (customerId != null) {
            customerRepo.findById(customerId).ifPresent(c -> {
                invoice.setCustomerName(c.getName());
                invoice.setCustomerPhone(c.getPhone());
                invoice.setCustomerGstin(c.getGstin());
                invoice.setCustomerAddress(c.getAddress());
            });
        }

        if (invoice.getCustomerName() == null || invoice.getCustomerName().isBlank()) {
            invoice.setCustomerName("Site Client / Cash");
        }

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber("VOLTA-SITE-" + LocalDate.now().getYear() + "-" + String.format("%03d", invoiceRepo.count() + 1));
        }
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDate.now().plusDays(15));
        }
        if (invoice.getPlaceOfSupply() == null || invoice.getPlaceOfSupply().isBlank()) {
            invoice.setPlaceOfSupply("32-Kerala");
        }
        if (invoice.getSupplyType() == null || invoice.getSupplyType().isBlank()) {
            invoice.setSupplyType("INTRA_STATE");
        }

        // Add items submitted from mobile form
        if (descriptions != null && !descriptions.isEmpty()) {
            invoice.clearItems();
            for (int i = 0; i < descriptions.size(); i++) {
                String desc = descriptions.get(i);
                if (desc != null && !desc.trim().isEmpty()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setDescription(desc.trim());
                    if (materialIds != null && i < materialIds.size()) {
                        item.setMaterialId(materialIds.get(i));
                    }
                    item.setHsnSac(hsns != null && i < hsns.size() && hsns.get(i) != null ? hsns.get(i) : "8544");
                    item.setQuantity(quantities != null && i < quantities.size() && quantities.get(i) != null ? quantities.get(i) : BigDecimal.ONE);
                    item.setUnit(units != null && i < units.size() && units.get(i) != null ? units.get(i) : "Nos");
                    item.setUnitPrice(rates != null && i < rates.size() && rates.get(i) != null ? rates.get(i) : BigDecimal.ZERO);
                    item.setGstRate(gstRates != null && i < gstRates.size() && gstRates.get(i) != null ? gstRates.get(i) : new BigDecimal("18.00"));
                    invoice.addItem(item);
                }
            }
        }

        // Fallback item if none selected
        if (invoice.getItems().isEmpty()) {
            InvoiceItem fallback = new InvoiceItem();
            fallback.setDescription("Site Electrical Work & Materials");
            fallback.setHsnSac("995461");
            fallback.setQuantity(BigDecimal.ONE);
            fallback.setUnit("Job");
            fallback.setUnitPrice(invoice.getTaxableAmount() != null ? invoice.getTaxableAmount() : BigDecimal.ZERO);
            fallback.setGstRate(new BigDecimal("18.00"));
            invoice.addItem(fallback);
        }

        invoice.calculateTotals();
        Invoice saved = invoiceRepo.save(invoice);

        // Auto-deduct inventory for billed materials
        for (InvoiceItem item : saved.getItems()) {
            if (item.getMaterialId() != null && item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                materialRepo.findById(item.getMaterialId()).ifPresent(mat -> {
                    mat.deductStock(item.getQuantity());
                    materialRepo.save(mat);
                });
            }
        }

        return "redirect:/mobile/quick-bill/success/" + saved.getId();
    }

    /**
     * Mobile Bill Success Screen with Instant WhatsApp Sharing
     */
    @GetMapping("/quick-bill/success/{id}")
    public String quickBillSuccess(@PathVariable Long id, Model m) {
        Invoice invoice = invoiceRepo.findById(id).orElse(null);
        if (invoice == null) {
            invoice = invoiceRepo.findAllByOrderByInvoiceDateDesc().stream().findFirst().orElse(null);
        }
        if (invoice == null) {
            return "redirect:/mobile";
        }
        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElse(new CompanyProfile());

        m.addAttribute("invoice", invoice);
        m.addAttribute("company", company);

        // Build WhatsApp share message
        StringBuilder waText = new StringBuilder();
        waText.append("*⚡ VOLTA Electrical Associates*\n");
        waText.append("Official Site Tax Invoice\n\n");
        waText.append("🧾 *Bill No:* ").append(invoice.getInvoiceNumber()).append("\n");
        waText.append("📅 *Date:* ").append(invoice.getInvoiceDate()).append("\n");
        if (invoice.getCustomerName() != null) {
            waText.append("👤 *Customer:* ").append(invoice.getCustomerName()).append("\n");
        }
        waText.append("💰 *Grand Total:* ₹").append(invoice.getGrandTotal()).append("\n\n");
        waText.append("*Items:*\n");
        for (InvoiceItem itm : invoice.getItems()) {
            waText.append("• ").append(itm.getDescription())
                  .append(" (").append(itm.getQuantity()).append(" ").append(itm.getUnit())
                  .append(" @ ₹").append(itm.getUnitPrice()).append(")\n");
        }
        waText.append("\nThank you for choosing VOLTA Electrical Associates!");

        String encodedWa = URLEncoder.encode(waText.toString(), StandardCharsets.UTF_8);
        m.addAttribute("waShareUrl", "https://wa.me/?text=" + encodedWa);
        if (invoice.getCustomerPhone() != null && !invoice.getCustomerPhone().isBlank()) {
            String cleanPhone = invoice.getCustomerPhone().replaceAll("[^0-9]", "");
            if (cleanPhone.length() == 10) cleanPhone = "91" + cleanPhone;
            m.addAttribute("waDirectCustomerUrl", "https://wa.me/" + cleanPhone + "?text=" + encodedWa);
        }

        return "mobile-bill-success";
    }

    /**
     * Mobile Material Stock Lookup for Field Supervisors
     */
    @GetMapping("/materials")
    public String mobileMaterials(Model m) {
        m.addAttribute("materials", materialRepo.findAll());
        return "mobile-materials";
    }

    /**
     * Site Material Request / Indent Screen (Order materials for tomorrow's site work)
     */
    @GetMapping(value = {"/indent", "/materials/request"})
    public String siteMaterialRequestForm(Model m, Authentication auth) {
        SiteMaterialRequest req = new SiteMaterialRequest();
        req.setRequestDate(LocalDate.now());
        req.setRequiredForDate(LocalDate.now().plusDays(1)); // Default: Tomorrow
        req.setIndentNumber("IND-" + LocalDate.now().getYear() + "-" + String.format("%03d", siteRequestRepo.count() + 1));
        req.setPriority("NORMAL");
        req.setStatus("PENDING");

        if (auth != null && auth.isAuthenticated() && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            staffRepo.findAll().stream()
                    .filter(s -> s.getFullName() != null && s.getFullName().toLowerCase().contains(auth.getName().toLowerCase()))
                    .findFirst()
                    .ifPresent(s -> {
                        req.setRequestedBy(s.getFullName());
                        req.setRequestedByPhone(s.getPhone());
                    });
            if (req.getRequestedBy() == null || req.getRequestedBy().isBlank()) {
                req.setRequestedBy(auth.getName());
            }
        }

        m.addAttribute("siteRequest", req);
        m.addAttribute("materials", materialRepo.findAll());
        m.addAttribute("projects", projectRepo.findAll());
        m.addAttribute("staffList", staffRepo.findAll());
        return "mobile-material-request";
    }

    /**
     * Save Site Material Request
     */
    @PostMapping(value = {"/indent", "/indent/save", "/materials/request/save"})
    public String saveSiteMaterialRequest(@ModelAttribute SiteMaterialRequest siteRequest,
                                          @RequestParam(name = "itemMaterialId", required = false) List<Long> materialIds,
                                          @RequestParam(name = "itemDescription", required = false) List<String> descriptions,
                                          @RequestParam(name = "itemQty", required = false) List<BigDecimal> quantities,
                                          @RequestParam(name = "itemUnit", required = false) List<String> units,
                                          @RequestParam(name = "itemNotes", required = false) List<String> itemNotes,
                                          RedirectAttributes ra) {

        if (siteRequest.getIndentNumber() == null || siteRequest.getIndentNumber().isBlank()) {
            siteRequest.setIndentNumber("IND-" + LocalDate.now().getYear() + "-" + String.format("%03d", siteRequestRepo.count() + 1));
        }
        if (siteRequest.getRequestDate() == null) {
            siteRequest.setRequestDate(LocalDate.now());
        }
        if (siteRequest.getRequiredForDate() == null) {
            siteRequest.setRequiredForDate(LocalDate.now().plusDays(1));
        }
        if (siteRequest.getProjectName() == null || siteRequest.getProjectName().isBlank()) {
            siteRequest.setProjectName("General Site Work");
        }
        if (siteRequest.getRequestedBy() == null || siteRequest.getRequestedBy().isBlank()) {
            siteRequest.setRequestedBy("Site Electrician / Supervisor");
        }

        // Reconstruct items
        siteRequest.clearItems();
        if (descriptions != null && !descriptions.isEmpty()) {
            for (int i = 0; i < descriptions.size(); i++) {
                String desc = descriptions.get(i);
                if (desc != null && !desc.trim().isEmpty()) {
                    BigDecimal qty = (quantities != null && i < quantities.size() && quantities.get(i) != null) ? quantities.get(i) : BigDecimal.ONE;
                    String unit = (units != null && i < units.size() && units.get(i) != null && !units.get(i).isBlank()) ? units.get(i) : "Nos";
                    Long matId = (materialIds != null && i < materialIds.size()) ? materialIds.get(i) : null;
                    String note = (itemNotes != null && i < itemNotes.size()) ? itemNotes.get(i) : null;

                    SiteMaterialRequestItem item = new SiteMaterialRequestItem(desc.trim(), qty, unit);
                    item.setMaterialId(matId);
                    item.setNotes(note);
                    siteRequest.addItem(item);
                }
            }
        }

        // Fallback default item if none provided
        if (siteRequest.getItems().isEmpty()) {
            SiteMaterialRequestItem fallback = new SiteMaterialRequestItem("Standard Site Electrical Materials", BigDecimal.ONE, "Lot");
            siteRequest.addItem(fallback);
        }

        siteRequest.setStatus("PENDING");
        SiteMaterialRequest saved = siteRequestRepo.save(siteRequest);

        return "redirect:/mobile/indent/success/" + saved.getId();
    }

    /**
     * Success Screen with 1-Tap WhatsApp Share to Office/Godown
     */
    @GetMapping(value = {"/indent/success/{id}", "/materials/request/success/{id}"})
    public String siteMaterialRequestSuccess(@PathVariable Long id, Model m) {
        SiteMaterialRequest req = siteRequestRepo.findById(id).orElse(null);
        if (req == null) {
            req = siteRequestRepo.findAllByOrderByRequestDateDescIdDesc().stream().findFirst().orElse(null);
        }
        if (req == null) {
            return "redirect:/mobile";
        }

        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElse(new CompanyProfile());

        // Format pre-filled WhatsApp message
        StringBuilder wa = new StringBuilder();
        wa.append("⚡ *VOLTA ELECTRICAL ASSOCIATES*\n");
        wa.append("📋 *SITE MATERIAL REQUISITION / INDENT*\n\n");
        wa.append("🔖 *Indent No:* ").append(req.getIndentNumber()).append("\n");
        wa.append("📍 *Site / Project:* ").append(req.getProjectName()).append("\n");
        if (req.getSiteLocation() != null && !req.getSiteLocation().isBlank()) {
            wa.append("📌 *Location:* ").append(req.getSiteLocation()).append("\n");
        }
        wa.append("📅 *Needed For:* ").append(req.getRequiredForDate()).append(" (Tomorrow)\n");
        wa.append("👤 *Requested By:* ").append(req.getRequestedBy()).append("\n");
        if ("URGENT".equalsIgnoreCase(req.getPriority())) {
            wa.append("🚨 *Priority:* ⚠️ URGENT / HIGH PRIORITY\n");
        }

        wa.append("\n📦 *REQUIRED MATERIALS / സാധനങ്ങൾ:*\n");
        int idx = 1;
        for (SiteMaterialRequestItem itm : req.getItems()) {
            wa.append(idx++).append(". ").append(itm.getMaterialName())
              .append(" — *").append(itm.getQuantity()).append(" ").append(itm.getUnit()).append("*");
            if (itm.getNotes() != null && !itm.getNotes().isBlank()) {
                wa.append(" (").append(itm.getNotes()).append(")");
            }
            wa.append("\n");
        }

        if (req.getNotes() != null && !req.getNotes().isBlank()) {
            wa.append("\n📝 *Notes / Remarks:*\n").append(req.getNotes()).append("\n");
        }

        wa.append("\n_Sent from VOLTA Mobile App Portal_");

        String encodedWa = URLEncoder.encode(wa.toString(), StandardCharsets.UTF_8);
        m.addAttribute("waShareUrl", "https://api.whatsapp.com/send?text=" + encodedWa);

        if (company.getPhone() != null && !company.getPhone().isBlank()) {
            String officeClean = company.getPhone().replaceAll("[^0-9]", "");
            if (officeClean.length() == 10) officeClean = "91" + officeClean;
            m.addAttribute("waOfficeUrl", "https://wa.me/" + officeClean + "?text=" + encodedWa);
        } else {
            m.addAttribute("waOfficeUrl", "https://api.whatsapp.com/send?text=" + encodedWa);
        }

        m.addAttribute("siteRequest", req);
        m.addAttribute("company", company);
        return "mobile-material-request-success";
    }

    /**
     * Mobile List of Recent Site Material Requests
     */
    @GetMapping(value = {"/indent/list", "/materials/requests"})
    public String siteMaterialRequestList(Model m) {
        m.addAttribute("requests", siteRequestRepo.findAllByOrderByRequestDateDescIdDesc());
        return "mobile-material-requests";
    }
}
