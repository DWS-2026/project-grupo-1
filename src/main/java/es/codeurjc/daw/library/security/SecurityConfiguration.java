package es.codeurjc.daw.library.security;




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;




@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Algoritmo de encriptación seguro [cite: 17]
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http.authorizeHttpRequests ((requests) -> requests
                        // public paths
                        .requestMatchers("/", "/index", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/login", "/register", "/admin-login", "/login-check").permitAll()

                        // admin private paths
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // rest of paths (require login as user or admin)
                        .anyRequest().authenticated()
                )


                .formLogin((form) -> form
                        .loginPage("/login") // normal user login
                        .loginProcessingUrl("/login-check") // internal url used by spring for validation
                        .failureUrl("/login?error") // error url
                        .successHandler((request, response, authentication) -> {
                            // Lógica de redirección inteligente:
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin"); // Si es Admin -> Panel de control
                            } else {
                                response.sendRedirect("/"); // Si es User -> Página de inicio
                            }
                        })
                        .permitAll()
                )


                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}