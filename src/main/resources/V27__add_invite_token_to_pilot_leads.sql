-- V27__Add_invite_token_to_pilot_leads.sql

ALTER TABLE pilot_leads
ADD COLUMN invite_token VARCHAR(36) NOT NULL;

ALTER TABLE pilot_leads
ADD COLUMN converted_at DATETIME NULL;


CREATE UNIQUE INDEX idx_pilot_leads_invite_token ON pilot_leads(invite_token);