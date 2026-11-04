package in.volta.mis.repository;

import in.volta.mis.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByAttendanceDateOrderByStaffNameAsc(LocalDate date);
    List<Attendance> findByAttendanceDateBetweenOrderByAttendanceDateDescStaffNameAsc(LocalDate startDate, LocalDate endDate);
    List<Attendance> findByStaffIdOrderByAttendanceDateDesc(Long staffId);
    List<Attendance> findByStaffIdAndAttendanceDateBetween(Long staffId, LocalDate startDate, LocalDate endDate);
    List<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);
    long countByAttendanceDateAndStatus(LocalDate date, String status);
    long countByAttendanceDate(LocalDate date);
}
