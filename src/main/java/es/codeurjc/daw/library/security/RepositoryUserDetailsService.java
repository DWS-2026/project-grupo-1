package es.codeurjc.daw.library.security;




import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;




@Service // service to bridge db with spring security
public class RepositoryUserDetailsService implements UserDetailsService { // implementation to define how users are loaded during auth

    @Autowired
    private UserRepository userRepository;

    /**
     * loads user based on email address.
     * method automatically called by spring security during login process.
     *
     * @param email email submitted in login form
     * @return UserDetails object containing users credentials and authorities
     * @throws UsernameNotFoundException if user not found in db
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // retrieve user from db using email
        User user = userRepository.findByEmail(email);

        // if user doesnt exist, throw exception required by spring security
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        // convert apps custom roles (strings) to spring security authorities
        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // return standard spring security user object
        // includes: username (email), hashed password and permissions
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                roles);
    }
}