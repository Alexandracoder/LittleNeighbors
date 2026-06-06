package com.alexandracoder.littleneighbors.auth.dto;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String inviteToken
) {
}
