package com.alexandracoder.littleneighbors.auth.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {}
