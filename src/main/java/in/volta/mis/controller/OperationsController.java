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
public class OperationsController {

    private final StaffRepository staffRepo;
    private final MaterialRepository materialRepo;
    private final ExpenseRepository expenseRepo;

    public OperationsController(StaffRepository s, MaterialRepository m, ExpenseRepository e) {
        this.staffRepo = s;
        this.materialRepo = m;
        this.expenseRepo = e;
    }

    // Staff
    @GetMapping("/staff")
    public String staff(Model m) {
        m.addAttribute("staffList", staffRepo.findAll());
        m.addAttribute("staff", new Staff());
        return "staff";
    }

    @GetMapping("/staff/edit/{id}")
    public String editStaff(@PathVariable Long id, Model m) {
        m.addAttribute("staffList", staffRepo.findAll());
        m.addAttribute("staff", staffRepo.findById(id).orElseThrow());
        m.addAttribute("editing", true);
        return "staff";
    }

    @PostMapping("/staff/save")
    public String saveStaff(@ModelAttribute Staff staff, RedirectAttributes ra) {
        if (staff.getSalary() == null) staff.setSalary(BigDecimal.ZERO);
        if (staff.getDailyWageRate() == null) staff.setDailyWageRate(new BigDecimal("950.00"));
        if (staff.getMonthlySalary() == null) staff.setMonthlySalary(new BigDecimal("25000.00"));
        if (staff.getOvertimeHourlyRate() == null) staff.setOvertimeHourlyRate(new BigDecimal("130.00"));
        if (staff.getWageType() == null || staff.getWageType().isBlank()) staff.setWageType("DAILY_WAGE");
        if (staff.getStatus() == null || staff.getStatus().isBlank()) staff.setStatus("ACTIVE");
        staffRepo.save(staff);
        ra.addFlashAttribute("message", "Staff member saved successfully (" + staff.getEffectiveWageDisplay() + ")");
        return "redirect:/staff";
    }

    @PostMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes ra) {
        staffRepo.deleteById(id);
        ra.addFlashAttribute("message", "Staff member deleted");
        return "redirect:/staff";
    }

    // Materials & Godown Stock
    @GetMapping("/materials")
    public String materials(Model m) {
        List<Material> list = materialRepo.findAll();
        m.addAttribute("materials", list);
        m.addAttribute("material", new Material());
        BigDecimal totalStockValuation = list.stream()
                .map(mat -> mat.getQuantity().multiply(mat.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long lowStockCount = list.stream().filter(Material::isLowStock).count();
        m.addAttribute("totalStockValuation", totalStockValuation);
        m.addAttribute("lowStockCount", lowStockCount);
        return "materials";
    }

    @GetMapping("/materials/edit/{id}")
    public String editMaterial(@PathVariable Long id, Model m) {
        List<Material> list = materialRepo.findAll();
        m.addAttribute("materials", list);
        m.addAttribute("material", materialRepo.findById(id).orElseThrow());
        m.addAttribute("editing", true);
        BigDecimal totalStockValuation = list.stream()
                .map(mat -> mat.getQuantity().multiply(mat.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long lowStockCount = list.stream().filter(Material::isLowStock).count();
        m.addAttribute("totalStockValuation", totalStockValuation);
        m.addAttribute("lowStockCount", lowStockCount);
        return "materials";
    }

    @PostMapping("/materials/save")
    public String saveMaterial(@ModelAttribute Material material, RedirectAttributes ra) {
        if (material.getQuantity() == null) material.setQuantity(BigDecimal.ZERO);
        if (material.getUnitPrice() == null) material.setUnitPrice(BigDecimal.ZERO);
        if (material.getMinAlertQty() == null) material.setMinAlertQty(new BigDecimal("5"));
        if (material.getGodownLocation() == null || material.getGodownLocation().isBlank()) {
            material.setGodownLocation("Valluvambram Main Godown");
        }
        materialRepo.save(material);
        ra.addFlashAttribute("message", "Material inventory record saved successfully.");
        return "redirect:/materials";
    }

    @PostMapping("/materials/adjust-stock/{id}")
    public String adjustStock(@PathVariable Long id,
                              @RequestParam("action") String action,
                              @RequestParam("qty") BigDecimal qty,
                              @RequestParam(value = "reason", required = false) String reason,
                              RedirectAttributes ra) {
        Material mat = materialRepo.findById(id).orElseThrow();
        if ("DEDUCT".equalsIgnoreCase(action)) {
            mat.deductStock(qty);
            ra.addFlashAttribute("message", "Deducted " + qty + " " + mat.getUnit() + " of " + mat.getName() + " (" + (reason != null && !reason.isBlank() ? reason : "Site Dispatch") + "). Current Balance: " + mat.getQuantity() + " " + mat.getUnit());
        } else {
            mat.addStock(qty);
            ra.addFlashAttribute("message", "Added " + qty + " " + mat.getUnit() + " to " + mat.getName() + ". New Stock: " + mat.getQuantity() + " " + mat.getUnit());
        }
        materialRepo.save(mat);
        return "redirect:/materials";
    }

    @PostMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable Long id, RedirectAttributes ra) {
        materialRepo.deleteById(id);
        ra.addFlashAttribute("message", "Material deleted");
        return "redirect:/materials";
    }

    // Expenses
    @GetMapping("/expenses")
    public String expenses(Model m) {
        m.addAttribute("expenses", expenseRepo.findAll());
        m.addAttribute("expense", new Expense());
        return "expenses";
    }

    @GetMapping("/expenses/edit/{id}")
    public String editExpense(@PathVariable Long id, Model m) {
        m.addAttribute("expenses", expenseRepo.findAll());
        m.addAttribute("expense", expenseRepo.findById(id).orElseThrow());
        m.addAttribute("editing", true);
        return "expenses";
    }

    @PostMapping("/expenses/save")
    public String saveExpense(@ModelAttribute Expense expense, RedirectAttributes ra) {
        if (expense.getAmount() == null) expense.setAmount(BigDecimal.ZERO);
        expenseRepo.save(expense);
        ra.addFlashAttribute("message", "Expense saved successfully");
        return "redirect:/expenses";
    }

    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes ra) {
        expenseRepo.deleteById(id);
        ra.addFlashAttribute("message", "Expense deleted");
        return "redirect:/expenses";
    }
}
