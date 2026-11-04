package in.volta.mis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;
    private String unit = "pcs";
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private String supplier;

    private String hsnSac = "8544"; // Default electrical cables / fittings HSN
    private BigDecimal minAlertQty = new BigDecimal("5");
    private String godownLocation = "Valluvambram Main Godown";

    public Material() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getQuantity() { return quantity != null ? quantity : BigDecimal.ZERO; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice != null ? unitPrice : BigDecimal.ZERO; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getHsnSac() { return hsnSac; }
    public void setHsnSac(String hsnSac) { this.hsnSac = hsnSac; }

    public BigDecimal getMinAlertQty() { return minAlertQty != null ? minAlertQty : BigDecimal.ZERO; }
    public void setMinAlertQty(BigDecimal minAlertQty) { this.minAlertQty = minAlertQty; }

    public String getGodownLocation() { return godownLocation; }
    public void setGodownLocation(String godownLocation) { this.godownLocation = godownLocation; }

    public BigDecimal getTotalStockValue() {
        return getQuantity().multiply(getUnitPrice());
    }

    public boolean isLowStock() {
        return getQuantity().compareTo(getMinAlertQty()) <= 0;
    }

    public void deductStock(BigDecimal qty) {
        if (qty == null) return;
        BigDecimal current = getQuantity();
        BigDecimal remaining = current.subtract(qty);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }
        setQuantity(remaining);
    }

    public void addStock(BigDecimal qty) {
        if (qty == null) return;
        setQuantity(getQuantity().add(qty));
    }
}
