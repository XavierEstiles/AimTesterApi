package com.aimtester.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.aimtester.api.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void saveMatchShouldPersistAndComputeAccuracy() throws Exception {
        String username = register("jugador-partida");

        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"mode":"classic_30s","durationSeconds":30,"hits":42,"misses":7}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mode").value("classic_30s"))
                .andExpect(jsonPath("$.durationSeconds").value(30))
                .andExpect(jsonPath("$.hits").value(42))
                .andExpect(jsonPath("$.misses").value(7))
                .andExpect(jsonPath("$.accuracy").value(85.71));
    }

    @Test
    void saveMatchShouldRequireAuthentication() throws Exception {
        MvcResult result = mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hits":1,"misses":0}
                                """))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 401 || status == 403, "Se esperaba 401 o 403, llegó " + status);
    }

    @Test
    void saveMatchShouldRejectNegativeHits() throws Exception {
        String username = register("jugador-negativo");

        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hits":-1,"misses":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void historyShouldReturnMatchesNewestFirst() throws Exception {
        String username = register("jugador-historial");

        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"durationSeconds":60,"hits":10,"misses":5}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"durationSeconds":30,"hits":20,"misses":5}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/matches").header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].durationSeconds").value(30))
                .andExpect(jsonPath("$[1].durationSeconds").value(60));
    }

    @Test
    void historyShouldNotReturnMatchesFromOtherUsers() throws Exception {
        String first = register("jugador-aislado-1");
        String second = register("jugador-aislado-2");

        mockMvc.perform(post("/matches")
                        .header("Authorization", token(first))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hits":3,"misses":1}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/matches").header("Authorization", token(second)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void historyShouldPaginateMatchesNewestFirst() throws Exception {
        String username = register("jugador-paginas");

        // Empezadas hace 60, 45 y 30 segundos: ordenado por startedAt desc
        // queda 30s, 45s, 60s (el DATETIME solo guarda segundos).
        save(username, 60, 10, 0);
        save(username, 45, 20, 0);
        save(username, 30, 30, 0);

        mockMvc.perform(get("/matches")
                        .param("limit", "2")
                        .param("page", "0")
                        .header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hits").value(30))
                .andExpect(jsonPath("$[1].hits").value(20));

        mockMvc.perform(get("/matches")
                        .param("limit", "2")
                        .param("page", "1")
                        .header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].hits").value(10));
    }

    @Test
    void historyShouldTreatNegativePageAsTheFirstOne() throws Exception {
        String username = register("jugador-pagina-negativa");

        save(username, 5, 1);

        mockMvc.perform(get("/matches")
                        .param("page", "-3")
                        .header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void historyShouldReturnAnEmptyListBeyondTheLastPage() throws Exception {
        String username = register("jugador-pagina-vacia");

        save(username, 5, 1);

        mockMvc.perform(get("/matches")
                        .param("limit", "1")
                        .param("page", "9")
                        .header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void statsShouldAggregateOwnMatches() throws Exception {
        String username = register("jugador-stats");

        save(username, 40, 10);
        save(username, 10, 0);

        mockMvc.perform(get("/player/stats").header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchesPlayed").value(2))
                .andExpect(jsonPath("$.totalHits").value(50))
                .andExpect(jsonPath("$.totalMisses").value(10))
                .andExpect(jsonPath("$.accuracy").value(83.33))
                .andExpect(jsonPath("$.bestScore").value(40))
                .andExpect(jsonPath("$.avgScore").value(25.00))
                .andExpect(jsonPath("$.lastMatchAt").exists());
    }

    @Test
    void statsByModeShouldGroupMatchesPerMode() throws Exception {
        String username = register("jugador-modos");

        save(username, "classic_30s", 40, 10);
        save(username, "precision_30s", 10, 0);
        save(username, "precision_30s", 25, 5);

        mockMvc.perform(get("/player/stats/by-mode").header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].mode").value("classic_30s"))
                .andExpect(jsonPath("$[0].matchesPlayed").value(1))
                .andExpect(jsonPath("$[0].bestScore").value(40))
                .andExpect(jsonPath("$[0].accuracy").value(80.00))
                .andExpect(jsonPath("$[1].mode").value("precision_30s"))
                .andExpect(jsonPath("$[1].matchesPlayed").value(2))
                .andExpect(jsonPath("$[1].bestScore").value(25))
                .andExpect(jsonPath("$[1].totalHits").value(35))
                .andExpect(jsonPath("$[1].totalMisses").value(5))
                .andExpect(jsonPath("$[1].accuracy").value(87.5))
                .andExpect(jsonPath("$[1].avgScore").value(17.50));
    }

    @Test
    void statsByModeShouldRequireAuthentication() throws Exception {
        MvcResult result = mockMvc.perform(get("/player/stats/by-mode")).andReturn();

        int status = result.getResponse().getStatus();
        assertTrue(status == 401 || status == 403, "Se esperaba 401 o 403, llegó " + status);
    }

    @Test
    void leaderboardShouldIncludePlayerWithPosition() throws Exception {
        String username = register("jugador-ranking");

        save(username, 7, 3);

        MvcResult result = mockMvc.perform(get("/leaderboard")
                        .param("limit", "100")
                        .header("Authorization", token(username)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("\"username\":\"" + username + "\""), "El jugador no aparece en el ranking: " + body);
        assertTrue(body.contains("\"matchesPlayed\":1"), "No se contó la partida: " + body);
        assertTrue(body.contains("\"accuracy\":70.0"), "Precisión incorrecta en el ranking: " + body);
    }

    private void save(String username, int hits, int misses) throws Exception {
        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hits\":" + hits + ",\"misses\":" + misses + "}"))
                .andExpect(status().isCreated());
    }

    private void save(String username, String mode, int hits, int misses) throws Exception {
        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mode\":\"" + mode + "\",\"hits\":" + hits + ",\"misses\":" + misses + "}"))
                .andExpect(status().isCreated());
    }

    /** Guarda una partida empezada hace {@code durationSeconds} segundos. */
    private void save(String username, int durationSeconds, int hits, int misses) throws Exception {
        mockMvc.perform(post("/matches")
                        .header("Authorization", token(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationSeconds\":" + durationSeconds + ",\"hits\":" + hits
                                + ",\"misses\":" + misses + "}"))
                .andExpect(status().isCreated());
    }

    private String token(String username) {
        return "Bearer " + jwtService.generateToken(username);
    }

    /** Crea la cuenta y devuelve su nombre de usuario. */
    private String register(String username) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"email\":\"" + username
                                + "@aimtester.dev\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated());
        return username;
    }
}
