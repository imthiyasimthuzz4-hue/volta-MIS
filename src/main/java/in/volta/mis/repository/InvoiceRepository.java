package in.volta.mis.repository;

import in.volta.mis.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findAllByOrderByInvoiceDateDesc();

    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i")
    BigDecimal getTotalInvoicedAmount();

    @Query("SELECT COALESCE(SUM(i.taxableAmount), 0) FROM Invoice i")
    BigDecimal getTotalTaxableAmount();

    @Query("SELECT COALESCE(SUM(i.totalGstAmount), 0) FROM Invoice i")
    BigDecimal getTotalGstCollected();

    @Query("SELECT COALESCE(SUM(i.balanceAmount), 0) FROM Invoice i")
    BigDecimal getTotalPendingBalance();

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i")
    BigDecimal getTotalPaidAmount();
}
