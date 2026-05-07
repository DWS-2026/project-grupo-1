package es.apexexpeditions.library.service;

import es.apexexpeditions.library.model.Guide;
import es.apexexpeditions.library.repository.GuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import es.apexexpeditions.library.dto.guide.GuideMapper;
import es.apexexpeditions.library.dto.guide.GuideRequestDTO;
import es.apexexpeditions.library.dto.guide.GuideResponseDTO;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.repository.TourRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


// for pfp generation
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jakarta.annotation.Nullable;




@Service
public class GuideService {

    private final GuideRepository guideRepository;

    private final TourRepository tourRepository;
    private final GuideMapper guideMapper;

    public GuideService(GuideRepository guideRepository, TourRepository tourRepository, GuideMapper guideMapper) {
        this.guideRepository = guideRepository;
        this.tourRepository = tourRepository;
        this.guideMapper = guideMapper;
    }

public Page<GuideResponseDTO> findAllDTOs(Pageable pageable) {
    return guideRepository.findAll(pageable)
            .map(guide -> guideMapper.toDTO(guide));
}

    public GuideResponseDTO findDTOById(Long id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guide not found with id: " + id));
        return guideMapper.toDTO(guide);
    }

    @Transactional
    public GuideResponseDTO create(GuideRequestDTO dto) {
        Guide guide = guideMapper.toDomain(dto);

        if (dto.tourId() != null) {
            Tour tour = tourRepository.findById(dto.tourId())
                    .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + dto.tourId()));
            guide.setTour(tour);
        }

        return guideMapper.toDTO(guideRepository.save(guide));
    }


    @Transactional
    public GuideResponseDTO replace(Long id, GuideRequestDTO dto) {
        Guide existing = guideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guide not found: " + id));

        // manual update to be coherent with TourService
        existing.setName(dto.name());
        existing.setLastName(dto.lastName());
        existing.setPrice(dto.price());
        existing.setEnabled(dto.enabled());

        if (dto.tourId() != null) {
            Tour tour = tourRepository.findById(dto.tourId())
                    .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + dto.tourId()));
            existing.setTour(tour);
        }

        return guideMapper.toDTO(guideRepository.save(existing));
    }


    public List<Guide> getGuidesByTour(Long tourId) {
        return guideRepository.findByTourId(tourId);
    }

    public Guide save(Guide guide) {
        return guideRepository.save(guide);
    }

    public List<Guide> findAll() {
        return guideRepository.findAll();
    }

    public Guide findById(Long id) {
        return guideRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        guideRepository.deleteById(id);
    }




    /**
     * generates default pfp
     * @param roleText first line text (user role)
     * @param nameText second line text (users name)
     * @param bgColor image bg color
     * @return b64 string ready to store in db
     */
    public @Nullable byte[] generateDefaultAvatar (String roleText, String nameText, Color bgColor) {
        // image size
        int width = 200;
        int height = 200;

        // buffered image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // anti-analising for smoother text
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // add background
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        // add text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics metrics = g.getFontMetrics();

        // calculate center for role and name
        // role
        int x1 = (width - metrics.stringWidth(roleText)) / 2;
        int y1 = (height / 2) - 15;
        g.drawString (roleText, x1, y1);
        // name
        int x2 = (width - metrics.stringWidth(nameText)) / 2;
        int y2 = (height / 2) + 25;
        g.drawString (nameText, x2, y2);
        g.dispose();

        // convert generated image to b64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return imageBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // generation error fallback
        }
    }
}
