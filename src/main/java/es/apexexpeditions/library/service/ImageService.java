package es.apexexpeditions.library.service;


// region =========== imports =================
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.repository.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found: " + id));
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

    public void deleteImage (long id) {
        imageRepository.deleteById (id);
    }
    // endregion

    // region =========== methods =================
    public byte[] getImageFile(long id) {
        return getImage(id).getImageFile();
    }
    // endregion
}