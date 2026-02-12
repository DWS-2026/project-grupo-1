package es.codeurjc.daw.library.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Algoritmo de encriptación seguro [cite: 17]
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        // Rutas públicas (Web, CSS, JS, Login)
                        .requestMatchers("/", "/index", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/login", "/register", "/admin-login").permitAll()

                        // Rutas PRIVADAS de Admin (Solo usuarios con rol ADMIN) [cite: 18, 19]
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // El resto de rutas requieren estar al menos autenticado (ej. perfil usuario)
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/admin-login") // URL de tu formulario HTML
                        .loginProcessingUrl("/admin-login") // URL a la que el formulario envía el POST
                        .defaultSuccessUrl("/admin/index", true) // Si login OK, ir al dashboard
                        .failureUrl("/admin-login?error") // Si falla, volver con error
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