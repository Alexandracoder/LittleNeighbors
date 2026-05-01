package com.alexandracoder.littleneighbors.child;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.service.ChildService;
import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.alexandracoder.littleneighbors.child.controller.ChildController.class)
@ActiveProfiles("test")
@DisplayName("ChildController — POST /api/children")
class ChildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildService childService;

    private ObjectMapper objectMapper;
    private ChildResponseDTO stubResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        stubResponse = new ChildResponseDTO(
                42L,
                "Pepito",
                LocalDate.of(2020, 6, 15),
                null,
                LifeStage.BORN,
                Gender.BOY,
                List.of(),
                false,
                false,
                10L,
                5L
        );
    }

    @Nested
    @DisplayName("Given a valid request")
    class ValidRequests {

        @Test
        @WithMockUser(username = "parent@test.com", roles = {"FAMILY"})
        @DisplayName("should return 201 Created when description is valid (FAMILY role)")
        void createChild_asFamily_returns201() throws Exception {
            ChildRequestDTO requestBody = new ChildRequestDTO(
                    LocalDate.of(2020, 6, 15), null, LifeStage.BORN, Gender.BOY, Set.of(), false, "Valid bio"
            );

            when(childService.create(any(ChildRequestDTO.class), eq("parent@test.com"))).thenReturn(stubResponse);

            mockMvc.perform(post("/api/children")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        @DisplayName("should return 201 Created when user has ADMIN role (support capability)")
        void createChild_asAdmin_returns201() throws Exception {
            when(childService.create(any(ChildRequestDTO.class), eq("admin@test.com"))).thenReturn(stubResponse);

            String jsonBody = """
                {
                    "lifeStage": "BORN",
                    "birthDate": "2020-06-15",
                    "gender": "BOY",
                    "interestIds": [],
                    "isPrenatal": false,
                    "description": "Admin creating this for a family"
                }
                """;

            mockMvc.perform(post("/api/children")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Given an invalid request")
    class InvalidRequests {

        @Test
        @WithMockUser(roles = {"FAMILY"})
        @DisplayName("should return 400 when description exceeds 500 characters")
        void createChild_tooLongDescription_returns400() throws Exception {
            String tooLong = "A".repeat(501);
            String jsonBody = """
                {
                    "lifeStage": "BORN",
                    "birthDate": "2020-06-15",
                    "gender": "BOY",
                    "interestIds": [],
                    "isPrenatal": false,
                    "description": "%s"
                }
                """.formatted(tooLong);

            mockMvc.perform(post("/api/children")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.description").exists());
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication is provided")
        void createChild_noAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/children")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }
}