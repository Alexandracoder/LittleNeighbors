package com.alexandracoder.littleneighbors.qr.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pilot_leads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QrEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String inviteToken;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String neighborhood;

    private LocalDateTime convertedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.inviteToken == null) {
            this.inviteToken = UUID.randomUUID().toString();
        }
    }
}