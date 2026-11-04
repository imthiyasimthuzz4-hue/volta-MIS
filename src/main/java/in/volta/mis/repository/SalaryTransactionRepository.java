package in.volta.mis.repository;

import in.volta.mis.model.SalaryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryTransactionRepository extends JpaRepository<SalaryTransaction, Long> {

    List<SalaryTransaction> findByStaffId(Long staffId);

    List<SalaryTransaction> findByStaffIdOrderByTransactionDateDesc(Long staffId);

    List<SalaryTransaction> findBySalaryMonth(String salaryMonth);

    List<SalaryTransaction> findByStaffIdAndSalaryMonth(Long staffId, String salaryMonth);

    List<SalaryTransaction> findAllByOrderByTransactionDateDesc();
}
