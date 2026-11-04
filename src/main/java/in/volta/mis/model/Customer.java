package in.volta.mis.model;
import jakarta.persistence.*;
@Entity @Table(name="customers")
public class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 private String phone; private String email; private String address; private String gstin;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getAddress(){return address;} public void setAddress(String v){address=v;}
 public String getGstin(){return gstin;} public void setGstin(String v){gstin=v;}
}
