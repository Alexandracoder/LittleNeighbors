
ALTER TABLE matches
ADD COLUMN user_accepted BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN neighbor_accepted BOOLEAN NOT NULL DEFAULT FALSE;


UPDATE matches SET user_accepted = FALSE, neighbor_accepted = FALSE WHERE user_accepted IS NULL;
