package com.alexandracoder.littleneighbors.qr;

import com.alexandracoder.littleneighbors.qr.service.QrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class QrIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("littleneighbors_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private QrService qrService;

    @Test
    void shouldCountLeadsCaseInsensitiveAndTrimmed() {

        qrService.saveLead("familia1@test.com", "Benimaclet");
        qrService.saveLead("familia2@test.com", "benimaclet");
        qrService.saveLead("familia3@test.com", "  BENIMACLET  ");

        long count = qrService.countLeadsByNeighborhood("Benimaclet");

        assertThat(count).isEqualTo(3);
    }
}