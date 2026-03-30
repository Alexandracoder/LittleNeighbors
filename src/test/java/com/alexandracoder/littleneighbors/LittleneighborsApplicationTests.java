package com.alexandracoder.littleneighbors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:littleneighbors-test;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "jwt.secret=8f42a6b2d1c5e9f8a3b7c4d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1"
})
class LittleneighborsApplicationTests {

    @Test
    void contextLoads() {
    }
}
