package in.volta.mis.controller;

import in.volta.mis.model.AppUser;
import in.volta.mis.repository.AppUserRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {

    private final AppUserRepository appUserRepository;

    public GlobalModelAttributes(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @ModelAttribute
    public void populateGlobalUser(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("currentUsername", username);
            AppUser u = appUserRepository.findByUsername(username).orElse(null);
            String role = (u != null && u.getRole() != null) ? u.getRole().toUpperCase().trim() : "ADMIN";
            String fullName = (u != null && u.getFullName() != null && !u.getFullName().isBlank()) ? u.getFullName() : username;

            model.addAttribute("currentUserRole", role);
            model.addAttribute("currentUserFullName", fullName);

            boolean isAdmin = "ADMIN".equals(role);
            boolean isEngineer = "ENGINEER".equals(role);
            boolean isAccountant = "ACCOUNTANT".equals(role);
            boolean isViewer = "VIEWER".equals(role);

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isEngineer", isEngineer);
            model.addAttribute("isAccountant", isAccountant);
            model.addAttribute("isViewer", isViewer);

            // Module Navigation Visibility
            model.addAttribute("canAccessUsers", isAdmin);
            model.addAttribute("canAccessCompanySettings", isAdmin);
            model.addAttribute("canAccessExpenses", isAdmin || isAccountant);
            model.addAttribute("canAccessInvoices", isAdmin || isAccountant || isViewer);
            model.addAttribute("canAccessPayroll", isAdmin || isAccountant || isViewer);
            model.addAttribute("canAccessMaterials", isAdmin || isEngineer || isViewer);
            model.addAttribute("canAccessProjects", true);
            model.addAttribute("canAccessWorkOrders", true);
            model.addAttribute("canAccessQuotations", true);
            model.addAttribute("canAccessCustomers", true);
            model.addAttribute("canAccessStaff", true);
            model.addAttribute("canAccessAttendance", true);
            model.addAttribute("canAccessReports", true);

            // Mutation Permissions
            model.addAttribute("canManageFinancials", isAdmin || isAccountant);
            model.addAttribute("canManageOperations", isAdmin || isEngineer || isAccountant);
            model.addAttribute("canDelete", isAdmin);
            model.addAttribute("isReadOnly", isViewer);
        }
    }
}
