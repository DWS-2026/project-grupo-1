package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image getImage(long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found: " + id));
    }

    public String getImageFile(long id) {
        return getImage(id).getImageFile();
    }

    public Image createImage(MultipartFile file) throws IOException {
        String base64 = encodeToBase64(file);
        Image image = new Image(base64);
        return imageRepository.save(image);
    }

    public void replaceImageFile(long id, MultipartFile file) throws IOException {
        Image image = getImage(id);
        image.setImageFile(encodeToBase64(file));
        imageRepository.save(image);
    }

    public Image deleteImage(long id) {
        Image image = getImage(id);
        imageRepository.delete(image);
        return image;
    }

    private String encodeToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}