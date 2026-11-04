package in.volta.mis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String phone;
    private String email;
    private String designation;
    
    // Wage & Compensation
    private String wageType = "DAILY_WAGE"; // DAILY_WAGE, HOURLY_WAGE or MONTHLY_SALARY
    private BigDecimal hourlyWageRate = new BigDecimal("100.00");
    private BigDecimal dailyWageRate = new BigDecimal("800.00");
    private BigDecimal monthlySalary = new BigDecimal("25000.00");
    private BigDecimal overtimeHourlyRate = new BigDecimal("150.00");
    private BigDecimal salary = BigDecimal.ZERO; // legacy/base field

    // Banking & UPI Details for Payroll
    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;
    private String upiId;

    private LocalDate joiningDate;
    private String status = "ACTIVE";

    public Staff() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getWageType() { return wageType != null ? wageType : "DAILY_WAGE"; }
    public void setWageType(String wageType) { this.wageType = wageType; }

    public BigDecimal getHourlyWageRate() { return hourlyWageRate != null ? hourlyWageRate : BigDecimal.ZERO; }
    public void setHourlyWageRate(BigDecimal hourlyWageRate) { this.hourlyWageRate = hourlyWageRate; }

    public BigDecimal getEffectiveHourlyRate() {
        if (hourlyWageRate != null && hourlyWageRate.compareTo(BigDecimal.ZERO) > 0) {
            return hourlyWageRate;
        }
        if (dailyWageRate != null && dailyWageRate.compareTo(BigDecimal.ZERO) > 0) {
            return dailyWageRate.divide(new BigDecimal("8.00"), 2, java.math.RoundingMode.HALF_UP);
        }
        return new BigDecimal("100.00");
    }

    public BigDecimal getDailyWageRate() { return dailyWageRate != null ? dailyWageRate : BigDecimal.ZERO; }
    public void setDailyWageRate(BigDecimal dailyWageRate) { this.dailyWageRate = dailyWageRate; }

    public BigDecimal getMonthlySalary() { return monthlySalary != null ? monthlySalary : BigDecimal.ZERO; }
    public void setMonthlySalary(BigDecimal monthlySalary) { this.monthlySalary = monthlySalary; }

    public BigDecimal getOvertimeHourlyRate() { return overtimeHourlyRate != null ? overtimeHourlyRate : BigDecimal.ZERO; }
    public void setOvertimeHourlyRate(BigDecimal overtimeHourlyRate) { this.overtimeHourlyRate = overtimeHourlyRate; }

    public BigDecimal getSalary() { return salary != null ? salary : BigDecimal.ZERO; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public String getEffectiveWageDisplay() {
        if ("HOURLY_WAGE".equalsIgnoreCase(wageType)) {
            return "₹ " + getEffectiveHourlyRate() + " / hr";
        }
        if ("MONTHLY_SALARY".equalsIgnoreCase(wageType)) {
            return "₹ " + monthlySalary + " / mo";
        }
        return "₹ " + dailyWageRate + " / day (₹ " + getEffectiveHourlyRate() + "/hr)";
    }

    public boolean hasOvertimeRate() {
        return overtimeHourlyRate != null && overtimeHourlyRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
