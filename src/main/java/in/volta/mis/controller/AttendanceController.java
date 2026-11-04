package in.volta.mis.controller;

import in.volta.mis.model.Attendance;
import in.volta.mis.model.Staff;
import in.volta.mis.repository.AttendanceRepository;
import in.volta.mis.repository.ProjectRepository;
import in.volta.mis.repository.StaffRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;
    private final StaffRepository staffRepo;
    private final ProjectRepository projectRepo;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    public AttendanceController(AttendanceRepository a, StaffRepository s, ProjectRepository p) {
        this.attendanceRepo = a;
        this.staffRepo = s;
        this.projectRepo = p;
    }

    @GetMapping
    public String viewAttendance(@RequestParam(name = "date", required = false) String dateStr, Model model) {
        LocalDate selectedDate = LocalDate.now();
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                selectedDate = LocalDate.parse(dateStr);
            } catch (Exception ignored) {
                selectedDate = LocalDate.now();
            }
        }

        List<Attendance> records = attendanceRepo.findByAttendanceDateOrderByStaffNameAsc(selectedDate);
        List<Staff> allStaff = staffRepo.findAll();

        long totalStaffCount = allStaff.size();
        long presentCount = records.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
        long onSiteCount = records.stream().filter(r -> "ON_DUTY_SITE".equalsIgnoreCase(r.getStatus())).count();
        long halfDayCount = records.stream().filter(r -> "HALF_DAY".equalsIgnoreCase(r.getStatus())).count();
        long absentCount = records.stream().filter(r -> "ABSENT".equalsIgnoreCase(r.getStatus()) || "LEAVE".equalsIgnoreCase(r.getStatus())).count();

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("records", records);
        model.addAttribute("staffList", allStaff);
        model.addAttribute("projects", projectRepo.findAll());
        model.addAttribute("totalStaffCount", totalStaffCount);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("onSiteCount", onSiteCount);
        model.addAttribute("halfDayCount", halfDayCount);
        model.addAttribute("absentCount", absentCount);

        Attendance empty = new Attendance();
        empty.setAttendanceDate(selectedDate);
        empty.setInTime(LocalTime.now().format(TIME_FMT));
        model.addAttribute("newAttendance", empty);

        return "attendance";
    }

    @GetMapping("/self-punch")
    public String selfPunchPortal(Model model) {
        model.addAttribute("staffList", staffRepo.findAll().stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus())).toList());
        model.addAttribute("projects", projectRepo.findAll());
        model.addAttribute("todayDate", LocalDate.now());
        model.addAttribute("currentTime", LocalTime.now().format(TIME_FMT));
        return "self-punch";
    }

    @PostMapping(value = {"/self-punch", "/self-punch/submit"})
    public String submitSelfPunch(@RequestParam("staffId") Long staffId,
                                  @RequestParam(value = "punchAction", defaultValue = "IN") String punchAction,
                                  @RequestParam(value = "workSite", required = false) String workSite,
                                  @RequestParam(value = "latitude", required = false) String latitude,
                                  @RequestParam(value = "longitude", required = false) String longitude,
                                  @RequestParam(value = "remarks", required = false) String remarks,
                                  @RequestParam(value = "photoBase64", required = false) String photoBase64,
                                  RedirectAttributes ra) {
        Staff staff = staffRepo.findById(staffId).orElseThrow();
        LocalDate today = LocalDate.now();
        String timeNow = LocalTime.now().format(TIME_FMT);

        // Anti-fraud: Gallery uploads disallowed, live camera snapshot required for Punch IN
        if ("IN".equalsIgnoreCase(punchAction) && (photoBase64 == null || photoBase64.isBlank())) {
            ra.addFlashAttribute("errorMessage", "⚠️ Live Camera Photo Required! Gallery photos are strictly disallowed. You must snap a real-time live camera photo at the site to punch in.");
            return "redirect:/attendance/self-punch";
        }

        String coords = null;
        if (latitude != null && !latitude.isBlank() && longitude != null && !longitude.isBlank()) {
            coords = latitude.trim() + ", " + longitude.trim();
        }

        // Process live camera snapshot proof only
        String savedPhotoUrl = null;
        if (photoBase64 != null && !photoBase64.isBlank()) {
            try {
                String baseDir = System.getenv().getOrDefault("UPLOAD_DIR", "uploads");
                Path uploadDir = Paths.get(baseDir, "attendance");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String filePrefix = "att_live_" + staffId + "_" + System.currentTimeMillis();
                String data = photoBase64.trim();
                String ext = ".jpg";
                if (data.contains(",")) {
                    String meta = data.substring(0, data.indexOf(","));
                    if (meta.contains("png")) ext = ".png";
                    data = data.substring(data.indexOf(",") + 1);
                }
                byte[] bytes = Base64.getDecoder().decode(data);
                String filename = filePrefix + ext;
                Path dest = uploadDir.resolve(filename);
                Files.write(dest, bytes);
                savedPhotoUrl = "/uploads/attendance/" + filename;
            } catch (Exception ex) {
                System.err.println("Notice: Could not save live camera photo proof: " + ex.getMessage());
            }
        }

        Attendance att = attendanceRepo.findByAttendanceDateOrderByStaffNameAsc(today).stream()
                .filter(a -> staffId.equals(a.getStaffId()))
                .findFirst()
                .orElse(null);

        if ("IN".equalsIgnoreCase(punchAction)) {
            if (att == null) {
                att = new Attendance();
                att.setStaffId(staff.getId());
                att.setStaffName(staff.getFullName());
                att.setDesignation(staff.getDesignation());
                att.setAttendanceDate(today);
                att.setStatus("ON_DUTY_SITE");
                att.setInTime(timeNow);
                att.setWorkSite(workSite != null && !workSite.isBlank() ? workSite : "Site Assignment");
                att.setLocationCoordinates(coords);
                att.setPunchType("SELF_SITE_PUNCH");
                if (savedPhotoUrl != null) att.setPhotoUrl(savedPhotoUrl);
                att.setRemarks(remarks != null && !remarks.isBlank() ? remarks : "Self site check-in via mobile");
                att.setRecordedBy("Self (" + staff.getFullName() + ")");
                att.setRecordedAt(LocalDateTime.now());
                attendanceRepo.save(att);
            } else {
                if (workSite != null && !workSite.isBlank()) att.setWorkSite(workSite);
                if (coords != null) att.setLocationCoordinates(coords);
                if (savedPhotoUrl != null) att.setPhotoUrl(savedPhotoUrl);
                att.setPunchType("SELF_SITE_PUNCH");
                if ("ABSENT".equalsIgnoreCase(att.getStatus()) || "LEAVE".equalsIgnoreCase(att.getStatus())) att.setStatus("ON_DUTY_SITE");
                attendanceRepo.save(att);
            }
            String photoMsg = (savedPhotoUrl != null || att.hasPhoto()) ? " [📸 Photo Proof Verified]" : "";
            ra.addFlashAttribute("message", "📍 Site arrival & GPS location logged for " + staff.getFullName() + " at " + att.getWorkSite() + " (" + timeNow + ")" + photoMsg);
        } else {
            // Punch OUT
            if (att == null) {
                att = new Attendance();
                att.setStaffId(staff.getId());
                att.setStaffName(staff.getFullName());
                att.setDesignation(staff.getDesignation());
                att.setAttendanceDate(today);
                att.setStatus("ON_DUTY_SITE");
                att.setInTime("09:00 AM");
                att.setOutTime(timeNow);
                att.setWorkSite(workSite != null && !workSite.isBlank() ? workSite : "Site Assignment");
                att.setLocationCoordinates(coords);
                att.setPunchType("SELF_SITE_PUNCH");
                if (savedPhotoUrl != null) att.setPhotoUrl(savedPhotoUrl);
                att.setRecordedBy("Self (" + staff.getFullName() + ")");
                att.setRecordedAt(LocalDateTime.now());
            } else {
                att.setOutTime(timeNow);
                if (coords != null) att.setLocationCoordinates(coords);
                if (savedPhotoUrl != null) att.setPhotoUrl(savedPhotoUrl);
                att.setPunchType("SELF_SITE_PUNCH");
            }

            // Calculate working hours & overtime
            try {
                if (att.getInTime() != null) {
                    LocalTime in = LocalTime.parse(att.getInTime(), TIME_FMT);
                    LocalTime out = LocalTime.parse(timeNow, TIME_FMT);
                    long minutes = Duration.between(in, out).toMinutes();
                    if (minutes > 0) {
                        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                        att.setWorkingHours(hours);
                        if (hours.compareTo(new BigDecimal("8.00")) > 0) {
                            att.setOvertimeHours(hours.subtract(new BigDecimal("8.00")));
                        }
                    }
                }
            } catch (Exception ignored) {}

            attendanceRepo.save(att);
            ra.addFlashAttribute("message", "🏁 Checked Out recorded at " + timeNow + " (" + (att.getWorkingHours() != null ? att.getWorkingHours() + " hrs" : "") + ")");
        }

        return "redirect:/attendance/self-punch";
    }

    @PostMapping("/quick-check-in")
    public String quickCheckIn(@RequestParam("staffId") Long staffId,
                               @RequestParam(value = "workSite", required = false) String workSite,
                               @RequestParam(value = "status", required = false, defaultValue = "PRESENT") String status,
                               Principal principal,
                               RedirectAttributes ra) {
        Staff staff = staffRepo.findById(staffId).orElseThrow();
        LocalDate today = LocalDate.now();

        Attendance att = attendanceRepo.findByAttendanceDateOrderByStaffNameAsc(today).stream()
                .filter(a -> staffId.equals(a.getStaffId()))
                .findFirst()
                .orElse(new Attendance());

        att.setStaffId(staff.getId());
        att.setStaffName(staff.getFullName());
        att.setDesignation(staff.getDesignation());
        att.setAttendanceDate(today);
        att.setStatus(status);
        att.setInTime(LocalTime.now().format(TIME_FMT));
        att.setWorkSite(workSite != null && !workSite.isBlank() ? workSite : "Valluvambram Office / Godown");
        att.setPunchType("ADMIN_PUNCH");
        att.setRecordedBy(principal != null ? principal.getName() : "System");
        att.setRecordedAt(LocalDateTime.now());

        attendanceRepo.save(att);
        ra.addFlashAttribute("message", "⚡ Attendance recorded for " + staff.getFullName() + " (In-Time: " + att.getInTime() + ", Site: " + att.getWorkSite() + ")");
        return "redirect:/attendance?date=" + today;
    }

    @PostMapping("/quick-check-out/{id}")
    public String quickCheckOut(@PathVariable Long id, RedirectAttributes ra) {
        Attendance att = attendanceRepo.findById(id).orElseThrow();
        String outTimeStr = LocalTime.now().format(TIME_FMT);
        att.setOutTime(outTimeStr);

        try {
            if (att.getInTime() != null) {
                LocalTime in = LocalTime.parse(att.getInTime(), TIME_FMT);
                LocalTime out = LocalTime.parse(outTimeStr, TIME_FMT);
                long minutes = Duration.between(in, out).toMinutes();
                if (minutes > 0) {
                    BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                    att.setWorkingHours(hours);
                    if (hours.compareTo(new BigDecimal("8.00")) > 0) {
                        att.setOvertimeHours(hours.subtract(new BigDecimal("8.00")));
                    }
                }
            }
        } catch (Exception ignored) {}

        attendanceRepo.save(att);
        ra.addFlashAttribute("message", "🏁 Check-out recorded for " + att.getStaffName() + " (" + outTimeStr + (att.getWorkingHours() != null ? ", Total: " + att.getWorkingHours() + " hrs" : "") + ")");
        return "redirect:/attendance?date=" + att.getAttendanceDate();
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Attendance attendance, Principal principal, RedirectAttributes ra) {
        if (attendance.getStaffId() != null) {
            staffRepo.findById(attendance.getStaffId()).ifPresent(s -> {
                attendance.setStaffName(s.getFullName());
                attendance.setDesignation(s.getDesignation());
            });
        }
        if (attendance.getAttendanceDate() == null) {
            attendance.setAttendanceDate(LocalDate.now());
        }
        if (attendance.getRecordedBy() == null || attendance.getRecordedBy().isBlank()) {
            attendance.setRecordedBy(principal != null ? principal.getName() : "Admin");
        }
        attendanceRepo.save(attendance);
        ra.addFlashAttribute("message", "Attendance record saved successfully.");
        return "redirect:/attendance?date=" + attendance.getAttendanceDate();
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Attendance att = attendanceRepo.findById(id).orElse(null);
        LocalDate date = att != null ? att.getAttendanceDate() : LocalDate.now();
        attendanceRepo.deleteById(id);
        ra.addFlashAttribute("message", "Attendance record deleted successfully.");
        return "redirect:/attendance?date=" + date;
    }
}
