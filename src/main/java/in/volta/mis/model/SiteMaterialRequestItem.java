package in.volta.mis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "site_material_request_items")
public class SiteMaterialRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private SiteMaterialRequest request;

    private Long materialId; // Optional link to central Godown Material

    @Column(nullable = false)
    private String materialName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;

    private String unit = "Nos";

    private String notes;

    public SiteMaterialRequestItem() {}

    public SiteMaterialRequestItem(String materialName, BigDecimal quantity, String unit) {
        this.materialName = materialName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SiteMaterialRequest getRequest() { return request; }
    public void setRequest(SiteMaterialRequest request) { this.request = request; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
