package com.alexandracoder.littleneighbors.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "app.mail")
@Getter @Setter
@Validated
public class AppMailProperties {
    @NotBlank
    private String fromAddress;
    @NotBlank
    private String frontendUrl;
}