package com.aimtester.api.match;

/**
 * Fila de la agregación de partidas de un usuario agrupadas por modo de juego.
 * Los tipos son {@link Number} para no acoplarse al tipo que devuelve el motor
 * ({@code Long}, {@code Double}, {@code BigDecimal}...).
 */
public interface ModeStatsAggregation {

    String getMode();

    Number getMatchesPlayed();

    Number getTotalHits();

    Number getTotalMisses();

    Number getBestScore();

    Number getAvgScore();
}
