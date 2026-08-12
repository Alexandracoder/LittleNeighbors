-- =====================================================================
-- V20__add_notification_params.sql
--
-- Antes cada notificación guardaba su título/mensaje ya escritos en un
-- idioma fijo (mezcla de español e inglés según el archivo que la creara),
-- sin relación con el idioma que la persona tiene elegido en la app. A
-- partir de ahora solo se guarda el tipo + los datos variables (nombre de
-- quien la genera, título del evento/plan...) en param1/param2, y el
-- frontend construye el texto con i18next en el idioma activo — igual que
-- el resto de la interfaz.
--
-- title/message se dejan de rellenar en notificaciones nuevas, pero no se
-- borran ni se hacen NOT NULL -> false: las notificaciones antiguas ya
-- guardadas se siguen mostrando tal cual (ver NotificationBell.tsx).
-- =====================================================================

ALTER TABLE notifications ADD COLUMN param1 VARCHAR(255);
ALTER TABLE notifications ADD COLUMN param2 VARCHAR(255);
ALTER TABLE notifications ALTER COLUMN title DROP NOT NULL;
ALTER TABLE notifications ALTER COLUMN message DROP NOT NULL;
