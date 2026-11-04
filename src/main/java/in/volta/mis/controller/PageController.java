package in.volta.mis.controller;

import in.volta.mis.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;

@Controller
public class PageController {
    private final CustomerRepository customers;
    private final ProjectRepository projects;
    private final StaffRepository staff;
    private final MaterialRepository materials;
    private final ExpenseRepository expenses;
    private final WorkOrderRepository workOrders;
    private final QuotationRepository quotations;
    private final InvoiceRepository invoices;
    private final AppUserRepository appUsers;
    private final AttendanceRepository attendanceRepo;

    public PageController(
            CustomerRepository c,
            ProjectRepository p,
            StaffRepository s,
            MaterialRepository m,
            ExpenseRepository e,
            WorkOrderRepository w,
            QuotationRepository q,
            InvoiceRepository i,
            AppUserRepository u,
            AttendanceRepository a
    ) {
        this.customers = c;
        this.projects = p;
        this.staff = s;
        this.materials = m;
        this.expenses = e;
        this.workOrders = w;
        this.quotations = q;
        this.invoices = i;
        this.appUsers = u;
        this.attendanceRepo = a;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model m, java.security.Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        m.addAttribute("currentUsername", username);
        in.volta.mis.model.AppUser currentUser = appUsers.findByUsername(username).orElse(null);
        m.addAttribute("currentUserRole", currentUser != null ? currentUser.getRole() : "ADMIN");
        m.addAttribute("currentUserFullName", currentUser != null && currentUser.getFullName() != null ? currentUser.getFullName() : username);

        m.addAttribute("customerCount", customers.count());
        m.addAttribute("projectCount", projects.count());
        m.addAttribute("staffCount", staff.count());
        m.addAttribute("materialCount", materials.count());
        m.addAttribute("expenseCount", expenses.count());
        m.addAttribute("workOrderCount", workOrders.count());
        m.addAttribute("quotationCount", quotations.count());
        m.addAttribute("invoiceCount", invoices.count());
        m.addAttribute("totalInvoicedAmount", invoices.getTotalInvoicedAmount());
        m.addAttribute("totalGstAmount", invoices.getTotalGstCollected());

        java.time.LocalDate today = java.time.LocalDate.now();
        long todayPresent = attendanceRepo.findByAttendanceDateOrderByStaffNameAsc(today).stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()) || "ON_DUTY_SITE".equalsIgnoreCase(a.getStatus()) || "HALF_DAY".equalsIgnoreCase(a.getStatus()))
                .count();
        m.addAttribute("todayPresentCount", todayPresent);
        m.addAttribute("todayAttendanceRecords", attendanceRepo.findByAttendanceDateOrderByStaffNameAsc(today));

        BigDecimal totalExpenses = expenses.findAll().stream()
                .map(e -> e.getAmount() == null ? BigDecimal.ZERO : e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProjectValue = projects.findAll().stream()
                .map(p -> p.getAmount() == null ? BigDecimal.ZERO : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        m.addAttribute("totalExpenses", totalExpenses);
        m.addAttribute("totalProjectValue", totalProjectValue);
        m.addAttribute("recentProjects", projects.findAll());
        m.addAttribute("recentWorkOrders", workOrders.findAll());
        m.addAttribute("recentQuotations", quotations.findAll());
        m.addAttribute("recentInvoices", invoices.findAllByOrderByInvoiceDateDesc());
        return "dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model m, java.security.Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        m.addAttribute("currentUsername", username);
        in.volta.mis.model.AppUser currentUser = appUsers.findByUsername(username).orElse(null);
        m.addAttribute("currentUserRole", currentUser != null ? currentUser.getRole() : "ADMIN");
        m.addAttribute("currentUserFullName", currentUser != null && currentUser.getFullName() != null ? currentUser.getFullName() : username);

        m.addAttribute("customerCount", customers.count());
        m.addAttribute("projectCount", projects.count());
        m.addAttribute("staffCount", staff.count());
        m.addAttribute("materialCount", materials.count());
        m.addAttribute("expenseCount", expenses.count());
        m.addAttribute("workOrderCount", workOrders.count());
        m.addAttribute("quotationCount", quotations.count());
        m.addAttribute("invoiceCount", invoices.count());
        m.addAttribute("totalInvoicedAmount", invoices.getTotalInvoicedAmount());
        m.addAttribute("totalTaxableAmount", invoices.getTotalTaxableAmount());
        m.addAttribute("totalGstAmount", invoices.getTotalGstCollected());
        m.addAttribute("totalPendingBalance", invoices.getTotalPendingBalance());

        BigDecimal totalExpenses = expenses.findAll().stream()
                .map(e -> e.getAmount() == null ? BigDecimal.ZERO : e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProjectValue = projects.findAll().stream()
                .map(p -> p.getAmount() == null ? BigDecimal.ZERO : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        m.addAttribute("totalExpenses", totalExpenses);
        m.addAttribute("totalProjectValue", totalProjectValue);
        m.addAttribute("expenses", expenses.findAll());
        m.addAttribute("projects", projects.findAll());
        m.addAttribute("workOrders", workOrders.findAll());
        m.addAttribute("quotations", quotations.findAll());
        m.addAttribute("invoices", invoices.findAllByOrderByInvoiceDateDesc());
        return "reports";
    }
}
