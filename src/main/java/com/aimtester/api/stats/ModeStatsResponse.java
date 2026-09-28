package com.aimtester.api.stats;

import java.math.BigDecimal;

/**
 * Estadísticas de un modo de juego concreto para un jugador autenticado.
 *
 * <p>{@code accuracy} es la precisión global de ese modo
 * ({@code totalHits * 100 / (totalHits + totalMisses)}), igual que en
 * {@link StatsResponse}, no la media de los porcentajes de cada partida.</p>
 */
public record ModeStatsResponse(
        String mode,
        long matchesPlayed,
        long totalHits,
        long totalMisses,
        BigDecimal accuracy,
        int bestScore,
        BigDecimal avgScore) {
}
