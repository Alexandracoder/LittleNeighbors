package com.alexandracoder.littleneighbors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=un_secreto_de_prueba_de_32_caracteres_minimo",
        "jwt.expiration=3600000"
})
class LittleneighborsApplicationTests {

    @Test
    void contextLoads() {

    }
}