package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.shared.exceptions.UserAlreadyExistsException;
import jakarta.validation.Valid;

public interface AuthService {
    void register(@Valid RegisterRequest request) throws UserAlreadyExistsException;
    AuthResponse login(AuthRequest request);
    UserProfileDTO getCurrentProfile(String email);
    AuthResponse reloadUserTokenFromRefresh(String refreshToken);
    void sendWelcomeEmail(String email, String firstName);
    void initiatePasswordReset(String email);
}