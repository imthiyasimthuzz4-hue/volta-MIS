package in.volta.mis.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "site_material_requests")
public class SiteMaterialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String indentNumber;

    @Column(nullable = false)
    private String projectName;

    private String siteLocation;

    @Column(nullable = false)
    private String requestedBy;

    private String requestedByPhone;

    private LocalDate requestDate = LocalDate.now();

    private LocalDate requiredForDate = LocalDate.now().plusDays(1); // Default to tomorrow!

    private String priority = "NORMAL"; // NORMAL, URGENT

    private String status = "PENDING"; // PENDING, APPROVED, DISPATCHED, CANCELLED

    @Column(length = 2000)
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteMaterialRequestItem> items = new ArrayList<>();

    public SiteMaterialRequest() {}

    public void addItem(SiteMaterialRequestItem item) {
        items.add(item);
        item.setRequest(this);
    }

    public void removeItem(SiteMaterialRequestItem item) {
        items.remove(item);
        item.setRequest(null);
    }

    public void clearItems() {
        for (SiteMaterialRequestItem itm : items) {
            itm.setRequest(null);
        }
        items.clear();
    }

    public int getItemCount() {
        return items.size();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIndentNumber() { return indentNumber; }
    public void setIndentNumber(String indentNumber) { this.indentNumber = indentNumber; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getSiteLocation() { return siteLocation; }
    public void setSiteLocation(String siteLocation) { this.siteLocation = siteLocation; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getRequestedByPhone() { return requestedByPhone; }
    public void setRequestedByPhone(String requestedByPhone) { this.requestedByPhone = requestedByPhone; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getRequiredForDate() { return requiredForDate; }
    public void setRequiredForDate(LocalDate requiredForDate) { this.requiredForDate = requiredForDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<SiteMaterialRequestItem> getItems() { return items; }
    public void setItems(List<SiteMaterialRequestItem> items) { this.items = items; }
}
