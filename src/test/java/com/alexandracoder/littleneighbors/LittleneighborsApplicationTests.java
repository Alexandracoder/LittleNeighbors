package com.alexandracoder.littleneighbors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // <--- Esto le dice a Spring: "Busca la sección 'test' en application.yml"
class LittleneighborsApplicationTests {

    @Test
    void contextLoads() {

    }
}