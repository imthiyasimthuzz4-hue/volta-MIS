package in.volta.mis.controller;

import in.volta.mis.model.*;
import in.volta.mis.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/payroll")
public class PayrollController {

    private final StaffRepository staffRepo;
    private final AttendanceRepository attendanceRepo;
    private final SalaryTransactionRepository transactionRepo;
    private final CompanyProfileRepository companyRepo;

    public PayrollController(StaffRepository s, AttendanceRepository a, SalaryTransactionRepository t, CompanyProfileRepository c) {
        this.staffRepo = s;
        this.attendanceRepo = a;
        this.transactionRepo = t;
        this.companyRepo = c;
    }

    @GetMapping
    public String viewPayroll(@RequestParam(value = "month", required = false) String monthStr, Model model) {
        YearMonth ym = parseYearMonth(monthStr);
        String currentMonthStr = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Staff> staffList = staffRepo.findAll();
        List<Attendance> monthAttendance = attendanceRepo.findByAttendanceDateBetween(startDate, endDate);
        List<SalaryTransaction> monthTransactions = transactionRepo.findBySalaryMonth(currentMonthStr);

        List<StaffPayrollSummary> summaries = new ArrayList<>();
        BigDecimal totalGrossWages = BigDecimal.ZERO;
        BigDecimal totalAdvancesGiven = BigDecimal.ZERO;
        BigDecimal totalBonusesGiven = BigDecimal.ZERO;
        BigDecimal totalDeductionsMade = BigDecimal.ZERO;
        BigDecimal totalSalariesDisbursed = BigDecimal.ZERO;
        BigDecimal totalPendingDues = BigDecimal.ZERO;

        for (Staff s : staffList) {
            StaffPayrollSummary summary = calculateStaffSummary(s, monthAttendance, monthTransactions);
            summaries.add(summary);

            totalGrossWages = totalGrossWages.add(summary.getGrossEarnedWages());
            totalAdvancesGiven = totalAdvancesGiven.add(summary.getTotalAdvances());
            totalBonusesGiven = totalBonusesGiven.add(summary.getTotalBonuses());
            totalDeductionsMade = totalDeductionsMade.add(summary.getTotalDeductions());
            totalSalariesDisbursed = totalSalariesDisbursed.add(summary.getTotalPaidSalary());
            if (summary.getPendingBalance().compareTo(BigDecimal.ZERO) > 0) {
                totalPendingDues = totalPendingDues.add(summary.getPendingBalance());
            }
        }

        model.addAttribute("selectedMonth", currentMonthStr);
        model.addAttribute("monthDisplay", ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        model.addAttribute("summaries", summaries);
        model.addAttribute("staffList", staffList);
        model.addAttribute("recentTransactions", transactionRepo.findAllByOrderByTransactionDateDesc().stream().limit(15).toList());

        model.addAttribute("totalGrossWages", totalGrossWages);
        model.addAttribute("totalAdvancesGiven", totalAdvancesGiven);
        model.addAttribute("totalSalariesDisbursed", totalSalariesDisbursed);
        model.addAttribute("totalPendingDues", totalPendingDues);

        SalaryTransaction emptyTx = new SalaryTransaction();
        emptyTx.setSalaryMonth(currentMonthStr);
        emptyTx.setTransactionDate(LocalDate.now());
        model.addAttribute("newTransaction", emptyTx);

        return "payroll";
    }

    @GetMapping("/staff/{id}")
    public String viewStaffLedger(@PathVariable Long id,
                                  @RequestParam(value = "month", required = false) String monthStr,
                                  Model model) {
        Staff staff = staffRepo.findById(id).orElseThrow();
        YearMonth ym = parseYearMonth(monthStr);
        String currentMonthStr = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Attendance> attendances = attendanceRepo.findByStaffIdAndAttendanceDateBetween(staff.getId(), startDate, endDate);
        List<SalaryTransaction> staffTxList = transactionRepo.findByStaffIdAndSalaryMonth(staff.getId(), currentMonthStr);
        StaffPayrollSummary summary = calculateStaffSummary(staff, attendances, staffTxList);

        List<SalaryTransaction> allStaffHistory = transactionRepo.findByStaffIdOrderByTransactionDateDesc(staff.getId());

        model.addAttribute("staff", staff);
        model.addAttribute("summary", summary);
        model.addAttribute("selectedMonth", currentMonthStr);
        model.addAttribute("monthDisplay", ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        model.addAttribute("attendances", attendances);
        model.addAttribute("monthTransactions", staffTxList);
        model.addAttribute("allStaffHistory", allStaffHistory);

        SalaryTransaction emptyTx = new SalaryTransaction();
        emptyTx.setStaffId(staff.getId());
        emptyTx.setStaffName(staff.getFullName());
        emptyTx.setSalaryMonth(currentMonthStr);
        emptyTx.setTransactionDate(LocalDate.now());
        model.addAttribute("newTransaction", emptyTx);

        return "payroll-staff-ledger";
    }

    @PostMapping("/save-transaction")
    public String saveTransaction(@ModelAttribute SalaryTransaction tx, Principal principal, RedirectAttributes ra) {
        if (tx.getStaffId() != null) {
            staffRepo.findById(tx.getStaffId()).ifPresent(s -> tx.setStaffName(s.getFullName()));
        }
        if (tx.getTransactionDate() == null) {
            tx.setTransactionDate(LocalDate.now());
        }
        if (tx.getSalaryMonth() == null || tx.getSalaryMonth().isBlank()) {
            tx.setSalaryMonth(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        if (tx.getAmount() == null) {
            tx.setAmount(BigDecimal.ZERO);
        }
        tx.setRecordedBy(principal != null ? principal.getName() : "Admin");

        transactionRepo.save(tx);

        String msg;
        if ("ADVANCE_PAYMENT".equalsIgnoreCase(tx.getTransactionType())) {
            msg = "⚡ Salary Advance of ₹ " + tx.getAmount() + " recorded for " + tx.getStaffName() + " (" + tx.getPaymentMode() + "). Deducted from pending salary.";
        } else if ("SALARY_PAYMENT".equalsIgnoreCase(tx.getTransactionType())) {
            msg = "✅ Salary Payment of ₹ " + tx.getAmount() + " disbursed to " + tx.getStaffName() + " (" + tx.getPaymentMode() + "). Balance updated.";
        } else if ("BONUS_ALLOWANCE".equalsIgnoreCase(tx.getTransactionType())) {
            msg = "🎁 Site Allowance / Bonus of ₹ " + tx.getAmount() + " added for " + tx.getStaffName();
        } else {
            msg = "⚠️ Deduction of ₹ " + tx.getAmount() + " applied to " + tx.getStaffName();
        }

        ra.addFlashAttribute("message", msg);
        return "redirect:/payroll?month=" + tx.getSalaryMonth();
    }

    @PostMapping("/delete-transaction/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes ra) {
        SalaryTransaction tx = transactionRepo.findById(id).orElse(null);
        String month = tx != null ? tx.getSalaryMonth() : YearMonth.now().toString();
        if (tx != null) {
            transactionRepo.deleteById(id);
            ra.addFlashAttribute("message", "Payment transaction #" + id + " for " + tx.getStaffName() + " deleted. Balance restored.");
        }
        return "redirect:/payroll?month=" + month;
    }

    @GetMapping("/payslip/{staffId}")
    public String printPayslip(@PathVariable Long staffId,
                               @RequestParam(value = "month", required = false) String monthStr,
                               Model model) {
        Staff staff = staffRepo.findById(staffId).orElseThrow();
        YearMonth ym = parseYearMonth(monthStr);
        String currentMonthStr = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Attendance> attendances = attendanceRepo.findByStaffIdAndAttendanceDateBetween(staff.getId(), startDate, endDate);
        List<SalaryTransaction> staffTxList = transactionRepo.findByStaffIdAndSalaryMonth(staff.getId(), currentMonthStr);
        StaffPayrollSummary summary = calculateStaffSummary(staff, attendances, staffTxList);

        CompanyProfile company = companyRepo.findAll().stream().findFirst().orElseGet(() -> {
            CompanyProfile cp = new CompanyProfile();
            cp.setCompanyName("VOLTA ELECTRICAL ASSOCIATES");
            return cp;
        });

        model.addAttribute("staff", staff);
        model.addAttribute("summary", summary);
        model.addAttribute("company", company);
        model.addAttribute("monthDisplay", ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        model.addAttribute("selectedMonth", currentMonthStr);
        model.addAttribute("transactions", staffTxList);

        return "payslip-print";
    }

    private StaffPayrollSummary calculateStaffSummary(Staff s, List<Attendance> allAttendance, List<SalaryTransaction> allTx) {
        StaffPayrollSummary sum = new StaffPayrollSummary();
        sum.setStaff(s);

        List<Attendance> staffAtt = allAttendance.stream()
                .filter(a -> s.getId().equals(a.getStaffId()))
                .toList();

        long present = staffAtt.stream().filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus())).count();
        long onSite = staffAtt.stream().filter(a -> "ON_DUTY_SITE".equalsIgnoreCase(a.getStatus())).count();
        long half = staffAtt.stream().filter(a -> "HALF_DAY".equalsIgnoreCase(a.getStatus())).count();
        long absent = staffAtt.stream().filter(a -> "ABSENT".equalsIgnoreCase(a.getStatus()) || "LEAVE".equalsIgnoreCase(a.getStatus())).count();

        sum.setDaysPresent(present);
        sum.setOnSiteDays(onSite);
        sum.setHalfDays(half);
        sum.setAbsentDays(absent);

        BigDecimal effectiveDays = BigDecimal.valueOf(present + onSite)
                .add(BigDecimal.valueOf(half).multiply(new BigDecimal("0.5")));
        sum.setEffectiveWorkingDays(effectiveDays);

        BigDecimal effectiveHourly = s.getEffectiveHourlyRate();
        BigDecimal otHourlyRate = s.getOvertimeHourlyRate() != null && s.getOvertimeHourlyRate().compareTo(BigDecimal.ZERO) > 0
                ? s.getOvertimeHourlyRate()
                : effectiveHourly.multiply(new BigDecimal("1.5")).setScale(2, RoundingMode.HALF_UP);
        sum.setHourlyRate(effectiveHourly);

        BigDecimal totalRegHours = BigDecimal.ZERO;
        BigDecimal totalOtHours = BigDecimal.ZERO;
        BigDecimal totalWorkingHours = BigDecimal.ZERO;

        for (Attendance att : staffAtt) {
            BigDecimal wh = att.getWorkingHours() != null ? att.getWorkingHours() : BigDecimal.ZERO;
            if (wh.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal reg = wh.min(new BigDecimal("8.00"));
                BigDecimal ot = wh.compareTo(new BigDecimal("8.00")) > 0 ? wh.subtract(new BigDecimal("8.00")) : BigDecimal.ZERO;
                totalRegHours = totalRegHours.add(reg);
                totalOtHours = totalOtHours.add(ot);
                totalWorkingHours = totalWorkingHours.add(wh);
            } else if ("PRESENT".equalsIgnoreCase(att.getStatus()) || "ON_DUTY_SITE".equalsIgnoreCase(att.getStatus())) {
                totalRegHours = totalRegHours.add(new BigDecimal("8.00"));
                totalWorkingHours = totalWorkingHours.add(new BigDecimal("8.00"));
            } else if ("HALF_DAY".equalsIgnoreCase(att.getStatus())) {
                totalRegHours = totalRegHours.add(new BigDecimal("4.00"));
                totalWorkingHours = totalWorkingHours.add(new BigDecimal("4.00"));
            }
        }

        sum.setTotalWorkingHours(totalWorkingHours);
        sum.setTotalRegularHours(totalRegHours);
        sum.setTotalOvertimeHours(totalOtHours);

        // Calculate Gross Earned based on actual hours worked
        BigDecimal gross;
        if ("MONTHLY_SALARY".equalsIgnoreCase(s.getWageType())) {
            gross = s.getMonthlySalary();
            sum.setHourlyEarned(gross);
            sum.setOvertimeEarned(BigDecimal.ZERO);
        } else {
            // Precise hourly calculation: (Regular Hours * Hourly Rate) + (OT Hours * Overtime Rate)
            BigDecimal baseEarned = totalRegHours.multiply(effectiveHourly);
            BigDecimal otEarned = totalOtHours.multiply(otHourlyRate);
            gross = baseEarned.add(otEarned);
            sum.setHourlyEarned(baseEarned);
            sum.setOvertimeEarned(otEarned);
        }
        sum.setGrossEarnedWages(gross);

        // Transactions
        List<SalaryTransaction> staffTx = allTx.stream()
                .filter(t -> s.getId().equals(t.getStaffId()))
                .toList();

        BigDecimal advances = staffTx.stream()
                .filter(t -> "ADVANCE_PAYMENT".equalsIgnoreCase(t.getTransactionType()))
                .map(SalaryTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bonuses = staffTx.stream()
                .filter(t -> "BONUS_ALLOWANCE".equalsIgnoreCase(t.getTransactionType()))
                .map(SalaryTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deductions = staffTx.stream()
                .filter(t -> "DEDUCTION".equalsIgnoreCase(t.getTransactionType()))
                .map(SalaryTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidSalary = staffTx.stream()
                .filter(t -> "SALARY_PAYMENT".equalsIgnoreCase(t.getTransactionType()))
                .map(SalaryTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sum.setTotalAdvances(advances);
        sum.setTotalBonuses(bonuses);
        sum.setTotalDeductions(deductions);
        sum.setTotalPaidSalary(paidSalary);

        // Net Payable = Gross + Bonus - Advance - Deductions
        BigDecimal netPayable = gross.add(bonuses).subtract(advances).subtract(deductions);
        if (netPayable.compareTo(BigDecimal.ZERO) < 0) netPayable = BigDecimal.ZERO;
        sum.setNetPayable(netPayable);

        // Pending Balance = Net Payable - Paid Salary
        BigDecimal pending = netPayable.subtract(paidSalary);
        sum.setPendingBalance(pending);

        return sum;
    }

    private YearMonth parseYearMonth(String str) {
        if (str != null && !str.isBlank()) {
            try {
                return YearMonth.parse(str);
            } catch (Exception ignored) {}
        }
        return YearMonth.now();
    }
}
