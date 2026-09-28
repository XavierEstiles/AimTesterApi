package com.aimtester.api.match;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<MatchResponse> save(@RequestBody(required = false) SaveMatchRequest request,
            Authentication authentication) {
        MatchResponse response = matchService.save(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Historial paginado del jugador autenticado, del más reciente al más antiguo.
     *
     * @param limit partidas por página (por defecto 20, máximo 100)
     * @param page  página solicitada, empezando en 0
     */
    @GetMapping
    public List<MatchResponse> history(Authentication authentication,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "page", required = false) Integer page) {
        return matchService.history(authentication.getName(), limit, page);
    }
}
