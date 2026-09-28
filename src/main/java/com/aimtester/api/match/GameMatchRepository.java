package com.aimtester.api.match;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {

    List<GameMatch> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    Optional<GameMatch> findFirstByUserIdOrderByStartedAtDesc(Long userId);

    /**
     * Agregados de una partida de un usuario. Los totales pueden ser {@code null}
     * cuando el usuario todavía no ha jugado; eso lo resuelve la capa de servicio.
     */
    @Query("""
           SELECT COUNT(m) AS matchesPlayed,
                  SUM(m.hits) AS totalHits,
                  SUM(m.misses) AS totalMisses,
                  MAX(m.hits) AS bestScore,
                  AVG(m.hits) AS avgScore
             FROM GameMatch m
            WHERE m.user.id = :userId
           """)
    MatchStatsAggregation aggregateByUserId(@Param("userId") Long userId);

    /**
     * Agregados de las partidas de un usuario agrupadas por modo, ordenados de
     * la mejor puntuación (dentro de cada modo) a la peor. Los totales no pueden
     * ser {@code null} porque cada fila agrupada tiene al menos una partida.
     */
    @Query("""
           SELECT m.mode AS mode,
                  COUNT(m) AS matchesPlayed,
                  SUM(m.hits) AS totalHits,
                  SUM(m.misses) AS totalMisses,
                  MAX(m.hits) AS bestScore,
                  AVG(m.hits) AS avgScore
             FROM GameMatch m
            WHERE m.user.id = :userId
            GROUP BY m.mode
            ORDER BY MAX(m.hits) DESC, COUNT(m) DESC, m.mode ASC
           """)
    List<ModeStatsAggregation> aggregateByUserIdAndMode(@Param("userId") Long userId);

    /** Ranking global agrupando por usuario. */
    @Query("""
           SELECT m.user.username AS username,
                  COUNT(m) AS matchesPlayed,
                  MAX(m.hits) AS bestScore,
                  SUM(m.hits) AS totalHits,
                  SUM(m.misses) AS totalMisses
             FROM GameMatch m
            GROUP BY m.user.id, m.user.username
            ORDER BY MAX(m.hits) DESC, SUM(m.hits) DESC
           """)
    List<LeaderboardAggregation> findLeaderboard(Pageable pageable);
}
