package in.volta.mis.config;

import in.volta.mis.model.*;
import in.volta.mis.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class InitialAdmin {

    @Bean
    CommandLineRunner seedInitialData(
            AppUserRepository users,
            PasswordEncoder encoder,
            CompanyProfileRepository companyRepo,
            StaffRepository staffRepo,
            MaterialRepository materialRepo,
            WorkOrderRepository workOrderRepo,
            QuotationRepository quotationRepo,
            InvoiceRepository invoiceRepo,
            AttendanceRepository attendanceRepo,
            SalaryTransactionRepository salaryTxRepo
    ) {
        return args -> {
            // Seed RBAC Users (Admin, Engineer, Accountant, Viewer)
            if (users.findByUsername("admin").isEmpty()) {
                AppUser u = new AppUser();
                u.setUsername("admin");
                u.setPassword(encoder.encode("Admin@12345"));
                u.setFullName("Er. Zubair (Managing Director)");
                u.setEmail("zubairvoltavvm@gmail.com");
                u.setPhone("+91 94474 12235");
                u.setRole("ADMIN");
                u.setEnabled(true);
                users.save(u);
            } else {
                AppUser u = users.findByUsername("admin").get();
                if (u.getFullName() == null || u.getFullName().isBlank()) {
                    u.setFullName("Er. Zubair (Managing Director)");
                    u.setEmail("zubairvoltavvm@gmail.com");
                    u.setPhone("+91 94474 12235");
                    users.save(u);
                }
            }

            if (users.findByUsername("engineer").isEmpty()) {
                AppUser u = new AppUser();
                u.setUsername("engineer");
                u.setPassword(encoder.encode("Engineer@123"));
                u.setFullName("Arun Kumar K (Site Engineer)");
                u.setEmail("arun@voltaelectricals.in");
                u.setPhone("+91 98471 23456");
                u.setRole("ENGINEER");
                u.setEnabled(true);
                users.save(u);
            }

            if (users.findByUsername("accountant").isEmpty()) {
                AppUser u = new AppUser();
                u.setUsername("accountant");
                u.setPassword(encoder.encode("Account@123"));
                u.setFullName("Kiran V (Chief Accountant)");
                u.setEmail("accounts@voltaelectricals.in");
                u.setPhone("+91 85903 94345");
                u.setRole("ACCOUNTANT");
                u.setEnabled(true);
                users.save(u);
            }

            if (users.findByUsername("viewer").isEmpty()) {
                AppUser u = new AppUser();
                u.setUsername("viewer");
                u.setPassword(encoder.encode("Viewer@123"));
                u.setFullName("Auditor / Guest Viewer");
                u.setEmail("auditor@voltaelectricals.in");
                u.setPhone("+91 94470 00000");
                u.setRole("VIEWER");
                u.setEnabled(true);
                users.save(u);
            }

            // Company Profile
            CompanyProfile cp = companyRepo.findAll().stream().findFirst().orElse(null);
            if (cp == null) {
                cp = new CompanyProfile();
            }
            if (cp.getAddress() == null || cp.getAddress().contains("Kozhikode, Kerala - 673001")) {
                cp.setAddress("Kozhikode Road, Valluvambram, Malappuram, Kerala - 673642");
            }
            if (cp.getEmail() == null || cp.getEmail().contains("contact@voltaelectricals.in")) {
                cp.setEmail("zubairvoltavvm@gmail.com");
            }
            if (cp.getPhone() == null || cp.getPhone().contains("94470 00000")) {
                cp.setPhone("+91 94474 12235, +91 85903 94345");
            }
            if (cp.getTagline() == null || cp.getTagline().contains("Engineering Consultants")) {
                cp.setTagline("Govt. Licensed Electrical Contractors & Engineers");
            }
            if (cp.getBranch() == null || cp.getBranch().contains("Kozhikode Main Branch")) {
                cp.setBranch("Valluvambram / Manjeri Branch");
            }
            if (cp.getWebsite() == null || cp.getWebsite().isBlank()) {
                cp.setWebsite("https://voltaelectricalassociates.com");
            }
            if (cp.getInstagram() == null || cp.getInstagram().isBlank()) {
                cp.setInstagram("https://www.instagram.com/volta_vvm/");
            }
            if (cp.getWhatsapp() == null || cp.getWhatsapp().isBlank()) {
                cp.setWhatsapp("+91 94474 12235");
            }
            if (cp.getFacebook() == null || cp.getFacebook().isBlank()) {
                cp.setFacebook("https://www.facebook.com/");
            }
            if (cp.getLinkedin() == null || cp.getLinkedin().isBlank()) {
                cp.setLinkedin("https://www.linkedin.com/");
            }
            if (cp.getLogoPath() == null || cp.getLogoPath().isBlank()) {
                cp.setLogoPath("/images/logo.png");
            }
            companyRepo.save(cp);

            // Seed Staff if empty
            if (staffRepo.count() == 0) {
                Staff s1 = new Staff();
                s1.setFullName("Arun Kumar K");
                s1.setDesignation("Senior Electrical Engineer");
                s1.setPhone("+91 98471 23456");
                s1.setEmail("arun@voltaelectricals.in");
                s1.setSalary(new BigDecimal("45000.00"));
                s1.setJoiningDate(LocalDate.of(2025, 4, 1));
                s1.setStatus("ACTIVE");
                staffRepo.save(s1);

                Staff s2 = new Staff();
                s2.setFullName("Muhammed Shafi");
                s2.setDesignation("Site Supervisor / Wireman");
                s2.setPhone("+91 97450 98765");
                s2.setEmail("shafi@voltaelectricals.in");
                s2.setSalary(new BigDecimal("32000.00"));
                s2.setJoiningDate(LocalDate.of(2025, 7, 15));
                s2.setStatus("ACTIVE");
                staffRepo.save(s2);
            }

            // Seed Materials if empty
            if (materialRepo.count() == 0) {
                Material m1 = new Material();
                m1.setName("Finolex 4.0 sqmm Multi-Strand Copper Wire (Red/Black)");
                m1.setCategory("Cables & Wires");
                m1.setUnit("Rolls (90m)");
                m1.setQuantity(new BigDecimal("25.0"));
                m1.setUnitPrice(new BigDecimal("3450.00"));
                m1.setSupplier("National Electrical Agencies, Kozhikode");
                materialRepo.save(m1);

                Material m2 = new Material();
                m2.setName("Schneider Acti9 32A Triple Pole C-Curve MCB");
                m2.setCategory("Switchgear & Protection");
                m2.setUnit("Nos");
                m2.setQuantity(new BigDecimal("40.0"));
                m2.setUnitPrice(new BigDecimal("1250.00"));
                m2.setSupplier("ABB & Schneider Regional Distributor");
                materialRepo.save(m2);

                Material m3 = new Material();
                m3.setName("25x3 mm Copper Earthing Strip / Tape");
                m3.setCategory("Earthing & Lightning");
                m3.setUnit("Meters");
                m3.setQuantity(new BigDecimal("120.0"));
                m3.setUnitPrice(new BigDecimal("480.00"));
                m3.setSupplier("Kerala Copper Products");
                materialRepo.save(m3);
            }

            // Seed Work Orders if empty
            if (workOrderRepo.count() == 0) {
                WorkOrder wo1 = new WorkOrder();
                wo1.setWorkOrderNumber("WO-2026-001");
                wo1.setTitle("Substation Transformer Cabling & Earth Pit Installation");
                wo1.setProjectName("VOLTA Electrical Installation");
                wo1.setCustomerName("VOLTA Test Customer");
                wo1.setAssignedStaff("Arun Kumar K");
                wo1.setPriority("HIGH");
                wo1.setStatus("IN_PROGRESS");
                wo1.setSiteLocation("Industrial Estate, Kozhikode");
                wo1.setIssueDate(LocalDate.now().minusDays(5));
                wo1.setTargetDate(LocalDate.now().plusDays(10));
                wo1.setScopeOfWork("Laying 3.5C x 240 sqmm XLPE Armoured Cable from 11kV Substation to Main Panel. Construction of 4 Chemical Earthing Pits.");
                wo1.setMaterialsRequired("240 sqmm Cable (60m), 4 Chemical Earthing Electrodes, 25x3 Copper Strip.");
                wo1.setEngineerNotes("Megger insulation testing to be witnessed by KSEB Electrical Inspector.");
                workOrderRepo.save(wo1);

                WorkOrder wo2 = new WorkOrder();
                wo2.setWorkOrderNumber("WO-2026-002");
                wo2.setTitle("Commercial Mall DB Dressing & Circuit Testing");
                wo2.setProjectName("Default project");
                wo2.setCustomerName("VOLTA Test Customer");
                wo2.setAssignedStaff("Muhammed Shafi");
                wo2.setPriority("MEDIUM");
                wo2.setStatus("PENDING");
                wo2.setSiteLocation("Commercial Complex, Calicut Beach");
                wo2.setIssueDate(LocalDate.now().minusDays(1));
                wo2.setTargetDate(LocalDate.now().plusDays(15));
                wo2.setScopeOfWork("Complete DB dressing for 8 Distribution Boards, phase balancing and neutral integrity test.");
                wo2.setMaterialsRequired("MCBs, RCCBs, Cable Ties, Wire ferrules.");
                wo2.setEngineerNotes("Schedule work after mall operational hours (night shift).");
                workOrderRepo.save(wo2);
            }

            // Seed Quotations if empty
            if (quotationRepo.count() == 0) {
                Quotation q1 = new Quotation();
                q1.setQuotationNumber("QT-2026-101");
                q1.setCustomerName("VOLTA Test Customer");
                q1.setProjectName("VOLTA Electrical Installation");
                q1.setQuotationDate(LocalDate.now().minusDays(3));
                q1.setValidUntil(LocalDate.now().plusDays(27));
                q1.setSubTotal(new BigDecimal("127118.64"));
                q1.setGstPercent(new BigDecimal("18.00"));
                q1.setGstAmount(new BigDecimal("22881.36"));
                q1.setGrandTotal(new BigDecimal("150000.00"));
                q1.setStatus("APPROVED");
                q1.setDescription("Supply, installation, testing and commissioning of 11kV Substation indoor switchgear and internal electrical wiring.");
                q1.setPaymentTerms("50% Mobilization advance, 40% against supply of materials, 10% on KSEB energization.");
                quotationRepo.save(q1);
            }

            // Seed GST Tax Invoices if empty
            if (invoiceRepo.count() == 0) {
                Invoice inv = new Invoice();
                inv.setInvoiceNumber("INV-2026-001");
                inv.setInvoiceDate(LocalDate.now().minusDays(2));
                inv.setDueDate(LocalDate.now().plusDays(13));
                inv.setCustomerName("VOLTA Test Customer");
                inv.setCustomerGstin("32ABCDE1234F1Z5");
                inv.setCustomerAddress("Commercial Complex, Calicut Beach, Kozhikode, Kerala - 673032");
                inv.setCustomerPhone("+91 98470 12345");
                inv.setProjectName("VOLTA Electrical Installation");
                inv.setPlaceOfSupply("32-Kerala");
                inv.setSupplyType("INTRA_STATE");
                inv.setQuotationReference("QT-2026-101");
                inv.setPaymentMode("BANK_TRANSFER");
                inv.setNotes("1. Goods once sold will not be taken back.\n2. Payment via RTGS/NEFT to our State Bank of India account.\n3. All electrical works carry standard 1-year workmanship warranty.");

                InvoiceItem it1 = new InvoiceItem();
                it1.setDescription("Supply, Installation, Testing & Commissioning of 11kV Substation Switchgear");
                it1.setHsnSac("995461");
                it1.setQuantity(new BigDecimal("1.0"));
                it1.setUnit("Lot");
                it1.setUnitPrice(new BigDecimal("120000.00"));
                it1.setGstRate(new BigDecimal("18.00"));
                inv.addItem(it1);

                InvoiceItem it2 = new InvoiceItem();
                it2.setDescription("3.5C x 240 sqmm XLPE Aluminium Armoured HT Power Cable Laying & Termination");
                it2.setHsnSac("8544");
                it2.setQuantity(new BigDecimal("60.0"));
                it2.setUnit("Meters");
                it2.setUnitPrice(new BigDecimal("850.00"));
                it2.setGstRate(new BigDecimal("18.00"));
                inv.addItem(it2);

                inv.setPaidAmount(new BigDecimal("100000.00"));
                inv.calculateTotals();
                invoiceRepo.save(inv);
            }

            // 1. Clear old dummy transactions & attendance to provide clean Zero Balance test state
            salaryTxRepo.deleteAll();
            attendanceRepo.deleteAll();

            // 2. Setup exactly the 2 Employees with clean fixed hourly & daily wage rates
            List<Staff> existingStaff = staffRepo.findAll();
            Staff s1 = null;
            Staff s2 = null;

            for (Staff st : existingStaff) {
                if (st.getFullName() != null && st.getFullName().toLowerCase().contains("arun")) {
                    s1 = st;
                } else if (st.getFullName() != null && st.getFullName().toLowerCase().contains("shafi")) {
                    s2 = st;
                } else {
                    // Remove extra staff to ensure exactly 2 employees remain for testing
                    staffRepo.delete(st);
                }
            }

            if (s1 == null) {
                s1 = new Staff();
                s1.setFullName("Arun Kumar K");
                s1.setJoiningDate(LocalDate.of(2025, 4, 1));
            }
            s1.setDesignation("Senior Electrical Engineer / Site Supervisor");
            s1.setPhone("+91 98471 23456");
            s1.setEmail("arun@voltaelectricals.in");
            s1.setWageType("HOURLY_WAGE");
            s1.setHourlyWageRate(new BigDecimal("150.00")); // ₹ 150 / hr
            s1.setDailyWageRate(new BigDecimal("1200.00")); // ₹ 1,200 / 8 hrs
            s1.setOvertimeHourlyRate(new BigDecimal("200.00")); // ₹ 200 / hr
            s1.setBankName("Federal Bank, Valluvambram Branch");
            s1.setBankAccountNumber("12340100987654");
            s1.setIfscCode("FDRL0001234");
            s1.setUpiId("9847123456@okaxis");
            s1.setStatus("ACTIVE");
            staffRepo.save(s1);

            if (s2 == null) {
                s2 = new Staff();
                s2.setFullName("Muhammed Shafi");
                s2.setJoiningDate(LocalDate.of(2025, 7, 15));
            }
            s2.setDesignation("Site Wireman / Electrician");
            s2.setPhone("+91 97450 98765");
            s2.setEmail("shafi@voltaelectricals.in");
            s2.setWageType("HOURLY_WAGE");
            s2.setHourlyWageRate(new BigDecimal("100.00")); // ₹ 100 / hr
            s2.setDailyWageRate(new BigDecimal("800.00")); // ₹ 800 / 8 hrs
            s2.setOvertimeHourlyRate(new BigDecimal("150.00")); // ₹ 150 / hr
            s2.setBankName("State Bank of India, Manjeri Branch");
            s2.setBankAccountNumber("98760100123456");
            s2.setIfscCode("SBIN0000854");
            s2.setUpiId("9745098765@okaxis");
            s2.setStatus("ACTIVE");
            staffRepo.save(s2);
        };
    }
}
