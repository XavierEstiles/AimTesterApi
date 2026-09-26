package com.aimtester.api.match;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Respuesta pública de una partida. Nunca se expone la entidad JPA.
 */
public record MatchResponse(
        Long id,
        String mode,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer durationSeconds,
        Integer hits,
        Integer misses,
        BigDecimal accuracy) {

    public static MatchResponse from(GameMatch match) {
        return new MatchResponse(
                match.getId(),
                match.getMode(),
                match.getStartedAt(),
                match.getFinishedAt(),
                match.getDurationSeconds(),
                match.getHits(),
                match.getMisses(),
                Accuracy.of(match.getHits(), match.getMisses())
        );
    }
}
