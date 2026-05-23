CREATE TABLE pilot_leads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    neighborhood VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL
);

-- Índice único para evitar duplicados en el mismo barrio (funciona igual en MySQL)
CREATE UNIQUE INDEX idx_pilot_leads_email_neighborhood ON pilot_leads(email, neighborhood);