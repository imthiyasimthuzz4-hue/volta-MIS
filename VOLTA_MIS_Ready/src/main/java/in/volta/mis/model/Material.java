package in.volta.mis.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="materials")
public class Material {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 private String category;
 private String unit="pcs";
 private BigDecimal quantity=BigDecimal.ZERO;
 private BigDecimal unitPrice=BigDecimal.ZERO;
 private String supplier;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;}
 public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
 public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
 public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
 public String getSupplier(){return supplier;} public void setSupplier(String v){supplier=v;}
}
