package in.volta.mis.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="materials")
public class Material {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String itemName;
 private String itemCode; private String unit="pcs"; private Integer quantity=0; private BigDecimal unitPrice=BigDecimal.ZERO; private String supplier;
 public Long getId(){return id;} public String getItemName(){return itemName;} public void setItemName(String v){itemName=v;}
 public String getItemCode(){return itemCode;} public void setItemCode(String v){itemCode=v;} public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
 public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;} public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
 public String getSupplier(){return supplier;} public void setSupplier(String v){supplier=v;}
}
