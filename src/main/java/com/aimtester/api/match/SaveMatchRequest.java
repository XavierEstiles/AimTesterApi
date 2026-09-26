package com.aimtester.api.match;

/**
 * Petición de guardado de una partida.
 *
 * <p>Los horarios no viajan en la petición: el servidor deriva
 * {@code finished_at = ahora} y {@code started_at = ahora - durationSeconds}.</p>
 */
public record SaveMatchRequest(String mode, Integer durationSeconds, Integer hits, Integer misses) {
}
