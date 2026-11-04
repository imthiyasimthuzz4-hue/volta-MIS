package in.volta.mis.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_orders")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String workOrderNumber;

    @Column(nullable = false)
    private String title;

    private String projectName;
    private String customerName;
    private String assignedStaff;
    private String priority = "MEDIUM";
    private String status = "PENDING";
    private String siteLocation;
    private LocalDate issueDate;
    private LocalDate targetDate;

    @Column(length = 2000)
    private String scopeOfWork;

    @Column(length = 1000)
    private String materialsRequired;

    @Column(length = 1000)
    private String engineerNotes;

    public Long getId() { return id; }
    public String getWorkOrderNumber() { return workOrderNumber; }
    public void setWorkOrderNumber(String workOrderNumber) { this.workOrderNumber = workOrderNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getAssignedStaff() { return assignedStaff; }
    public void setAssignedStaff(String assignedStaff) { this.assignedStaff = assignedStaff; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSiteLocation() { return siteLocation; }
    public void setSiteLocation(String siteLocation) { this.siteLocation = siteLocation; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public String getScopeOfWork() { return scopeOfWork; }
    public void setScopeOfWork(String scopeOfWork) { this.scopeOfWork = scopeOfWork; }
    public String getMaterialsRequired() { return materialsRequired; }
    public void setMaterialsRequired(String materialsRequired) { this.materialsRequired = materialsRequired; }
    public String getEngineerNotes() { return engineerNotes; }
    public void setEngineerNotes(String engineerNotes) { this.engineerNotes = engineerNotes; }
}
