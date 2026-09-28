package com.aimtester.api.stats;

import java.math.BigDecimal;

/** Posición de un jugador en la clasificación general. */
public record LeaderboardEntry(
        int position,
        String username,
        int bestScore,
        BigDecimal accuracy,
        int matchesPlayed) {
}
