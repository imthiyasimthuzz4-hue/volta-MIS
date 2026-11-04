package in.volta.mis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    private LocalDate invoiceDate;
    private LocalDate dueDate;

    @Column(nullable = false)
    private String customerName;

    private String customerGstin = "URP"; // URP = Unregistered Person
    private String customerAddress;
    private String customerPhone;
    private String projectName;

    private String placeOfSupply = "32-Kerala";
    private String supplyType = "INTRA_STATE"; // INTRA_STATE (CGST+SGST) or INTER_STATE (IGST)
    private boolean reverseCharge = false;
    private String quotationReference;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceItem> items = new ArrayList<>();

    private BigDecimal taxableAmount = BigDecimal.ZERO;
    private BigDecimal cgstAmount = BigDecimal.ZERO;
    private BigDecimal sgstAmount = BigDecimal.ZERO;
    private BigDecimal igstAmount = BigDecimal.ZERO;
    private BigDecimal totalGstAmount = BigDecimal.ZERO;
    private BigDecimal roundOff = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;

    private BigDecimal paidAmount = BigDecimal.ZERO;
    private BigDecimal balanceAmount = BigDecimal.ZERO;
    private String paymentStatus = "UNPAID"; // UNPAID, PARTIAL, PAID
    private String paymentMode = "BANK_TRANSFER"; // BANK_TRANSFER, UPI, CHEQUE, CASH

    @Column(length = 2000)
    private String notes;

    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    public void clearItems() {
        for (InvoiceItem item : items) {
            item.setInvoice(null);
        }
        items.clear();
    }

    public void calculateTotals() {
        BigDecimal sumTaxable = BigDecimal.ZERO;
        BigDecimal sumCgst = BigDecimal.ZERO;
        BigDecimal sumSgst = BigDecimal.ZERO;
        BigDecimal sumIgst = BigDecimal.ZERO;

        boolean isInterState = "INTER_STATE".equalsIgnoreCase(this.supplyType);

        for (InvoiceItem item : items) {
            BigDecimal qty = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE;
            BigDecimal rate = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal taxable = qty.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            item.setTaxableValue(taxable);

            BigDecimal gstPercent = item.getGstRate() != null ? item.getGstRate() : new BigDecimal("18.00");

            if (isInterState) {
                item.setIgstRate(gstPercent);
                BigDecimal igst = taxable.multiply(gstPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                item.setIgstAmount(igst);
                item.setCgstRate(BigDecimal.ZERO);
                item.setCgstAmount(BigDecimal.ZERO);
                item.setSgstRate(BigDecimal.ZERO);
                item.setSgstAmount(BigDecimal.ZERO);
                item.setTotalAmount(taxable.add(igst));
                sumIgst = sumIgst.add(igst);
            } else {
                BigDecimal halfGst = gstPercent.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                item.setCgstRate(halfGst);
                item.setSgstRate(halfGst);
                BigDecimal cgst = taxable.multiply(halfGst).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                BigDecimal sgst = taxable.multiply(halfGst).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                item.setCgstAmount(cgst);
                item.setSgstAmount(sgst);
                item.setIgstRate(BigDecimal.ZERO);
                item.setIgstAmount(BigDecimal.ZERO);
                item.setTotalAmount(taxable.add(cgst).add(sgst));
                sumCgst = sumCgst.add(cgst);
                sumSgst = sumSgst.add(sgst);
            }
            sumTaxable = sumTaxable.add(taxable);
        }

        this.taxableAmount = sumTaxable;
        this.cgstAmount = sumCgst;
        this.sgstAmount = sumSgst;
        this.igstAmount = sumIgst;
        this.totalGstAmount = sumCgst.add(sumSgst).add(sumIgst);

        BigDecimal exactTotal = sumTaxable.add(this.totalGstAmount);
        BigDecimal rounded = exactTotal.setScale(0, RoundingMode.HALF_UP);
        this.roundOff = rounded.subtract(exactTotal);
        this.grandTotal = rounded.setScale(2, RoundingMode.HALF_UP);

        if (this.paidAmount == null) this.paidAmount = BigDecimal.ZERO;
        this.balanceAmount = this.grandTotal.subtract(this.paidAmount);

        if (this.paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (this.paidAmount.compareTo(this.grandTotal) >= 0) {
                this.paymentStatus = "PAID";
            } else {
                this.paymentStatus = "PARTIAL";
            }
        } else {
            this.paymentStatus = "UNPAID";
        }
    }

    public String getAmountInWords() {
        if (grandTotal == null || grandTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return "Zero Rupees Only";
        }
        long amount = grandTotal.longValue();
        return convertToIndianWords(amount) + " Rupees Only";
    }

    private static String convertToIndianWords(long n) {
        if (n == 0) return "Zero";
        String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen" };
        String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety" };

        if (n < 20) return units[(int) n];
        if (n < 100) return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " + units[(int) (n % 10)] : "");
        if (n < 1000) return units[(int) (n / 100)] + " Hundred" + ((n % 100 != 0) ? " and " + convertToIndianWords(n % 100) : "");
        if (n < 100000) return convertToIndianWords(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " + convertToIndianWords(n % 1000) : "");
        if (n < 10000000) return convertToIndianWords(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " + convertToIndianWords(n % 100000) : "");
        return convertToIndianWords(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " + convertToIndianWords(n % 10000000) : "");
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerGstin() { return customerGstin; }
    public void setCustomerGstin(String customerGstin) { this.customerGstin = customerGstin; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getPlaceOfSupply() { return placeOfSupply; }
    public void setPlaceOfSupply(String placeOfSupply) { this.placeOfSupply = placeOfSupply; }
    public String getSupplyType() { return supplyType; }
    public void setSupplyType(String supplyType) { this.supplyType = supplyType; }
    public boolean isReverseCharge() { return reverseCharge; }
    public void setReverseCharge(boolean reverseCharge) { this.reverseCharge = reverseCharge; }
    public String getQuotationReference() { return quotationReference; }
    public void setQuotationReference(String quotationReference) { this.quotationReference = quotationReference; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }
    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }
    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }
    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }
    public BigDecimal getTotalGstAmount() { return totalGstAmount; }
    public void setTotalGstAmount(BigDecimal totalGstAmount) { this.totalGstAmount = totalGstAmount; }
    public BigDecimal getRoundOff() { return roundOff; }
    public void setRoundOff(BigDecimal roundOff) { this.roundOff = roundOff; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
