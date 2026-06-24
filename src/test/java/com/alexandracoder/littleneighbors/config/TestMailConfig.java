package com.alexandracoder.littleneighbors.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.mock;

@Import(TestMailConfig.class)
@TestConfiguration
public class TestMailConfig {

    @Bean
    @Primary
    public JavaMailSender mockJavaMailSender() {
        return mock(JavaMailSender.class);
    }
}