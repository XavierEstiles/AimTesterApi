package com.aimtester.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void loginShouldReturnToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginShouldAllowCorsPreflightFromVite() throws Exception {
        mockMvc.perform(options("/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void authenticatedRequestShouldReturnRenewedToken() throws Exception {
        MvcResult firstResponse = mockMvc.perform(get("/player/me")
                        .header("Origin", "http://localhost:5173")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(header().exists("X-Auth-Token"))
            .andExpect(header().string("Access-Control-Expose-Headers", "X-Auth-Token"))
            .andReturn();

        mockMvc.perform(get("/player/me")
                .header("Authorization", "Bearer " + firstResponse.getResponse().getHeader("X-Auth-Token")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }
}
