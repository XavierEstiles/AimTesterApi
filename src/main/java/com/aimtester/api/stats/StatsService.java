package com.aimtester.api.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimtester.api.match.Accuracy;
import com.aimtester.api.match.GameMatch;
import com.aimtester.api.match.GameMatchRepository;
import com.aimtester.api.match.LeaderboardAggregation;
import com.aimtester.api.match.MatchStatsAggregation;
import com.aimtester.api.user.User;
import com.aimtester.api.user.UserRepository;

@Service
public class StatsService {

    private static final int DEFAULT_LEADERBOARD_LIMIT = 20;
    private static final int MAX_LEADERBOARD_LIMIT = 100;

    private final GameMatchRepository gameMatchRepository;
    private final UserRepository userRepository;

    public StatsService(GameMatchRepository gameMatchRepository, UserRepository userRepository) {
        this.gameMatchRepository = gameMatchRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public StatsResponse playerStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado ya no existe."));

        MatchStatsAggregation stats = gameMatchRepository.aggregateByUserId(user.getId());

        long matchesPlayed = asLong(stats.getMatchesPlayed());
        long totalHits = asLong(stats.getTotalHits());
        long totalMisses = asLong(stats.getTotalMisses());
        int bestScore = (int) asLong(stats.getBestScore());
        BigDecimal avgScore = asBigDecimal(stats.getAvgScore());

        LocalDateTime lastMatchAt = gameMatchRepository
                .findFirstByUserIdOrderByStartedAtDesc(user.getId())
                .map(GameMatch::getStartedAt)
                .orElse(null);

        return new StatsResponse(
                matchesPlayed,
                totalHits,
                totalMisses,
                Accuracy.of(totalHits, totalMisses),
                bestScore,
                avgScore,
                lastMatchAt
        );
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> leaderboard(Integer limit) {
        int pageLimit = limit == null ? DEFAULT_LEADERBOARD_LIMIT
                : Math.min(Math.max(limit, 1), MAX_LEADERBOARD_LIMIT);

        List<LeaderboardAggregation> rows = gameMatchRepository.findLeaderboard(PageRequest.of(0, pageLimit));
        List<LeaderboardEntry> entries = new ArrayList<>(rows.size());

        int previousBestScore = Integer.MIN_VALUE;
        int currentPosition = 0;

        for (int index = 0; index < rows.size(); index++) {
            LeaderboardAggregation row = rows.get(index);
            int bestScore = (int) asLong(row.getBestScore());

            // Empate: comparten posición; si mejora la puntuación, se avanza.
            if (bestScore != previousBestScore) {
                currentPosition = index + 1;
                previousBestScore = bestScore;
            }

            long totalHits = asLong(row.getTotalHits());
            long totalMisses = asLong(row.getTotalMisses());

            entries.add(new LeaderboardEntry(
                    currentPosition,
                    row.getUsername(),
                    bestScore,
                    Accuracy.of(totalHits, totalMisses),
                    (int) asLong(row.getMatchesPlayed())
            ));
        }

        return entries;
    }

    private long asLong(Number value) {
        return value == null ? 0L : value.longValue();
    }

    private BigDecimal asBigDecimal(Number value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2);
        }

        return BigDecimal.valueOf(value.doubleValue()).setScale(2, RoundingMode.HALF_UP);
    }
}
