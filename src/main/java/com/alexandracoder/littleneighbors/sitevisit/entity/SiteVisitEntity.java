package com.alexandracoder.littleneighbors.sitevisit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Cada fila es una visita a la web. sessionId es un UUID anónimo generado
// en el navegador (localStorage), NO es una cookie de tracking ni contiene
// datos personales: solo sirve para poder distinguir "visitas totales" de
// "visitantes únicos" sin identificar a nadie. Por eso no requiere aviso
// de cookies ni consentimiento RGPD adicional (a diferencia del consentimiento
// que sí se pide para el registro de familias).
@Entity
@Table(name = "site_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteVisitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    private String path;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
