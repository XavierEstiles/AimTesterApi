package com.aimtester.api.match;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimtester.api.user.User;
import com.aimtester.api.user.UserRepository;

@Service
public class MatchService {

    private static final int DEFAULT_DURATION_SECONDS = 30;
    private static final int MAX_DURATION_SECONDS = 3600;
    private static final int MAX_MODE_LENGTH = 30;
    private static final int MAX_HISTORY_LIMIT = 100;

    private final GameMatchRepository gameMatchRepository;
    private final UserRepository userRepository;

    public MatchService(GameMatchRepository gameMatchRepository, UserRepository userRepository) {
        this.gameMatchRepository = gameMatchRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MatchResponse save(String username, SaveMatchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Falta el cuerpo de la petición.");
        }

        int hits = requireNonNegative(request.hits(), "hits");
        int misses = requireNonNegative(request.misses(), "misses");
        int duration = request.durationSeconds() == null ? DEFAULT_DURATION_SECONDS : request.durationSeconds();
        if (duration < 1 || duration > MAX_DURATION_SECONDS) {
            throw new IllegalArgumentException("durationSeconds debe estar entre 1 y " + MAX_DURATION_SECONDS + ".");
        }

        String mode = request.mode() == null || request.mode().isBlank()
                ? "classic_30s"
                : request.mode().trim();
        if (mode.length() > MAX_MODE_LENGTH) {
            throw new IllegalArgumentException("mode no puede superar " + MAX_MODE_LENGTH + " caracteres.");
        }

        User user = findUser(username);
        LocalDateTime finishedAt = LocalDateTime.now();
        LocalDateTime startedAt = finishedAt.minusSeconds(duration);

        GameMatch match = gameMatchRepository.save(
                new GameMatch(user, mode, startedAt, finishedAt, duration, hits, misses));

        return MatchResponse.from(match);
    }

    /**
     * Historial paginado de más reciente a más antiguo. {@code page} es 0-based y
     * las páginas fuera de rango devuelven una lista vacía en vez de fallar.
     */
    @Transactional(readOnly = true)
    public List<MatchResponse> history(String username, Integer limit, Integer page) {
        int pageLimit = limit == null ? 20 : Math.min(Math.max(limit, 1), MAX_HISTORY_LIMIT);
        int pageIndex = page == null ? 0 : Math.max(page, 0);
        User user = findUser(username);

        return gameMatchRepository
                .findByUserIdOrderByStartedAtDesc(user.getId(), PageRequest.of(pageIndex, pageLimit))
                .stream()
                .map(MatchResponse::from)
                .toList();
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado ya no existe."));
    }

    private int requireNonNegative(Integer value, String field) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(field + " debe ser un número mayor o igual a 0.");
        }
        return value;
    }
}
