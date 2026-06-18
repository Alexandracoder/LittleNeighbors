ALTER TABLE events ADD COLUMN creator_family_id BIGINT;

UPDATE events SET creator_family_id = 1 WHERE creator_family_id IS NULL;

ALTER TABLE events MODIFY COLUMN creator_family_id BIGINT NOT NULL;
ALTER TABLE events ADD CONSTRAINT fk_events_creator_family
FOREIGN KEY (creator_family_id) REFERENCES families(id);