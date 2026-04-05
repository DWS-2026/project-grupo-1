package es.codeurjc.daw.library.model;






// region =========== imports =================
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*; // for jpa annotations: mapping java class as persistent db entity
import java.time.LocalDateTime; // to manage timestamps
import java.time.format.DateTimeFormatter; // to format creation timestamp
import java.util.ArrayList;
import java.util.List; // for role lists
// endregion






@Entity // represents user in app
@Table (name = "users") // map to users table in db (user is reserved SQL keyword)
public class User {
    // region =========== id =================
    // primary key (increases automatically per new user)
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id; // unique identifier for each user in db
    // endregion





    // region =========== attributes =================
    // personal info
    private String name;
    private String lastName;
    private String mainPhone;
    private String secondaryPhone;

    @Lob // for image
    @Basic(fetch = FetchType.LAZY)   // to avoid loading the image unless explicitly required
    @Column (name = "profile_picture", columnDefinition = "LONGBLOB") // explicitly define as longblob for mysql
    private byte[] profilePicture;

    // account and security credentials
    @Column (unique = true) // unique email used as username for auth
    private String email;

    private String password; // hashed password

    @ElementCollection (fetch = FetchType.EAGER) // eager ensures roles loaded immediatly with user (for security checks)
    private List<String> roles; // list user roles

    private boolean enabled = true; // flag to enable/disable account (without deleting it)
    private LocalDateTime creationDate = LocalDateTime.now(); // account creation timestamp

    // business data
    private double moneySpent = 0.0;
    // endregion



    // region =========== relationships =================
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-reviews")
    private List<Review> reviews = new ArrayList<>();
    // endregion



    // region =========== constructors =================
    public User() {} // default empty constructor required by jpa



    // client constructor (has attributes for client metrics: money, enabled and date)
    public User (String name, String lastName, String email, String password,
                String mainPhone, String secondaryPhone,
                 double moneySpent, boolean enabled, LocalDateTime creationDate) {
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
    // ===== CONSTRUCTORS END
    // endregion



    // region =========== getters and setters =================
    // id
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // last name
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    // main phone
    public String getMainPhone() { return mainPhone; }
    public void setMainPhone(String mainPhone) { this.mainPhone = mainPhone; }

    // secondary phone
    public String getSecondaryPhone() { return secondaryPhone; }
    public void setSecondaryPhone(String secondaryPhone) { this.secondaryPhone = secondaryPhone; }

    // pfp
    public byte[] getProfilePicture() { return profilePicture; }
    public void setProfilePicture (byte[] profilePicture) { this.profilePicture = profilePicture; }

    // email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // roles list
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    // enabled flag
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // creation timestamp
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    /**
     * formats creation date for better display in mustache templates
     * @return string formatted as dd/mm/yyyy (hh:mm:ss)
     */
    public String getFormattedCreationDate() {
        if (this.creationDate == null) return ""; // if null return empty

        // format date to: day/month/year (hour:minute:second)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("dd/MM/yyyy (HH:mm:ss)");
        return this.creationDate.format (formatter);
    }

    // money spent
    public double getMoneySpent() { return moneySpent; }
    public void setMoneySpent(double moneySpent) { this.moneySpent = moneySpent; }
    // endregion



    // region =========== aux methods =================
    public boolean isHasProfilePicture() {
        return this.profilePicture != null && this.profilePicture.length > 0;
    }
    // endregion
}   