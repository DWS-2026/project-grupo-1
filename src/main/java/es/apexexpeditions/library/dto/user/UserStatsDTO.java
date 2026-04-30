package es.apexexpeditions.library.dto.user;


// simple summary of the aggregation all instances of user entity
public record UserStatsDTO (
        long totalUsers,
        long activeUsers,
        long disabledUsers,
        double totalMoneySpent
) {}

