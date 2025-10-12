package com.alexandracoder.littleneighbors.shared;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
    @Getter
    @Setter
    @EqualsAndHashCode(of = "id")
    public abstract class BaseEntity implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        protected Long id;

        @CreationTimestamp
        @Column(updatable = false, nullable = false)
        protected LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(nullable = false)
        protected LocalDateTime updatedAt;
    }
