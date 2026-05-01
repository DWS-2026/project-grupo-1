package es.apexexpeditions.library.security;


// region =========== imports =================

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// endregion

import es.apexexpeditions.library.security.jwt.JwtRequestFilter;
import es.apexexpeditions.library.security.jwt.JwtTokenProvider;
import es.apexexpeditions.library.security.jwt.UnauthorizedHandlerJwt;

/**
 * Dual security configuration:
 * - Order(1): REST API chain (/api/**) — stateless, JWT-based, JSON error
 * responses.
 * - Order(2): Web chain — stateful, form login, remember-me.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // region =========== autowired =================
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public RepositoryUserDetailsService userDetailService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // endregion

    // region =========== shared beans =================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
    // endregion

    // region =========== filter chains =================

    /**
     * ── CHAIN 1 · REST API (/api/**) ──────────────────────────────────────────
     * Stateless JWT auth. CSRF, form login and Basic Auth disabled.
     * Auth errors return a structured JSON 401 response.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        // error management for api

        http.authorizeHttpRequests(auth -> auth
                // PRIVATE ENDPOINTS
                // IMAGES
                .requestMatchers(HttpMethod.POST, "/api/v1/images/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/images/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/images/**").hasRole("ADMIN")
                // TOURS
                .requestMatchers(HttpMethod.POST, "/api/v1/tours").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/tours/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/tours/*").hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/tours/*/image/media").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/tours/*/image").hasRole("ADMIN")

                // PUBLIC ENDPOINTS
                // IMAGES
                .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
                // TOURS
                .requestMatchers(HttpMethod.GET, "/api/v1/tours/**").permitAll()

                .anyRequest().authenticated());

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(new JwtRequestFilter(userDetailService, jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // region 3. filterChain
    /**
     * Configures the security filter chain.
     * Defines which URLs are public, which require authentication, and how the
     * login/logout processes work.
     *
     * @param http The HttpSecurity object to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests((requests) -> requests
                        // Public paths (accessible without logging in)
                        // User navigation routes
                        .requestMatchers(
                                "/", // index
                                "/about",
                                "/contact",
                                "/guides",
                                "/guides/**",
                                "/packages",
                                "/services",
                                "/tour-details/**",
                                "/forgot-password",
                                "/login",
                                "/register",
                                "/admin-login",
                                "/login-check")
                        .permitAll()

                        // Static resources
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/img/**",
                                "/vendor/**")
                        .permitAll()

                        // Error pages
                        .requestMatchers(
                                "/error/**")
                        .permitAll()

                        // Admin private paths (strictly require the ADMIN role)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // User private paths (require either USER or ADMIN roles)
                        .requestMatchers("/cart/**", "/checkout/**", "/invoice/**", "/profile/**", "/review-user/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Any other paths that do not exist are permitted so the custom error
                        // controller can throw a 404
                        .anyRequest().permitAll())

                // Configure form-based login
                .formLogin((form) -> form
                        .loginPage("/login") // Custom normal user login URL
                        .loginProcessingUrl("/login-check") // Internal URL used by Spring Security for validation (POST
                                                            // processing)
                        // Dynamic failure handler to separate redirection for regular users vs admins
                        .failureHandler((request, response, exception) -> {
                            String referer = request.getHeader("Referer");
                            // Case: User account is set as inactive (disabled)
                            if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                response.sendRedirect("/login?inactive");
                            } else { // Case: Invalid credentials error
                                if (referer != null && referer.contains("/admin-login")) {
                                    // Redirect back to the admin login page if the attempt came from there
                                    response.sendRedirect("/admin-login?error");
                                } else {
                                    // Redirect back to the standard login page
                                    response.sendRedirect("/login?error");
                                }
                            }
                        })

                        // Redirection logic after a successful login
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin"); // If the authenticated user is an admin, go to the
                                                                 // admin control panel
                            } else {
                                response.sendRedirect("/"); // If not an admin, go to the main index page
                            }
                        })
                        .permitAll())

                // Configure logout behavior
                .logout((logout) -> logout
                        .logoutUrl("/logout") // URL that triggers the logout process
                        .logoutSuccessUrl("/") // Redirection URL after a successful logout
                        .permitAll())

                // Trigger a 403 Forbidden error if an unauthenticated user tries to visit admin
                // or user-exclusive pages directly
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String uri = request.getRequestURI();
                            // Conditions to trigger the 403 error
                            if (uri.startsWith("/admin") || // Admin pages
                                    uri.startsWith("/cart") || uri.startsWith("/checkout")
                                    || uri.startsWith("/invoice")) { // User-specific pages
                                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN,
                                        "Acceso Denegado");
                            } else {
                                // Default behavior: redirect unauthenticated users to the login page
                                response.sendRedirect("/login");
                            }
                        }));

        return http.build();
    }
    // endregion
    // endregion
}