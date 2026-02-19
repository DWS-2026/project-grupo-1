package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;




@Entity // entity represents a user in  app
@Table(name = "users") // map to users table in db (user is reserved SQL keyword)
public class User {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ATTRIBUTES START =====================================================
    // personal info
    private String name;
    private String lastName;
    private String mainPhone;
    private String secondaryPhone;

    @Lob // for large text or binary data (base64 or long URLs)
    private String profilePicture;

    // account and security credentials
    @Column(unique = true)
    private String email;

    private String password; // stores users password

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles; // stores users roles

    private boolean enabled = true; // flag to enable/disable account
    private LocalDateTime creationDate = LocalDateTime.now(); // account creation timestamp

    // business data
    private double moneySpent = 0.0;
    // ATTRIBUTES END =====================================================


    // CONSTRUCTORS START =====================================================
    public User() {} // default empty constructor required by JPA


    // client constructor (with money, enabled and date attributes)
    public User (String name, String lastName, String email, String password,
                String mainPhone, String secondaryPhone, double moneySpent,
                boolean enabled, LocalDateTime creationDate) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.mainPhone = mainPhone;
        this.secondaryPhone = secondaryPhone;
        this.moneySpent = moneySpent;
        this.enabled = enabled;
        this.creationDate = creationDate;
    }


    // admin constructor (no client metrics)
    public User(String name, String lastName, String email, String password,
                String mainPhone, String secondaryPhone) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.mainPhone = mainPhone;
        this.secondaryPhone = secondaryPhone;
    }
    // CONSTRUCTORS END =====================================================


    // GETTERS AND SETTERS START =====================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMainPhone() { return mainPhone; }
    public void setMainPhone(String mainPhone) { this.mainPhone = mainPhone; }

    public String getSecondaryPhone() { return secondaryPhone; }
    public void setSecondaryPhone(String secondaryPhone) { this.secondaryPhone = secondaryPhone; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public double getMoneySpent() { return moneySpent; }
    public void setMoneySpent(double moneySpent) { this.moneySpent = moneySpent; }
    // GETTERS AND SETTERS END =====================================================
}   