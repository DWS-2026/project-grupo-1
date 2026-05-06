package es.apexexpeditions.library.dto.tour;


// simple summary of the aggregation all instances of user entity
public record TourStatsDTO (
        long totalTours,
        long activeTours,
        long disabledTours,
        double maxPrice,
        double minPrice,
        double averageRating,
        double averageDuration
) {}

