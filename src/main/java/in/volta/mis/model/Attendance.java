package in.volta.mis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long staffId;

    @Column(nullable = false)
    private String staffName;

    private String designation;

    @Column(nullable = false)
    private LocalDate attendanceDate = LocalDate.now();

    private String inTime;
    private String outTime;
    private BigDecimal workingHours = BigDecimal.ZERO;
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "PRESENT"; // PRESENT, HALF_DAY, ABSENT, ON_DUTY_SITE, LEAVE

    private String workSite; // e.g. Office, Site Name, Substation
    private String locationCoordinates; // GPS Lat, Lng e.g. "11.1345, 76.1289"
    private String punchType = "ADMIN_PUNCH"; // SELF_SITE_PUNCH, ADMIN_PUNCH
    
    @Column(length = 500)
    private String photoUrl; // Site selfie or arrival photo proof

    private String remarks;
    private String recordedBy;
    private LocalDateTime recordedAt = LocalDateTime.now();

    public Attendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getInTime() { return inTime; }
    public void setInTime(String inTime) { this.inTime = inTime; }

    public String getOutTime() { return outTime; }
    public void setOutTime(String outTime) { this.outTime = outTime; }

    public BigDecimal getWorkingHours() { return workingHours != null ? workingHours : BigDecimal.ZERO; }
    public void setWorkingHours(BigDecimal workingHours) { this.workingHours = workingHours; }

    public BigDecimal getOvertimeHours() { return overtimeHours != null ? overtimeHours : BigDecimal.ZERO; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWorkSite() { return workSite; }
    public void setWorkSite(String workSite) { this.workSite = workSite; }

    public String getLocationCoordinates() { return locationCoordinates; }
    public void setLocationCoordinates(String locationCoordinates) { this.locationCoordinates = locationCoordinates; }

    public String getPunchType() { return punchType != null ? punchType : "ADMIN_PUNCH"; }
    public void setPunchType(String punchType) { this.punchType = punchType; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public boolean hasWorkingHours() {
        return workingHours != null && workingHours.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasOvertime() {
        return overtimeHours != null && overtimeHours.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public boolean hasPhoto() {
        return photoUrl != null && !photoUrl.isBlank();
    }

    public String getGoogleMapsUrl() {
        if (locationCoordinates != null && !locationCoordinates.isBlank()) {
            return "https://www.google.com/maps?q=" + locationCoordinates.trim();
        }
        return null;
    }
}
