package in.volta.mis.config;
import in.volta.mis.model.AppUser;
import in.volta.mis.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class InitialAdmin {
 @Bean CommandLineRunner createInitialAdmin(AppUserRepository users, PasswordEncoder encoder) {
  return args -> { if (users.findByUsername("admin").isEmpty()) { AppUser u=new AppUser(); u.setUsername("admin"); u.setPassword(encoder.encode("Admin@12345")); u.setRole("ADMIN"); u.setEnabled(true); users.save(u); } };
 }
}
