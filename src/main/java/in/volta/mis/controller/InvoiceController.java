package in.volta.mis.controller;

import in.volta.mis.model.CompanyProfile;
import in.volta.mis.model.Customer;
import in.volta.mis.model.Invoice;
import in.volta.mis.model.InvoiceItem;
import in.volta.mis.model.Quotation;
import in.volta.mis.repository.CompanyProfileRepository;
import in.volta.mis.repository.CustomerRepository;
import in.volta.mis.repository.InvoiceRepository;
import in.volta.mis.repository.MaterialRepository;
import in.volta.mis.repository.ProjectRepository;
import in.volta.mis.repository.QuotationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepo;
    private final CustomerRepository customerRepo;
    private final ProjectRepository projectRepo;
    private final QuotationRepository quotationRepo;
    private final CompanyProfileRepository companyRepo;
    private final MaterialRepository materialRepo;

    public InvoiceController(InvoiceRepository i, CustomerRepository c, ProjectRepository p, QuotationRepository q, CompanyProfileRepository cp, MaterialRepository m) {
        this.invoiceRepo = i;
        this.customerRepo = c;
        this.projectRepo = p;
        this.quotationRepo = q;
        this.companyRepo = cp;
        this.materialRepo = m;
    }

    @GetMapping
    public String list(Model m) {
        List<Invoice> invoices = invoiceRepo.findAllByOrderByInvoiceDateDesc();
        m.addAttribute("invoices", invoices);
        m.addAttribute("materials", materialRepo.findAll());

        BigDecimal totalInvoiced = invoiceRepo.getTotalInvoicedAmount();
        BigDecimal totalTaxable = invoiceRepo.getTotalTaxableAmount();
        BigDecimal totalGst = invoiceRepo.getTotalGstCollected();
        BigDecimal totalPending = invoiceRepo.getTotalPendingBalance();

        m.addAttribute("totalInvoiced", totalInvoiced);
        m.addAttribute("totalTaxable", totalTaxable);
        m.addAttribute("totalGst", totalGst);
        m.addAttribute("totalPending", totalPending);

        Invoice newInv = new Invoice();
        newInv.setInvoiceDate(LocalDate.now());
        newInv.setDueDate(LocalDate.now().plusDays(15));
        newInv.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + String.format("%03d", invoiceRepo.count() + 1));
        newInv.setPlaceOfSupply("32-Kerala");
        newInv.setSupplyType("INTRA_STATE");

        InvoiceItem defaultItem = new InvoiceItem();
        defaultItem.setDescription("Electrical Installation, Switchgear & Wiring Works");
        defaultItem.setHsnSac("995461");
        defaultItem.setQuantity(BigDecimal.ONE);
        defaultItem.setUnit("Lot");
        defaultItem.setUnitPrice(new BigDecimal("50000.00"));
        defaultItem.setGstRate(new BigDecimal("18.00"));
        newInv.addItem(defaultItem);

        m.addAttribute("invoice", newInv);
        m.addAttribute("customers", customerRepo.findAll());
        m.addAttribute("projects", projectRepo.findAll());
        return "invoices";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        Invoice inv = invoiceRepo.findById(id).orElseThrow();
        m.addAttribute("invoices", invoiceRepo.findAllByOrderByInvoiceDateDesc());
        m.addAttribute("invoice", inv);
        m.addAttribute("editing", true);
        m.addAttribute("customers", customerRepo.findAll());
        m.addAttribute("projects", projectRepo.findAll());
        m.addAttribute("materials", materialRepo.findAll());

        m.addAttribute("totalInvoiced", invoiceRepo.getTotalInvoicedAmount());
        m.addAttribute("totalTaxable", invoiceRepo.getTotalTaxableAmount());
        m.addAttribute("totalGst", invoiceRepo.getTotalGstCollected());
        m.addAttribute("totalPending", invoiceRepo.getTotalPendingBalance());
        return "invoices";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Invoice invoice,
                       @RequestParam(name = "itemMaterialId", required = false) List<Long> materialIds,
                       @RequestParam(name = "itemDescription", required = false) List<String> descriptions,
                       @RequestParam(name = "itemHsn", required = false) List<String> hsns,
                       @RequestParam(name = "itemQty", required = false) List<BigDecimal> quantities,
                       @RequestParam(name = "itemUnit", required = false) List<String> units,
                       @RequestParam(name = "itemRate", required = false) List<BigDecimal> rates,
                       @RequestParam(name = "itemGstRate", required = false) List<BigDecimal> gstRates,
                       RedirectAttributes ra) {

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + String.format("%03d", invoiceRepo.count() + 1));
        }
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDate.now().plusDays(15));
        }

        // Reconstruct items if submitted via array parameters
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
                    item.setHsnSac(hsns != null && i < hsns.size() && hsns.get(i) != null ? hsns.get(i) : "995461");
                    item.setQuantity(quantities != null && i < quantities.size() && quantities.get(i) != null ? quantities.get(i) : BigDecimal.ONE);
                    item.setUnit(units != null && i < units.size() && units.get(i) != null ? units.get(i) : "Nos");
                    item.setUnitPrice(rates != null && i < rates.size() && rates.get(i) != null ? rates.get(i) : BigDecimal.ZERO);
                    item.setGstRate(gstRates != null && i < gstRates.size() && gstRates.get(i) != null ? gstRates.get(i) : new BigDecimal("18.00"));
                    invoice.addItem(item);
                }
            }
        }

        // Fallback: If no items, provide at least one item from default
        if (invoice.getItems().isEmpty()) {
            InvoiceItem fallback = new InvoiceItem();
            fallback.setDescription("Electrical Contracting & Engineering Works");
            fallback.setHsnSac("995461");
            fallback.setQuantity(BigDecimal.ONE);
            fallback.setUnit("Lot");
            fallback.setUnitPrice(invoice.getTaxableAmount() != null && invoice.getTaxableAmount().compareTo(BigDecimal.ZERO) > 0 ? invoice.getTaxableAmount() : BigDecimal.ZERO);
            fallback.setGstRate(new BigDecimal("18.00"));
            invoice.addItem(fallback);
        }

        invoice.calculateTotals();
        invoiceRepo.save(invoice);

        // Automatic Inventory Deduction (Deduct billed quantity from godown inventory)
        int deductedCount = 0;
        StringBuilder stockNotes = new StringBuilder();
        for (InvoiceItem item : invoice.getItems()) {
            in.volta.mis.model.Material matched = null;
            if (item.getMaterialId() != null) {
                matched = materialRepo.findById(item.getMaterialId()).orElse(null);
            }
            if (matched == null && item.getDescription() != null) {
                String desc = item.getDescription().trim();
                matched = materialRepo.findAll().stream()
                        .filter(mat -> mat.getName().equalsIgnoreCase(desc))
                        .findFirst().orElse(null);
                if (matched != null) {
                    item.setMaterialId(matched.getId());
                }
            }

            if (matched != null && item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                matched.deductStock(item.getQuantity());
                materialRepo.save(matched);
                deductedCount++;
                if (stockNotes.length() > 0) stockNotes.append(", ");
                stockNotes.append(matched.getName())
                          .append(" (-").append(item.getQuantity()).append(" ").append(matched.getUnit())
                          .append(" | Remaining: ").append(matched.getQuantity()).append(")");
            }
        }

        if (deductedCount > 0) {
            ra.addFlashAttribute("message", "GST Tax Invoice " + invoice.getInvoiceNumber() + " generated successfully. Auto-deducted from Godown stock: " + stockNotes);
        } else {
            ra.addFlashAttribute("message", "GST Tax Invoice " + invoice.getInvoiceNumber() + " saved successfully.");
        }
        return "redirect:/invoices";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Invoice inv = invoiceRepo.findById(id).orElse(null);
        if (inv != null) {
            // Restore stock if material was deducted
            for (InvoiceItem itm : inv.getItems()) {
                if (itm.getMaterialId() != null && itm.getQuantity() != null) {
                    materialRepo.findById(itm.getMaterialId()).ifPresent(m -> {
                        m.addStock(itm.getQuantity());
                        materialRepo.save(m);
                    });
                }
            }
            invoiceRepo.delete(inv);
            ra.addFlashAttribute("message", "GST Invoice " + inv.getInvoiceNumber() + " deleted. Material stock restored.");
        } else {
            ra.addFlashAttribute("message", "GST Invoice deleted.");
        }
        return "redirect:/invoices";
    }

    @PostMapping("/{id}/record-payment")
    public String recordPayment(@PathVariable Long id,
                                @RequestParam("paymentAmount") BigDecimal amount,
                                @RequestParam("paymentMode") String mode,
                                RedirectAttributes ra) {
        Invoice inv = invoiceRepo.findById(id).orElseThrow();
        BigDecimal currentPaid = inv.getPaidAmount() != null ? inv.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaid = currentPaid.add(amount != null ? amount : BigDecimal.ZERO);
        inv.setPaidAmount(newPaid);
        inv.setPaymentMode(mode);
        inv.calculateTotals();
        invoiceRepo.save(inv);
        ra.addFlashAttribute("message", "Payment of ₹ " + amount + " recorded for Invoice " + inv.getInvoiceNumber());
        return "redirect:/invoices";
    }

    @GetMapping("/from-quotation/{quotationId}")
    public String createFromQuotation(@PathVariable Long quotationId, RedirectAttributes ra) {
        Quotation q = quotationRepo.findById(quotationId).orElseThrow();

        Invoice inv = new Invoice();
        inv.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + String.format("%03d", invoiceRepo.count() + 1));
        inv.setInvoiceDate(LocalDate.now());
        inv.setDueDate(LocalDate.now().plusDays(15));
        inv.setCustomerName(q.getCustomerName());
        inv.setProjectName(q.getProjectName());
        inv.setQuotationReference(q.getQuotationNumber());
        inv.setPlaceOfSupply("32-Kerala");
        inv.setSupplyType("INTRA_STATE");

        // Try to find customer GSTIN
        Customer c = customerRepo.findAll().stream()
                .filter(cust -> cust.getName().equalsIgnoreCase(q.getCustomerName()))
                .findFirst().orElse(null);
        if (c != null) {
            if (c.getGstin() != null && !c.getGstin().isBlank()) inv.setCustomerGstin(c.getGstin());
            if (c.getAddress() != null) inv.setCustomerAddress(c.getAddress());
            if (c.getPhone() != null) inv.setCustomerPhone(c.getPhone());
        }

        InvoiceItem item = new InvoiceItem();
        item.setDescription(q.getDescription() != null && !q.getDescription().isBlank() ? q.getDescription() : "Electrical contracting work as per Quotation " + q.getQuotationNumber());
        item.setHsnSac("995461");
        item.setQuantity(BigDecimal.ONE);
        item.setUnit("Lot");
        item.setUnitPrice(q.getSubTotal() != null ? q.getSubTotal() : BigDecimal.ZERO);
        item.setGstRate(q.getGstPercent() != null ? q.getGstPercent() : new BigDecimal("18.00"));
        inv.addItem(item);

        inv.calculateTotals();
        invoiceRepo.save(inv);

        // Update quotation status
        q.setStatus("INVOICED");
        quotationRepo.save(q);

        ra.addFlashAttribute("message", "Quotation " + q.getQuotationNumber() + " successfully converted to GST Invoice " + inv.getInvoiceNumber());
        return "redirect:/invoices";
    }

    @GetMapping({"/print/{id}", "/{id}/print"})
    public String print(@PathVariable Long id, Model m) {
        Invoice invoice = invoiceRepo.findById(id).orElseThrow();
        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElse(new CompanyProfile());
        m.addAttribute("invoice", invoice);
        m.addAttribute("company", company);
        return "invoice-print";
    }
}
