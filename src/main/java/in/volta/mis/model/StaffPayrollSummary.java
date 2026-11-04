package in.volta.mis.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StaffPayrollSummary {

    private Staff staff;
    private long daysPresent;
    private long onSiteDays;
    private long halfDays;
    private long absentDays;
    private BigDecimal effectiveWorkingDays = BigDecimal.ZERO;
    private BigDecimal totalWorkingHours = BigDecimal.ZERO;
    private BigDecimal totalRegularHours = BigDecimal.ZERO;
    private BigDecimal totalOvertimeHours = BigDecimal.ZERO;
    private BigDecimal hourlyRate = BigDecimal.ZERO;
    private BigDecimal hourlyEarned = BigDecimal.ZERO;
    private BigDecimal overtimeEarned = BigDecimal.ZERO;

    private BigDecimal grossEarnedWages = BigDecimal.ZERO;
    private BigDecimal totalAdvances = BigDecimal.ZERO;
    private BigDecimal totalBonuses = BigDecimal.ZERO;
    private BigDecimal totalDeductions = BigDecimal.ZERO;
    private BigDecimal totalPaidSalary = BigDecimal.ZERO;
    private BigDecimal netPayable = BigDecimal.ZERO;
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    public StaffPayrollSummary() {}

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public long getDaysPresent() { return daysPresent; }
    public void setDaysPresent(long daysPresent) { this.daysPresent = daysPresent; }

    public long getOnSiteDays() { return onSiteDays; }
    public void setOnSiteDays(long onSiteDays) { this.onSiteDays = onSiteDays; }

    public long getHalfDays() { return halfDays; }
    public void setHalfDays(long halfDays) { this.halfDays = halfDays; }

    public long getAbsentDays() { return absentDays; }
    public void setAbsentDays(long absentDays) { this.absentDays = absentDays; }

    public BigDecimal getEffectiveWorkingDays() { return effectiveWorkingDays; }
    public void setEffectiveWorkingDays(BigDecimal effectiveWorkingDays) { this.effectiveWorkingDays = effectiveWorkingDays; }

    public BigDecimal getTotalOvertimeHours() { return totalOvertimeHours; }
    public void setTotalOvertimeHours(BigDecimal totalOvertimeHours) { this.totalOvertimeHours = totalOvertimeHours; }

    public BigDecimal getTotalWorkingHours() { return totalWorkingHours != null ? totalWorkingHours : BigDecimal.ZERO; }
    public void setTotalWorkingHours(BigDecimal totalWorkingHours) { this.totalWorkingHours = totalWorkingHours; }

    public BigDecimal getTotalRegularHours() { return totalRegularHours != null ? totalRegularHours : BigDecimal.ZERO; }
    public void setTotalRegularHours(BigDecimal totalRegularHours) { this.totalRegularHours = totalRegularHours; }

    public BigDecimal getHourlyRate() { return hourlyRate != null ? hourlyRate : BigDecimal.ZERO; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public BigDecimal getHourlyEarned() { return hourlyEarned != null ? hourlyEarned : BigDecimal.ZERO; }
    public void setHourlyEarned(BigDecimal hourlyEarned) { this.hourlyEarned = hourlyEarned; }

    public BigDecimal getOvertimeEarned() { return overtimeEarned != null ? overtimeEarned : BigDecimal.ZERO; }
    public void setOvertimeEarned(BigDecimal overtimeEarned) { this.overtimeEarned = overtimeEarned; }

    public BigDecimal getGrossEarnedWages() { return grossEarnedWages; }
    public void setGrossEarnedWages(BigDecimal grossEarnedWages) { this.grossEarnedWages = grossEarnedWages; }

    public BigDecimal getTotalAdvances() { return totalAdvances; }
    public void setTotalAdvances(BigDecimal totalAdvances) { this.totalAdvances = totalAdvances; }

    public BigDecimal getTotalBonuses() { return totalBonuses; }
    public void setTotalBonuses(BigDecimal totalBonuses) { this.totalBonuses = totalBonuses; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getTotalPaidSalary() { return totalPaidSalary; }
    public void setTotalPaidSalary(BigDecimal totalPaidSalary) { this.totalPaidSalary = totalPaidSalary; }

    public BigDecimal getNetPayable() { return netPayable; }
    public void setNetPayable(BigDecimal netPayable) { this.netPayable = netPayable; }

    public BigDecimal getPendingBalance() { return pendingBalance; }
    public void setPendingBalance(BigDecimal pendingBalance) { this.pendingBalance = pendingBalance; }

    public boolean isFullySettled() {
        return pendingBalance != null && pendingBalance.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean hasPendingSalary() {
        return pendingBalance != null && pendingBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasOvertime() {
        return totalOvertimeHours != null && totalOvertimeHours.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getStatusBadge() {
        if (isFullySettled()) {
            return "background:#dcfce7;color:#15803d;border:1px solid #86efac;";
        }
        return "background:#fee2e2;color:#b91c1c;border:1px solid #fca5a5;";
    }
}
