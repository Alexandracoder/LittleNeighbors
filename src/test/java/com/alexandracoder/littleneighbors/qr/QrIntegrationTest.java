package com.alexandracoder.littleneighbors.qr;

import com.alexandracoder.littleneighbors.email.service.EmailService;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import(com.alexandracoder.littleneighbors.config.TestMailConfig.class)
@TestPropertySource(properties = {
        "JWT_SECRET=this-is-a-very-long-and-secure-secret-key-at-least-thirty-two-bytes-long",
        "ALLOWED_ORIGINS=http://localhost:5173"
})
public class QrIntegrationTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private EmailService emailService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("littleneighbors_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private QrService qrService;

    @Test
    void shouldCountLeadsCaseInsensitiveAndTrimmed() {

        qrService.saveLead("familia1@test.com", "Benimaclet", true, "v1");
        qrService.saveLead("familia2@test.com", "benimaclet", true, "v1");
        qrService.saveLead("familia3@test.com", "  BENIMACLET  ", true, "v1");

        long count = qrService.countLeadsByNeighborhood("Benimaclet");

        assertThat(count).isEqualTo(3);
    }
}