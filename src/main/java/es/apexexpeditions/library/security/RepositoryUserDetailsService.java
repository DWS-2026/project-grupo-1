package es.apexexpeditions.library.security;






// region =========== imports =================
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
// endregion






@Service // service to bridge db with spring security
// implementation to define how users are loaded during auth
public class RepositoryUserDetailsService implements UserDetailsService {
    // region =========== autowired =================
    @Autowired
    private UserRepository userRepository;
    // endregion





    // region =========== implementation =================
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
        User user = userRepository.findByEmail (email);

        // if user doesnt exist, throw exception required by spring security
        if (user == null) {
            throw new UsernameNotFoundException ("Usuario no encontrado");
        }

        // convert apps custom roles (strings) to spring security authorities
        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : user.getRoles()) {
            roles.add (new SimpleGrantedAuthority("ROLE_" + role));
        }

        // return standard spring security user object
        // includes: username (email), hashed password and permissions
        return new org.springframework.security.core.userdetails.User (
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                roles);
    }
    // endregion
}