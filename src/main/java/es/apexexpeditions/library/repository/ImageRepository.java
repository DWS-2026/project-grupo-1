package es.apexexpeditions.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.apexexpeditions.library.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> { } 