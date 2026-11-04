package in.volta.mis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "company_profile")
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName = "VOLTA ELECTRICAL ASSOCIATES";
    private String tagline = "Govt. Licensed Electrical Contractors & Engineers";
    private String licenseNo = "EA/KER/2026/8841 (Class A)";
    private String gstin = "32AAAAA0000A1Z5";
    private String panNo = "AAAAA0000A";
    private String email = "zubairvoltavvm@gmail.com";
    private String phone = "+91 94474 12235, +91 85903 94345";
    private String address = "Kozhikode Road, Valluvambram, Malappuram, Kerala - 673642";
    private String website = "https://voltaelectricalassociates.com";
    private String instagram = "https://www.instagram.com/volta_vvm/";
    private String whatsapp = "+91 94474 12235";
    private String facebook = "https://www.facebook.com/";
    private String linkedin = "https://www.linkedin.com/";
    private String logoPath = "/images/logo.png";
    private String bankName = "State Bank of India";
    private String accountNumber = "3000123456789";
    private String ifscCode = "SBIN0000861";
    private String branch = "Valluvambram / Manjeri Branch";
    private String authorizedSignatory = "Managing Partner / Chief Engineer";

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getPanNo() { return panNo; }
    public void setPanNo(String panNo) { this.panNo = panNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }
    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getAuthorizedSignatory() { return authorizedSignatory; }
    public void setAuthorizedSignatory(String authorizedSignatory) { this.authorizedSignatory = authorizedSignatory; }
}
