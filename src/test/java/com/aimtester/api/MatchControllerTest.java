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
