package es.apexexpeditions.library.model;


// region =========== imports =================
import jakarta.persistence.*;
// endregion




@Entity
@Table(name = "images")
public class Image {
    // region =========== id =================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // endregion

    // region =========== attributes =================
    @Lob
    @Basic(fetch = FetchType.LAZY)   // wont load until explicitly required
    private byte[] imageFile;
    // endregion

    // region =========== constructors =================
    public Image() {}
    public Image(byte[] imageFile) {
        this.imageFile = imageFile;
    }
    // endregion

    // region =========== getters and setters =================
    // id
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // imageFile
    public byte[] getImageFile() {
        return imageFile;
    }
    public void setImageFile(byte[] imageFile) {
        this.imageFile = imageFile;
    }
    // endregion
}