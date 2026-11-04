package in.volta.mis.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="staff")
public class Staff {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String fullName;
 private String phone; private String jobTitle; private String email; private BigDecimal salary=BigDecimal.ZERO; private String status="ACTIVE";
 public Long getId(){return id;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getJobTitle(){return jobTitle;} public void setJobTitle(String v){jobTitle=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;} public BigDecimal getSalary(){return salary;} public void setSalary(BigDecimal v){salary=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
