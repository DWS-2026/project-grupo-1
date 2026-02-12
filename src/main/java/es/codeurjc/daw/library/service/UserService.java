package es.codeurjc.daw.library.service;




import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




/**
 * service layer class that handles business logic related to users
 * acts as intermediary between WebController and UserRepository
 */
@Service
public class UserService {

    // repository injection for db access
    @Autowired
    private UserRepository userRepository;


    /**
     * retrieves user from db using their email address
     * @param email email used for search
     * @return user object if found, null otherwise
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * persists or updates user in dn
     * used for profile updates and initial data creation
     */
    public void save(User user) {
        userRepository.save(user);
    }


    /**
     * deletes user from db
     * Used when the user decides to close their account.
     */
    public void delete(User user) {
        userRepository.delete(user);
    }
}