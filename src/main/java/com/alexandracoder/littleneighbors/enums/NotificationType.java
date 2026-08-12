package com.alexandracoder.littleneighbors.enums;

public enum NotificationType {
    CHAT_MESSAGE,
    MATCH_CONFIRMED,
    MATCH_SUCCESS,
    EVENT_CREATED,
    EVENT_UPDATED,
    EVENT_CANCELLED,
    EVENT_ATTENDANCE_CONFIRMED,
    PLAYDATE_REQUEST,
    PLAYDATE_REJECTED,
    // Antes MatchServiceImpl reutilizaba PLAYDATE_REQUEST para esto, pero
    // es un concepto distinto (solicitud de conexión entre familias, sin
    // título de plan) y necesita su propia plantilla de texto.
    MATCH_REQUEST,
    SYSTEM
}
