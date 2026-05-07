package es.apexexpeditions.library.security;

// region =========== imports =================
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import es.apexexpeditions.library.security.jwt.JwtRequestFilter;
import es.apexexpeditions.library.security.jwt.JwtTokenProvider;
import es.apexexpeditions.library.security.jwt.UnauthorizedHandlerJwt;
// endregion

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
        @Autowired
        private RateLimitFilter rateLimitFilter;
        // endregion

        // region =========== bean =================
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

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
        // region 1. apiFilterChain
        /**
         * ── CHAIN 1 · REST API (/api/**) ──────────────────────────────────────────
         * Stateless JWT auth. CSRF, form login and Basic Auth disabled.
         * Auth errors return a structured JSON 401 response.
         */
        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
                http
                                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                                .authenticationProvider(authenticationProvider())
                                .securityMatcher("/api/**")
                                .exceptionHandling(
                                                handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt))

                                // configure route authorization
                                .authorizeHttpRequests(auth -> auth
                                                // BLOCK sensible files
                                                .requestMatchers(
                                                                "/.git/**",
                                                                "/.env",
                                                                "/backup/**",
                                                                "/*.bak",
                                                                "/*.backup",
                                                                "/*.old",
                                                                "/*.sql",
                                                                "/*.dump",
                                                                "/WEB-INF/**",
                                                                "/META-INF/**")
                                                .denyAll()

                                                // 1. PUBLIC ENDPOINTS
                                                .requestMatchers("/api/v1/auth/**").permitAll() // Covers login,
                                                                                                // register, and refresh
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/tours/**",
                                                                "/api/v1/reviews/**",
                                                                "/api/v1/guides/**",
                                                                "/api/v1/images/**")
                                                .permitAll()

                                                // 2. ADMIN-EXCLUSIVE ENDPOINTS (grouped by HTTP method to reduce code)
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/tours/stats", "/api/v1/reviews/hidden",
                                                                "/api/v1/users/stats")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/images/**", "/api/v1/tours", "/api/v1/guides")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.PUT,
                                                                "/api/v1/images/**", "/api/v1/tours/*",
                                                                "/api/v1/tours/*/image/media", "/api/v1/guides/*")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.PATCH,
                                                                "/api/v1/reviews/**", "/api/v1/users/*/status")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.DELETE,
                                                                "/api/v1/images/**", "/api/v1/tours/*",
                                                                "/api/v1/tours/*/image", "/api/v1/guides/*",
                                                                "/api/v1/bookings/*")
                                                .hasRole("ADMIN")

                                                // 3. ENDPOINTS FOR LOGGED USERS AND ADMINS
                                                .requestMatchers(HttpMethod.POST, "/api/v1/reviews")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                // 4. REST OF THE API (Requires authentication - Zero Trust)
                                                .anyRequest().authenticated())
                                // disable traditional web protections (stateless, csrf, form login)
                                .formLogin(formLogin -> formLogin.disable())
                                .csrf(csrf -> csrf.disable())
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // add JWT filter
                                .addFilterBefore(new JwtRequestFilter(userDetailService, jwtTokenProvider),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
        // endregion

        // region 2. filterChain
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
                http.headers(headers -> headers

                                // CSP patch: mitigates xss and data injection
                                .contentSecurityPolicy(csp -> csp
                                                .policyDirectives("default-src 'self'; " +
                                                // Added kit.fontawesome.com for FontAwesome Kits
                                                                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://ajax.googleapis.com https://cdnjs.cloudflare.com https://kit.fontawesome.com; "
                                                                +

                                                                // icon styles
                                                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com https://cdnjs.cloudflare.com https://ka-f.fontawesome.com https://use.fontawesome.com; "
                                                                +
                                                                // icon fonts
                                                                "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://ka-f.fontawesome.com https://use.fontawesome.com; "
                                                                +
                                                                // connect-src to allow FontAwesome Kits to fetch SVGs
                                                                "connect-src 'self' https://ka-f.fontawesome.com; " +

                                                                "img-src 'self' data: blob:; " +
                                                                "frame-ancestors 'none'; " +
                                                                "form-action 'self';"))

                                // HSTS patch: forces https
                                .httpStrictTransportSecurity(hsts -> hsts
                                                .maxAgeInSeconds(31536000) // 1 year
                                                .includeSubDomains(true)
                                                .preload(true)));

                http.authenticationProvider(authenticationProvider());

                http
                                .authorizeHttpRequests((requests) -> requests

                                                // BLOCK sensible files
                                                .requestMatchers(
                                                                "/.git/**",
                                                                "/.env",
                                                                "/backup/**",
                                                                "/*.bak",
                                                                "/*.backup",
                                                                "/*.old",
                                                                "/*.sql",
                                                                "/*.dump",
                                                                "/WEB-INF/**",
                                                                "/META-INF/**")
                                                .denyAll()

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
                                                                "/login-check",
                                                                "/user/*/image")
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
                                                .requestMatchers("/cart/**", "/checkout/**", "/invoice/**",
                                                                "/profile/**", "/review-user/**")
                                                .authenticated()

                                                // controller can throw a 404
                                                .anyRequest().authenticated())

                                // Configure form-based login
                                .formLogin((form) -> form
                                                .loginPage("/login") // Custom normal user login URL
                                                .loginProcessingUrl("/login-check") // Internal URL used by Spring
                                                                                    // Security for validation (POST
                                                                                    // processing)
                                                // Dynamic failure handler to separate redirection for regular users vs
                                                // admins
                                                .failureHandler((request, response, exception) -> {
                                                        String referer = request.getHeader("Referer");
                                                        // Case: User account is set as inactive (disabled)
                                                        if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                                                response.sendRedirect("/login?inactive");
                                                        } else { // Case: Invalid credentials error
                                                                if (referer != null
                                                                                && referer.contains("/admin-login")) {
                                                                        // Redirect back to the admin login page if the
                                                                        // attempt came from there
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
                                                                        .anyMatch(a -> a.getAuthority()
                                                                                        .equals("ROLE_ADMIN"))) {
                                                                response.sendRedirect("/admin"); // If the authenticated
                                                                                                 // user is an admin, go
                                                                                                 // to the
                                                                                                 // admin control panel
                                                        } else {
                                                                response.sendRedirect("/"); // If not an admin, go to
                                                                                            // the main index page
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
                                                                        uri.startsWith("/cart")
                                                                        || uri.startsWith("/checkout")
                                                                        || uri.startsWith("/invoice")) { // User-specific
                                                                                                         // pages
                                                                response.sendError(
                                                                                jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN,
                                                                                "Acceso Denegado");
                                                        } else {
                                                                // Default behavior: redirect unauthenticated users to
                                                                // the login page
                                                                response.sendRedirect("/login");
                                                        }
                                                }));

                return http.build();
        }
        // endregion
        // endregion
}