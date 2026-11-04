package in.volta.mis.controller;

import in.volta.mis.model.CompanyProfile;
import in.volta.mis.model.WorkOrder;
import in.volta.mis.repository.CompanyProfileRepository;
import in.volta.mis.repository.ProjectRepository;
import in.volta.mis.repository.StaffRepository;
import in.volta.mis.repository.WorkOrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/work-orders")
public class WorkOrderController {
    private final WorkOrderRepository workOrderRepo;
    private final ProjectRepository projectRepo;
    private final StaffRepository staffRepo;
    private final CompanyProfileRepository companyRepo;

    public WorkOrderController(WorkOrderRepository w, ProjectRepository p, StaffRepository s, CompanyProfileRepository c) {
        this.workOrderRepo = w;
        this.projectRepo = p;
        this.staffRepo = s;
        this.companyRepo = c;
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("workOrders", workOrderRepo.findAll());
        WorkOrder newWo = new WorkOrder();
        newWo.setIssueDate(LocalDate.now());
        newWo.setWorkOrderNumber("WO-" + LocalDate.now().getYear() + "-" + String.format("%03d", workOrderRepo.count() + 1));
        m.addAttribute("workOrder", newWo);
        m.addAttribute("projects", projectRepo.findAll());
        m.addAttribute("staffList", staffRepo.findAll());
        return "work-orders";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("workOrders", workOrderRepo.findAll());
        m.addAttribute("workOrder", workOrderRepo.findById(id).orElseThrow());
        m.addAttribute("projects", projectRepo.findAll());
        m.addAttribute("staffList", staffRepo.findAll());
        m.addAttribute("editing", true);
        return "work-orders";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute WorkOrder workOrder, RedirectAttributes ra) {
        if (workOrder.getWorkOrderNumber() == null || workOrder.getWorkOrderNumber().isBlank()) {
            workOrder.setWorkOrderNumber("WO-" + LocalDate.now().getYear() + "-" + String.format("%03d", workOrderRepo.count() + 1));
        }
        if (workOrder.getIssueDate() == null) {
            workOrder.setIssueDate(LocalDate.now());
        }
        workOrderRepo.save(workOrder);
        ra.addFlashAttribute("message", "Work Order " + workOrder.getWorkOrderNumber() + " saved successfully");
        return "redirect:/work-orders";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        workOrderRepo.deleteById(id);
        ra.addFlashAttribute("message", "Work Order deleted successfully");
        return "redirect:/work-orders";
    }

    @GetMapping({"/print/{id}", "/{id}/print"})
    public String print(@PathVariable Long id, Model m) {
        WorkOrder wo = workOrderRepo.findById(id).orElseThrow();
        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElse(new CompanyProfile());
        m.addAttribute("workOrder", wo);
        m.addAttribute("company", company);
        return "work-order-print";
    }
}
