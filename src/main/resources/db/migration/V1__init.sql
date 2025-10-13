-- V1__initial_schema.sql
-- Initial database schema for Little Neighbors application

-- Create cities table
CREATE TABLE cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create neighborhoods table
CREATE TABLE neighborhoods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    street_name VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20),
    city_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_neighborhoods_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE
);

-- Create indexes for neighborhoods
CREATE INDEX idx_neighborhoods_city_id ON neighborhoods(city_id);
CREATE INDEX idx_neighborhoods_postal_code ON neighborhoods(postal_code);

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index for users
CREATE INDEX idx_users_email ON users(email);

-- Create families table
CREATE TABLE families (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    representative_name VARCHAR(255),
    family_name VARCHAR(255),
    description TEXT NOT NULL,
    profile_picture_url VARCHAR(255),
    neighborhood_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_families_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_families_neighborhood FOREIGN KEY (neighborhood_id) REFERENCES neighborhoods(id) ON DELETE SET NULL
);

-- Create indexes for families
CREATE INDEX idx_families_user_id ON families(user_id);
CREATE INDEX idx_families_neighborhood_id ON families(neighborhood_id);

-- Create children table
CREATE TABLE children (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    birth_date DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    family_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_children_family FOREIGN KEY (family_id) REFERENCES families(id) ON DELETE CASCADE,
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE'))
);

-- Create indexes for children
CREATE INDEX idx_children_family_id ON children(family_id);
CREATE INDEX idx_children_birth_date ON children(birth_date);
CREATE INDEX idx_children_gender ON children(gender);

-- Create interests table
CREATE TABLE interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_interest_type CHECK (type IN ('SPORTS', 'ARTS', 'MUSIC', 'SCIENCE', 'TECHNOLOGY', 'OUTDOOR', 'OTHER'))
);

-- Create index for interests
CREATE INDEX idx_interests_type ON interests(type);
CREATE INDEX idx_interests_name ON interests(name);

-- Create child_interests junction table (many-to-many)
CREATE TABLE child_interests (
    child_id BIGINT NOT NULL,
    interest_id BIGINT NOT NULL,
    PRIMARY KEY (child_id, interest_id),
    CONSTRAINT fk_child_interests_child FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE,
    CONSTRAINT fk_child_interests_interest FOREIGN KEY (interest_id) REFERENCES interests(id) ON DELETE CASCADE
);

-- Create indexes for child_interests
CREATE INDEX idx_child_interests_child_id ON child_interests(child_id);
CREATE INDEX idx_child_interests_interest_id ON child_interests(interest_id);

-- Create matches table
CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    child_a_id BIGINT NOT NULL,
    child_b_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_matches_child_a FOREIGN KEY (child_a_id) REFERENCES children(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_child_b FOREIGN KEY (child_b_id) REFERENCES children(id) ON DELETE CASCADE,
    CONSTRAINT chk_match_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    CONSTRAINT chk_different_children CHECK (child_a_id != child_b_id)
);

-- Create indexes for matches
CREATE INDEX idx_matches_child_a_id ON matches(child_a_id);
CREATE INDEX idx_matches_child_b_id ON matches(child_b_id);
CREATE INDEX idx_matches_status ON matches(status);
-- Note: Uniqueness is enforced by the application/service layer to avoid duplicate matches
-- Alternative: add a composite unique constraint on (child_a_id, child_b_id)
CREATE UNIQUE INDEX idx_matches_unique_pair ON matches(child_a_id, child_b_id);