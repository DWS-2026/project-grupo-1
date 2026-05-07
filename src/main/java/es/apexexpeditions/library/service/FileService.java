package es.apexexpeditions.library.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileService {
    private final Path root = Paths.get("uploads/certificates");

    public void init() {
        try { if (!Files.exists(root)) Files.createDirectories(root); } 
        catch (IOException e) { throw new RuntimeException("Error creando carpeta uploads/certificates"); }
    }

    public String savePdf(MultipartFile file, Long userId) throws IOException {
        init();
        String originalName = file.getOriginalFilename();
        String fileName = userId + "_" + originalName;
        
        Path filePath = this.root.resolve(fileName);
        Files.deleteIfExists(filePath);
        Files.copy(file.getInputStream(), filePath);
        
        return fileName;
    }

    public byte[] getPdf(String fileName) throws IOException {
        return Files.readAllBytes(this.root.resolve(fileName));
    }
}