package in.volta.mis.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name="staff")
public class Staff {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String fullName;
 private String phone;
 private String email;
 private String designation;
 private BigDecimal salary=BigDecimal.ZERO;
 private LocalDate joiningDate;
 private String status="ACTIVE";
 public Long getId(){return id;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getDesignation(){return designation;} public void setDesignation(String v){designation=v;}
 public BigDecimal getSalary(){return salary;} public void setSalary(BigDecimal v){salary=v;}
 public LocalDate getJoiningDate(){return joiningDate;} public void setJoiningDate(LocalDate v){joiningDate=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
