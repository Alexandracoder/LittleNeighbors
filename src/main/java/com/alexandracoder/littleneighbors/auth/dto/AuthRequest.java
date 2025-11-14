package com.alexandracoder.littleneighbors.auth.dto;



public record AuthRequest(
        String email,
        String password
) {}
