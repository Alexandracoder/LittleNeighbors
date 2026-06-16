CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    neighborhood_id BIGINT,
    CONSTRAINT fk_event_neighborhood
        FOREIGN KEY (neighborhood_id)
        REFERENCES neighborhoods(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_event_location ON events(latitude, longitude);
CREATE INDEX idx_event_date ON events(event_date);

