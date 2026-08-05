package com.alexandracoder.littleneighbors.qr;

import java.util.List;

/**
 * Único sitio donde se define la lista de barrios del piloto QR
 * (formulario público de captación de leads + panel de estadísticas del
 * admin). Antes esta lista estaba duplicada a mano en el frontend
 * (QrLandingPage.tsx) y en AdminController.java, así que cada barrio
 * nuevo había que añadirlo en dos sitios y era fácil olvidarse de uno
 * (como pasó con Nou Moles). Ahora solo se edita aquí: el frontend la
 * obtiene vía GET /api/public/pilot-lead/barrios en vez de tener su
 * propio array hardcodeado.
 *
 * Nota: esta lista es intencionadamente distinta de la tabla
 * `neighborhoods` (los 16 distritos oficiales usados al registrar una
 * familia) — son los barrios "de calle" elegidos para la campaña de
 * captación con QR, y pueden no coincidir 1:1 con los distritos
 * administrativos.
 */
public final class PilotBarrios {

    private PilotBarrios() {
    }

    public static final List<String> BARRIOS = List.of(
            "Benimaclet", "Ruzafa", "Arrancapins", "Cabañal", "Velluters",
            "Nou Moles", "El Carmen", "Patraix", "Campanar", "La Xerea"
    );
}