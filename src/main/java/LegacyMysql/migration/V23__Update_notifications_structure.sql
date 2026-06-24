-- V23__Create_notifications_table.sql


CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_family_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL, -- Enum: EVENT_CREATED, MATCH_CONFIRMED, etc.
    related_id BIGINT,          -- ID genérico para Eventos o Matches
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,

    -- Relación con la tabla de familias
    CONSTRAINT fk_notification_family
        FOREIGN KEY (recipient_family_id)
        REFERENCES families(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;