-- V16: Simplemente preparamos el terreno sin nombres conflictivos
-- Borramos cualquier residuo si existiera
ALTER TABLE children DROP CHECK chk_gender;

-- Actualizamos datos
UPDATE children SET gender = 'PREGNANT' WHERE gender IS NULL;

-- Añadimos el check SIN nombre para que MySQL no se queje de duplicados
ALTER TABLE children ADD CHECK (gender IN ('BOY', 'GIRL', 'PREGNANT'));