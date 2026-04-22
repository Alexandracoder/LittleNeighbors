package com.alexandracoder.littleneighbors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=esta_es_una_clave_de_prueba_muy_larga_para_que_el_test_no_falle_12345",
        "jwt.expiration=3600000"
})
class LittleneighborsApplicationTests {

    @Test
    void contextLoads() {

    }
}