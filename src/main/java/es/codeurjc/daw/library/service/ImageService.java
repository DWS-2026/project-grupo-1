package es.codeurjc.daw.library.service;


// region =========== imports =================
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.repository.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
// endregion

@Service
public class ImageService {
    // region =========== autowired =================
    @Autowired
    private ImageRepository imageRepository;
    // endregion


    // region =========== derived query methods =================
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Page<Image> getAllImages (Pageable pageable) {
        return imageRepository.findAll (pageable);
    }

    public Image getImage (long id) {
        return imageRepository.findById (id)
                .orElseThrow(() -> new RuntimeException ("Image not found: " + id));
    }

    public Image createImage (MultipartFile file) throws IOException {
        Image image = new Image (file.getBytes());
        return imageRepository.save(image);
    }

    public void replaceImageFile (long id, MultipartFile file) throws IOException {
        Image image = getImage (id);
        image.setImageFile (file.getBytes()); // Directo a bytes
        imageRepository.save (image);
    }

    public Image deleteImage (long id) {
        Image image = getImage (id);
        imageRepository.delete (image);
        return image;
    }
    // endregion

    // region =========== methods =================
    public byte[] getImageFile(long id) {
        return getImage(id).getImageFile();
    }
    // endregion
}