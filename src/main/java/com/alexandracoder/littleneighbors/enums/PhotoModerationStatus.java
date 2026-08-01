package com.alexandracoder.littleneighbors.enums;

/**
 * Estado de moderación de la foto de perfil de una familia.
 *
 * Se guarda por separado de la propia URL de la foto: la URL puede existir
 * en Cloudinary desde el momento de la subida, pero solo se expone
 * públicamente (mapa, explorar, ficha de otra familia) cuando un admin la
 * ha revisado y aprobado. La propia familia siempre ve su foto, aprobada o
 * no, en su propio perfil.
 */
public enum PhotoModerationStatus {
    PENDING,
    APPROVED,
    REJECTED
}
