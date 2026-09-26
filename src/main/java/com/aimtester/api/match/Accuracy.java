package com.aimtester.api.match;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Misma fórmula que usa la interfaz y la columna generada
 * {@code matches.accuracy}: {@code hits * 100 / (hits + misses)}.
 */
public final class Accuracy {

    private Accuracy() {
    }

    public static BigDecimal of(int hits, int misses) {
        return of((long) hits, (long) misses);
    }

    public static BigDecimal of(long hits, long misses) {
        long total = hits + misses;
        if (total <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(hits * 100.0 / total).setScale(2, RoundingMode.HALF_UP);
    }
}
