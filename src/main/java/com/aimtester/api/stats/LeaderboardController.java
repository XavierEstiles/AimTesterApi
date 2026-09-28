package com.aimtester.api.stats;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final StatsService statsService;

    public LeaderboardController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public List<LeaderboardEntry> leaderboard(
            @RequestParam(name = "limit", required = false) Integer limit) {
        return statsService.leaderboard(limit);
    }
}
