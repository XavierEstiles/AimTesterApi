package com.aimtester.api.stats;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Estadísticas agregadas de un jugador.
 *
 * <p>{@code accuracy} es la precisión global de su historial
 * ({@code totalHits * 100 / (totalHits + totalMisses)}), no la media de los
 * porcentajes de cada partida.</p>
 */
public record StatsResponse(
        long matchesPlayed,
        long totalHits,
        long totalMisses,
        BigDecimal accuracy,
        int bestScore,
        BigDecimal avgScore,
        LocalDateTime lastMatchAt) {
}
