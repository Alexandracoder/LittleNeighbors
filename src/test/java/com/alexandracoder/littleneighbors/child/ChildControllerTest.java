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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@code POST /api/children} — description field validation.
 *
 * <p>Uses {@code @WebMvcTest} to load only the web layer (controller + security filters).
 * The {@code ChildService} is mocked so no database interaction occurs.
 *
 * <p>All test assertions and log messages are in English as required.
 */
@WebMvcTest(
        controllers = com.alexandracoder.littleneighbors.child.controller.ChildController.class
)
@ActiveProfiles("test")
@DisplayName("ChildController — POST /api/children (description field)")
class ChildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildService childService;

    private ObjectMapper objectMapper;

    // ── A realistic stub response returned by the mocked service ────────────
    private ChildResponseDTO stubResponse;

    @BeforeEach
    void setUpObjectMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        stubResponse = new ChildResponseDTO(
                42L,
                LocalDate.of(2020, 6, 15),
                null,                      // dueDate
                LifeStage.BORN,
                Gender.BOY,
                List.of(),                 // interests
                false,                     // isPrenatal
                false,                     // pregnancySupport
                10L,                       // familyId
                5L                         // familyUserId
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HAPPY PATH
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Given a valid request")
    class ValidRequest {

        @Test
        @WithMockUser(username = "parent@test.com", roles = {"FAMILY"})
        @DisplayName("should return 200 OK when description is within 500 characters")
        void createChild_withValidDescription_returns200() throws Exception {

            ChildRequestDTO requestBody = new ChildRequestDTO(
                    LocalDate.of(2020, 6, 15),
                    null,
                    LifeStage.BORN,
                    Gender.BOY,
                    Set.of(),
                    false,
                    "A curious and energetic child who loves painting."
            );

            when(childService.create(any(ChildRequestDTO.class), eq("parent@test.com")))
                    .thenReturn(stubResponse);

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42));
        }

        @Test
        @WithMockUser(username = "parent@test.com", roles = {"FAMILY"})
        @DisplayName("should return 200 OK when description is exactly 500 characters (boundary)")
        void createChild_withDescriptionAtBoundary_returns200() throws Exception {

            String exactly500Chars = "A".repeat(500);

            ChildRequestDTO requestBody = new ChildRequestDTO(
                    LocalDate.of(2020, 6, 15),
                    null,
                    LifeStage.BORN,
                    Gender.BOY,
                    Set.of(),
                    false,
                    exactly500Chars
            );

            when(childService.create(any(ChildRequestDTO.class), eq("parent@test.com")))
                    .thenReturn(stubResponse);

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "parent@test.com", roles = {"FAMILY"})
        @DisplayName("should return 200 OK when description is null (optional field)")
        void createChild_withNullDescription_returns200() throws Exception {

            ChildRequestDTO requestBody = new ChildRequestDTO(
                    LocalDate.of(2020, 6, 15),
                    null,
                    LifeStage.BORN,
                    Gender.BOY,
                    Set.of(),
                    false,
                    null   // description is optional
            );

            when(childService.create(any(ChildRequestDTO.class), eq("parent@test.com")))
                    .thenReturn(stubResponse);

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isOk());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDATION FAILURES
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Given an invalid request")
    class InvalidRequest {

        @Test
        @WithMockUser(username = "parent@test.com", roles = {"FAMILY"})
        @DisplayName("should return 400 Bad Request when description exceeds 500 characters")
        void createChild_withDescriptionOver500Chars_returns400WithMessage() throws Exception {

            // 501 characters — one over the limit
            String tooLong = "B".repeat(501);

            // We build the JSON manually to bypass the DTO constructor validation
            // (the @Size annotation is evaluated by Spring's @Valid, not the constructor).
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

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.description")
                            .value("Description cannot exceed 500 characters"));
        }

        @Test
        @WithMockUser(username = "parent@test.com", roles = {"FAMILY"})
        @DisplayName("should return 400 Bad Request when description is 1000 characters")
        void createChild_withDescriptionOf1000Chars_returns400() throws Exception {

            String wayTooLong = "C".repeat(1000);

            String jsonBody = """
                    {
                        "lifeStage": "BORN",
                        "birthDate": "2020-06-15",
                        "gender": "GIRL",
                        "interestIds": [],
                        "isPrenatal": false,
                        "description": "%s"
                    }
                    """.formatted(wayTooLong);

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(containsString("Validation Failed")));
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication is provided")
        void createChild_withoutAuthentication_returns401() throws Exception {

            String jsonBody = """
                    {
                        "lifeStage": "BORN",
                        "birthDate": "2020-06-15",
                        "gender": "BOY",
                        "interestIds": [],
                        "isPrenatal": false,
                        "description": "Valid description"
                    }
                    """;

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonBody)
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        @DisplayName("should return 403 Forbidden when user has ADMIN role instead of FAMILY")
        void createChild_withAdminRole_returns403() throws Exception {

            String jsonBody = """
                    {
                        "lifeStage": "BORN",
                        "birthDate": "2020-06-15",
                        "gender": "BOY",
                        "interestIds": [],
                        "isPrenatal": false,
                        "description": "Valid description"
                    }
                    """;

            mockMvc.perform(
                            post("/api/children")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonBody)
                    )
                    .andExpect(status().isForbidden());
        }
    }
}
