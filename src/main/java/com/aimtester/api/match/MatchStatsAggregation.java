package com.aimtester.api.match;

/**
 * Resultado de la consulta agregada de partidas de un usuario.
 * Los tipos son {@link Number} para no acoplarse al tipo que devuelve el motor
 * ({@code Long}, {@code Double}, {@code BigDecimal}...).
 */
public interface MatchStatsAggregation {

    Number getMatchesPlayed();

    Number getTotalHits();

    Number getTotalMisses();

    Number getBestScore();

    Number getAvgScore();
}
