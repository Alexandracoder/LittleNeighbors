-- =====================================================
-- V1__init.sql  |  Inicialización del esquema principal
-- =====================================================

-- Tabla: neighborhoods
CREATE TABLE neighborhoods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL
);

-- Tabla: families
CREATE TABLE families (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    neighborhood_id BIGINT,
    CONSTRAINT fk_families_neighborhood
        FOREIGN KEY (neighborhood_id)
        REFERENCES neighborhoods(id)
);

-- Tabla: users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    family_id BIGINT,
    CONSTRAINT fk_users_family
        FOREIGN KEY (family_id)
        REFERENCES families(id)
);

-- Tabla: children
CREATE TABLE children (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    family_id BIGINT,
    CONSTRAINT fk_children_family
        FOREIGN KEY (family_id)
        REFERENCES families(id)
);

-- Tabla: interests
CREATE TABLE interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla intermedia: child_interest
CREATE TABLE child_interest (
    child_id BIGINT NOT NULL,
    interest_id BIGINT NOT NULL,
    PRIMARY KEY (child_id, interest_id),
    CONSTRAINT fk_child_interest_child
        FOREIGN KEY (child_id)
        REFERENCES children(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_child_interest_interest
        FOREIGN KEY (interest_id)
        REFERENCES interests(id)
        ON DELETE CASCADE
);

-- Tabla: matches
CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    child_a_id BIGINT NOT NULL,
    child_b_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_matches_child_a
        FOREIGN KEY (child_a_id)
        REFERENCES children(id),
    CONSTRAINT fk_matches_child_b
        FOREIGN KEY (child_b_id)
        REFERENCES children(id),
    CONSTRAINT chk_match_children CHECK (child_a_id <> child_b_id)
);

-- Índices recomendados
CREATE INDEX idx_family_neighborhood ON families(neighborhood_id);
CREATE INDEX idx_child_family ON children(family_id);
CREATE INDEX idx_match_children ON matches(child_a_id, child_b_id);

-- =====================================================
-- Fin de la migración inicial
-- =====================================================
