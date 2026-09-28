package com.aimtester.api.player;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aimtester.api.stats.ModeStatsResponse;
import com.aimtester.api.stats.StatsResponse;
import com.aimtester.api.stats.StatsService;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final StatsService statsService;

    public PlayerController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        );
    }

    @GetMapping("/me")
    public Map<String, String> currentUser(Authentication authentication) {
        return Map.of("username", authentication.getName());
    }

    @GetMapping("/stats")
    public StatsResponse stats(Authentication authentication) {
        return statsService.playerStats(authentication.getName());
    }

    /** Estadísticas del jugador desglosadas por modo de juego. */
    @GetMapping("/stats/by-mode")
    public List<ModeStatsResponse> statsByMode(Authentication authentication) {
        return statsService.playerStatsByMode(authentication.getName());
    }
}
