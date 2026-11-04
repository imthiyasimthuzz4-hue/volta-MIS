package in.volta.mis.repository;
import in.volta.mis.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AppUserRepository extends JpaRepository<AppUser,Long> { Optional<AppUser> findByUsername(String username); }
