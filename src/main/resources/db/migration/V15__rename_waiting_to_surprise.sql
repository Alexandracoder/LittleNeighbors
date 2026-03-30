-- V15__rename_waiting_to_surprise.sql

-- 1. Actualizamos los registros existentes que estuvieran en 'WAITING_PROFILE'
UPDATE families SET status = 'SURPRISE' WHERE status = 'WAITING_PROFILE';

-- 2. Cambiamos el valor por defecto de la columna para futuros registros
ALTER TABLE families ALTER COLUMN status SET DEFAULT 'SURPRISE';