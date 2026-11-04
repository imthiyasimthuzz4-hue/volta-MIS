package in.volta.mis.controller;

import in.volta.mis.model.AppUser;
import in.volta.mis.repository.AppUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserController(AppUserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @GetMapping
    public String list(Model m, Principal principal) {
        List<AppUser> userList = userRepo.findAll();
        m.addAttribute("users", userList);
        m.addAttribute("newUser", new AppUser());
        m.addAttribute("currentUsername", principal != null ? principal.getName() : "admin");

        long adminCount = userList.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();
        long engCount = userList.stream().filter(u -> "ENGINEER".equalsIgnoreCase(u.getRole())).count();
        long accCount = userList.stream().filter(u -> "ACCOUNTANT".equalsIgnoreCase(u.getRole())).count();
        long viewerCount = userList.stream().filter(u -> "VIEWER".equalsIgnoreCase(u.getRole())).count();

        m.addAttribute("adminCount", adminCount);
        m.addAttribute("engCount", engCount);
        m.addAttribute("accCount", accCount);
        m.addAttribute("viewerCount", viewerCount);
        m.addAttribute("editing", false);
        return "users";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m, Principal principal) {
        AppUser u = userRepo.findById(id).orElseThrow();
        m.addAttribute("users", userRepo.findAll());
        m.addAttribute("newUser", u);
        m.addAttribute("editing", true);
        m.addAttribute("currentUsername", principal != null ? principal.getName() : "admin");
        return "users";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AppUser user,
                       @RequestParam(name = "newRawPassword", required = false) String newRawPassword,
                       RedirectAttributes ra) {
        if (user.getId() == null) {
            // New user
            if (userRepo.findByUsername(user.getUsername()).isPresent()) {
                ra.addFlashAttribute("error", "Username '" + user.getUsername() + "' is already taken.");
                return "redirect:/users";
            }
            if (newRawPassword == null || newRawPassword.isBlank()) {
                ra.addFlashAttribute("error", "Password is required for new users.");
                return "redirect:/users";
            }
            user.setPassword(encoder.encode(newRawPassword.trim()));
            userRepo.save(user);
            ra.addFlashAttribute("message", "User '" + user.getUsername() + "' created successfully with role " + user.getRole());
        } else {
            // Edit existing user
            AppUser existing = userRepo.findById(user.getId()).orElseThrow();
            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            existing.setPhone(user.getPhone());
            existing.setRole(user.getRole());
            existing.setEnabled(user.isEnabled());

            if (newRawPassword != null && !newRawPassword.isBlank()) {
                existing.setPassword(encoder.encode(newRawPassword.trim()));
            }
            userRepo.save(existing);
            ra.addFlashAttribute("message", "User '" + existing.getUsername() + "' updated successfully.");
        }
        return "redirect:/users";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        AppUser u = userRepo.findById(id).orElseThrow();
        if ("admin".equalsIgnoreCase(u.getUsername()) || (principal != null && u.getUsername().equalsIgnoreCase(principal.getName()))) {
            ra.addFlashAttribute("error", "Cannot disable your own or primary administrator account.");
            return "redirect:/users";
        }
        u.setEnabled(!u.isEnabled());
        userRepo.save(u);
        ra.addFlashAttribute("message", "User status changed to " + (u.isEnabled() ? "ACTIVE" : "DISABLED"));
        return "redirect:/users";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id, @RequestParam("newPassword") String newPassword, RedirectAttributes ra) {
        AppUser u = userRepo.findById(id).orElseThrow();
        if (newPassword == null || newPassword.isBlank()) {
            ra.addFlashAttribute("error", "Password cannot be empty.");
            return "redirect:/users";
        }
        u.setPassword(encoder.encode(newPassword.trim()));
        userRepo.save(u);
        ra.addFlashAttribute("message", "Password for '" + u.getUsername() + "' reset successfully.");
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        AppUser u = userRepo.findById(id).orElseThrow();
        if ("admin".equalsIgnoreCase(u.getUsername()) || (principal != null && u.getUsername().equalsIgnoreCase(principal.getName()))) {
            ra.addFlashAttribute("error", "Cannot delete the active administrator account.");
            return "redirect:/users";
        }
        userRepo.deleteById(id);
        ra.addFlashAttribute("message", "User '" + u.getUsername() + "' deleted successfully.");
        return "redirect:/users";
    }
}
