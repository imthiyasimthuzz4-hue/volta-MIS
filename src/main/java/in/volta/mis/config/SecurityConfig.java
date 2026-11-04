package in.volta.mis.config;

import in.volta.mis.service.DatabaseUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, DatabaseUserDetailsService users) throws Exception {
        http.userDetailsService(users)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/attendance/**", "/mobile/**", "/attendance/self-punch/**"))
            .authorizeHttpRequests(auth -> auth
                // Public assets, login, uploads, worker site self-punch, mobile hub & public print previews
                .requestMatchers("/login", "/attendance/self-punch", "/attendance/self-punch/**", "/mobile", "/mobile/hub", "/uploads/**", "/css/**", "/images/**", "/favicon.ico", "/manifest.json", "/sw.js", "/error", "/invoices/print/**", "/quotations/print/**", "/payroll/payslip/**", "/work-orders/print/**").permitAll()

                // Admin Only: User Management & Company Profile Settings
                .requestMatchers("/users/**", "/company-settings/**").hasRole("ADMIN")

                // Payroll & Wage Ledgers: Admin & Accountant can record/delete transactions; others can view
                .requestMatchers("/payroll/save-transaction", "/payroll/delete-transaction/**").hasAnyRole("ADMIN", "ACCOUNTANT")
                .requestMatchers("/payroll/**").hasAnyRole("ADMIN", "ACCOUNTANT", "ENGINEER", "VIEWER")

                // Financials & Invoices: Admin, Accountant & Field Site Engineers/Supervisors can generate bills
                .requestMatchers("/invoices/save", "/invoices/edit/**", "/invoices/delete/**", "/invoices/add-item/**", "/invoices/record-payment/**", "/mobile/quick-bill/**").hasAnyRole("ADMIN", "ACCOUNTANT", "ENGINEER")
                .requestMatchers("/invoices/**", "/mobile/**").hasAnyRole("ADMIN", "ACCOUNTANT", "ENGINEER", "VIEWER")

                // Quotations: Admin, Accountant & Engineer create/edit; Viewer can view/print
                .requestMatchers("/quotations/save", "/quotations/edit/**", "/quotations/delete/**", "/quotations/add-item/**").hasAnyRole("ADMIN", "ACCOUNTANT", "ENGINEER")
                .requestMatchers("/quotations/**").hasAnyRole("ADMIN", "ACCOUNTANT", "ENGINEER", "VIEWER")

                // Operational Expenses: Admin & Accountant only
                .requestMatchers("/expenses/**").hasAnyRole("ADMIN", "ACCOUNTANT")

                // Technical, Staff, Attendance & Operations: Admin, Engineer, Accountant (Viewer can inspect)
                .requestMatchers("/work-orders/save", "/work-orders/delete/**", "/projects/save", "/materials/save", "/materials/adjust-stock/**", "/staff/save", "/attendance/save", "/attendance/quick-check-in", "/attendance/quick-check-out/**", "/attendance/delete/**").hasAnyRole("ADMIN", "ENGINEER", "ACCOUNTANT")
                .requestMatchers("/work-orders/**", "/projects/**", "/materials/**", "/staff/**", "/attendance/**").hasAnyRole("ADMIN", "ENGINEER", "ACCOUNTANT", "VIEWER")

                // Customers: Admin, Engineer, Accountant can save
                .requestMatchers("/customers/save").hasAnyRole("ADMIN", "ENGINEER", "ACCOUNTANT")

                // Dashboard, Customers & Reports: All Authenticated Roles (ADMIN, ENGINEER, ACCOUNTANT, VIEWER)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/dashboard?accessDenied=true")
            );

        return http.build();
    }
}
