package in.volta.mis.service;
import in.volta.mis.repository.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
 private final AppUserRepository users;
 public DatabaseUserDetailsService(AppUserRepository users){this.users=users;}
 @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
  var u=users.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
  return User.withUsername(u.getUsername()).password(u.getPassword()).disabled(!u.isEnabled()).authorities(new SimpleGrantedAuthority("ROLE_"+u.getRole())).build();
 }
}
