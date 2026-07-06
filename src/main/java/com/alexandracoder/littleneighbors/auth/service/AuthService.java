package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.shared.exceptions.UserAlreadyExistsException;
import jakarta.validation.Valid;

import java.util.Locale;

public interface AuthService {
    void register(@Valid RegisterRequest request) throws UserAlreadyExistsException;
    AuthResponse login(AuthRequest request);
    UserProfileDTO getCurrentProfile(String email);
    AuthResponse reloadUserTokenFromRefresh(String refreshToken);

    void sendWelcomeEmail(String email, String firstName, Locale locale);

    void initiatePasswordReset(String email, Locale locale);

    void resetPassword(String token, String newPassword);
}