package com.alexandracoder.littleneighbors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    // BUG DE PRODUCCIÓN: el envío de emails (welcome/reset/verificación)
    // era síncrono dentro de la petición HTTP. Si el SMTP tarda o el
    // puerto saliente está bloqueado (frecuente en hostings como Render),
    // la petición se queda "pending" en el navegador hasta que expire el
    // timeout de SMTP (o indefinidamente si el timeout no se aplica bien
    // a nivel de plataforma) — exactamente el síntoma reportado en el
    // piloto: "he solicitado un forgot password y se queda pending, hay
    // payload pero no hay preview ni response". Ahora el envío de correo
    // se hace en este executor dedicado, así el hilo HTTP responde al
    // instante y el email se manda en segundo plano.
    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("mail-async-");
        executor.initialize();
        return executor;
    }
}
