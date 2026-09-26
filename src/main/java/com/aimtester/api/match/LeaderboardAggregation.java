package com.aimtester.api.match;

/**
 * Fila del ranking global. Los totales pueden ser {@code null} solo si una fila
 * agrupada no tiene partidas, cosa que no debería ocurrir.
 */
public interface LeaderboardAggregation {

    String getUsername();

    Number getMatchesPlayed();

    Number getBestScore();

    Number getTotalHits();

    Number getTotalMisses();
}
