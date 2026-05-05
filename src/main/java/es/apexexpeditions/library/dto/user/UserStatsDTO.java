package es.apexexpeditions.library.dto.user;




// use: show simple summary of the aggregation all instances of user entity
// role req: admin
public record UserStatsDTO (
        long totalUsers,
        long activeUsers,
        long disabledUsers,
        double totalMoneySpent
) {}

