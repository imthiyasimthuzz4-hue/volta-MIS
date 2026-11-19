package in.volta.mis.repository;

import in.volta.mis.model.SiteMaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteMaterialRequestRepository extends JpaRepository<SiteMaterialRequest, Long> {

    List<SiteMaterialRequest> findAllByOrderByRequestDateDescIdDesc();

    List<SiteMaterialRequest> findByStatusOrderByRequestDateDescIdDesc(String status);

    long countByStatus(String status);
}
