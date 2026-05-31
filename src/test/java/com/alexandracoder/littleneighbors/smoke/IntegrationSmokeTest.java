package com.alexandracoder.littleneighbors.smoke;

import com.alexandracoder.littleneighbors.qr.controller.AdminController;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import com.alexandracoder.littleneighbors.security.config.SecurityConfig;
import com.alexandracoder.littleneighbors.security.service.JwtService;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "jwt.secret=un_secreto_de_prueba_de_32_caracteres_minimo")
@DisplayName("AdminController - Smoke Test")
class IntegrationSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QrService qrService;


    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("Admin can access stats - 200 OK")
    @WithMockUser(roles = "ADMIN")
    void getStats_AsAdmin_Returns200() throws Exception {
        when(qrService.getAllNeighborhoodStats(anyList())).thenReturn(Map.of("Benimaclet", 10L));

        mockMvc.perform(get("/api/admin/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Family user should be forbidden (403)")
    @WithMockUser(roles = "FAMILY")
    void getStats_AsFamily_Returns403() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anonymous user should be unauthorized (401)")
    void getStats_Anonymous_Returns401() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
}