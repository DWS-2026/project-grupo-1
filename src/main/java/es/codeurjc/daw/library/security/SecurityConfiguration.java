package es.codeurjc.daw.library.security;




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






/**
 * main security configuration class for app
 * define password encryption, URL access rules, login/logout behavior and role-based redirection
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserDetailsService userDetailsService;



    // key value taken from properties
    @Value("${security.rememberme.key}")
    private String rememberMeKey;



    /**
     * define password encoder bean
     * bcrypt used to securely hash passwords before storing them in db
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Algoritmo de encriptación seguro [cite: 17]
    }



    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices (rememberMeKey, userDetailsService) {
            @Override
            public void onLoginSuccess (HttpServletRequest request, HttpServletResponse response, Authentication auth) {

                // if user is admin, no cookie
                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch (a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (isAdmin) { return;
                }

                // else, cookie remember-me for 7 days
                super.onLoginSuccess (request, response, auth);
            }
        };

        services.setTokenValiditySeconds (7 * 24 * 60 * 60); // duration
        services.setParameter ("remember-me"); // html input name
        return services;
    }



    /**
     * configures security filter chain
     * defines which URLs are public, require authentication and how login/logout processes work
     *
     * @param http HttpSecurity object to configure
     * @return built SecurityFilterChain
     * @throws Exception if error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http.authorizeHttpRequests ((requests) -> requests
                        // public paths (accesible without loggin in)
                        // user navigation
                        .requestMatchers (
                                "/",                 // index
                                "/about",
                                "/contact",
                                "/guides",
                                "/packages",
                                "/services",
                                "/tour-details",
                                "/forgot-password",
                                "/login",
                                "/register",
                                "/admin-login",
                                "/login-check"
                        ).permitAll()

                        // static
                        .requestMatchers (
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/img/**",
                                "/vendor/**"
                        ).permitAll()

                        // errors
                        .requestMatchers(
                                "/error/**"
                        ).permitAll()

                        // admin private paths (require ADMIN role)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // rest of paths / protected (require role USER or ADMIN)
                        .anyRequest().authenticated()
                )



                // configure form-based login
                .formLogin ((form) -> form
                        .loginPage ("/login") // custom normal user login url
                        .loginProcessingUrl ("/login-check") // internal url used by spring for validation (POST processing)
                        .failureUrl ("/login?error") // error url to redirect

                        // redirection logic after successful login
                        .successHandler ((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect ("/admin"); // if admin, go to admin control panel
                            } else {
                                response.sendRedirect ("/"); // if not admin, go to index
                            }
                        })
                        .permitAll()
                )


                // configure logout behavior
                .logout((logout) -> logout
                        .logoutUrl ("/logout") // url that triggers logout
                        .logoutSuccessUrl ("/") // redirection after succesful logout
                        .permitAll()
                )

                // remember me cookie functionality
                .rememberMe((remember) -> remember
                        .rememberMeServices(rememberMeServices())
                )

                // trigger 403 if anon tries to visit admin or user exclusive pages
                .exceptionHandling ((exception) -> exception
                    .authenticationEntryPoint( (request, response, authException) -> {
                        String uri = request.getRequestURI();
                        // conditions to trigger
                        if (uri.startsWith ("/admin") || // admin pages
                        uri.startsWith("/cart") || uri.startsWith("/checkout") || uri.startsWith("/invoice")) { // user pages
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN, "Acceso Denegado");
                        } else {
                            // default redirect to login
                            response.sendRedirect ("/login");
                        }
                })
        );

        return http.build();
    }
}