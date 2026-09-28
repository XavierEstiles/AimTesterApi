package com.aimtester.api.match;

import java.time.LocalDateTime;

import com.aimtester.api.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Una partida jugada (resumen).
 *
 * <p>La precisión no está mapeada: la calcula la base de datos en la columna
 * generada {@code matches.accuracy}. Como {@code hits} y {@code misses} están
 * aquí, la API devuelve el mismo resultado con la fórmula
 * {@code hits * 100 / (hits + misses)}.</p>
 */
@Entity
@Table(name = "matches")
public class GameMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "mode", nullable = false, length = 30)
    private String mode;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    /** Puntos: clics acertados en el objetivo. */
    @Column(name = "hits", nullable = false)
    private Integer hits;

    /** Fallos: clics sobre el tablero. */
    @Column(name = "misses", nullable = false)
    private Integer misses;

    protected GameMatch() {
        // Constructor para JPA
    }

    public GameMatch(
            User user,
            String mode,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            Integer durationSeconds,
            Integer hits,
            Integer misses) {
        this.user = user;
        this.mode = mode;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.durationSeconds = durationSeconds;
        this.hits = hits;
        this.misses = misses;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getMode() {
        return mode;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public Integer getHits() {
        return hits;
    }

    public Integer getMisses() {
        return misses;
    }
}
