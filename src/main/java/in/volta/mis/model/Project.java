package in.volta.mis.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="projects")
public class Project {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String projectName; private String customerName; private String location; private LocalDate startDate; private LocalDate endDate; private String status="PLANNED"; private BigDecimal amount=BigDecimal.ZERO;
 public Long getId(){return id;} public String getProjectName(){return projectName;} public void setProjectName(String v){projectName=v;}
 public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;} public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
}
