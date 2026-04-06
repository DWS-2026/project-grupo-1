package es.codeurjc.daw.library.security;






// region =========== imports =================
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
// endregion






/**
 * Main security configuration class for the application.
 * Defines password encryption, URL access rules, login/logout behavior, and role-based redirection.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    // region =========== autowired =================
    @Autowired
    private UserDetailsService userDetailsService;
    // endregion





    // region =========== value =================
    // Key value taken from application properties for the Remember-Me token
    @Value("${security.rememberme.key}")
    private String rememberMeKey;
    // endregion





    // region =========== bean =================
    // region 1. passwordEncoder
    /**
     * Defines the password encoder bean.
     * BCrypt is used to securely hash passwords before storing them in the database.
     * * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // endregion



    // region 2. rememberMeServices
    /**
     * Configures the Remember-Me services.
     * Customizes the behavior to prevent administrators from generating a remember-me cookie.
     * * @return TokenBasedRememberMeServices instance configured with the secret key and user details.
     */
    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices (rememberMeKey, userDetailsService) {
            @Override
            public void onLoginSuccess (HttpServletRequest request, HttpServletResponse response, Authentication auth) {

                // If the user has the ADMIN role, do not create a remember-me cookie for security reasons
                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch (a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (isAdmin) { 
                    return;
                }

                // Otherwise, create a remember-me cookie valid for the configured duration
                super.onLoginSuccess (request, response, auth);
            }
        };

        services.setTokenValiditySeconds (7 * 24 * 60 * 60); // Set cookie validity duration to 7 days (in seconds)
        services.setParameter ("remember-me"); // Define the HTML input name for the remember-me checkbox
        return services;
    }
    // endregion



    // region 3. filterChain
    /**
     * Configures the security filter chain.
     * Defines which URLs are public, which require authentication, and how the login/logout processes work.
     *
     * @param http The HttpSecurity object to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http.authorizeHttpRequests ((requests) -> requests
                        // Public paths (accessible without logging in)
                        // User navigation routes
                        .requestMatchers (
                                "/",                 // index
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
                                "/login-check"
                        ).permitAll()

                        // Static resources
                        .requestMatchers (
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/img/**",
                                "/vendor/**"
                        ).permitAll()

                        // Error pages
                        .requestMatchers(
                                "/error/**"
                        ).permitAll()

                        // Admin private paths (strictly require the ADMIN role)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // User private paths (require either USER or ADMIN roles)
                        .requestMatchers("/cart/**", "/checkout/**", "/invoice/**", "/profile/**", "/review-user/**").hasAnyRole("USER", "ADMIN")

                        // Any other paths that do not exist are permitted so the custom error controller can throw a 404
                        .anyRequest().permitAll()
                )



                // Configure form-based login
                .formLogin ((form) -> form
                        .loginPage ("/login") // Custom normal user login URL
                        .loginProcessingUrl ("/login-check") // Internal URL used by Spring Security for validation (POST processing)
                        // Dynamic failure handler to separate redirection for regular users vs admins
                        .failureHandler ((request, response, exception) -> {
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
                        .successHandler ((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect ("/admin"); // If the authenticated user is an admin, go to the admin control panel
                            } else {
                                response.sendRedirect ("/"); // If not an admin, go to the main index page
                            }
                        })
                        .permitAll()
                )


                // Configure logout behavior
                .logout((logout) -> logout
                        .logoutUrl ("/logout") // URL that triggers the logout process
                        .logoutSuccessUrl ("/") // Redirection URL after a successful logout
                        .permitAll()
                )

                // Configure the Remember-Me cookie functionality
                .rememberMe((remember) -> remember
                        .rememberMeServices(rememberMeServices())
                )

                // Trigger a 403 Forbidden error if an unauthenticated user tries to visit admin or user-exclusive pages directly
                .exceptionHandling ((exception) -> exception
                    .authenticationEntryPoint( (request, response, authException) -> {
                        String uri = request.getRequestURI();
                        // Conditions to trigger the 403 error
                        if (uri.startsWith ("/admin") || // Admin pages
                        uri.startsWith("/cart") || uri.startsWith("/checkout") || uri.startsWith("/invoice")) { // User-specific pages
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN, "Acceso Denegado");
                        } else {
                            // Default behavior: redirect unauthenticated users to the login page
                            response.sendRedirect ("/login");
                        }
                })
        );

        return http.build();
    }
    // endregion
    // endregion
}