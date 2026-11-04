package in.volta.mis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_transactions")
public class SalaryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long staffId;

    @Column(nullable = false)
    private String staffName;

    @Column(nullable = false)
    private LocalDate transactionDate = LocalDate.now();

    @Column(nullable = false)
    private String salaryMonth; // e.g. "2026-10"

    @Column(nullable = false)
    private String transactionType = "SALARY_PAYMENT"; // SALARY_PAYMENT, ADVANCE_PAYMENT, BONUS_ALLOWANCE, DEDUCTION

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    private String paymentMode = "CASH"; // CASH, BANK_TRANSFER, UPI_GPAY, CHEQUE
    private String referenceNo; // e.g. UTR / Voucher No.
    private String notes; // e.g. "Site advance for emergency", "Final salary settlement"
    private String recordedBy;
    private LocalDateTime createdAt = LocalDateTime.now();

    public SalaryTransaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(String salaryMonth) { this.salaryMonth = salaryMonth; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount != null ? amount : BigDecimal.ZERO; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMode() { return paymentMode != null ? paymentMode : "CASH"; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getTypeBadgeStyle() {
        if ("ADVANCE_PAYMENT".equalsIgnoreCase(transactionType)) {
            return "background:#fef3c7;color:#b45309;border:1px solid #fcd34d;";
        } else if ("SALARY_PAYMENT".equalsIgnoreCase(transactionType)) {
            return "background:#dcfce7;color:#15803d;border:1px solid #86efac;";
        } else if ("BONUS_ALLOWANCE".equalsIgnoreCase(transactionType)) {
            return "background:#dbeafe;color:#1d4ed8;border:1px solid #93c5fd;";
        } else {
            return "background:#fee2e2;color:#b91c1c;border:1px solid #fca5a5;";
        }
    }

    public String getTypeLabel() {
        if ("ADVANCE_PAYMENT".equalsIgnoreCase(transactionType)) {
            return "Salary Advance";
        } else if ("SALARY_PAYMENT".equalsIgnoreCase(transactionType)) {
            return "Salary Payout";
        } else if ("BONUS_ALLOWANCE".equalsIgnoreCase(transactionType)) {
            return "Bonus / Allowance";
        } else if ("DEDUCTION".equalsIgnoreCase(transactionType)) {
            return "Deduction / Recovery";
        }
        return transactionType;
    }
}
