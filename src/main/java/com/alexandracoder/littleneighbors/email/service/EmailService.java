package com.alexandracoder.littleneighbors.email.service;

import java.util.Locale;

public interface EmailService {
    void sendEmail(String to, String subject, String htmlBody);
    void sendWelcomeEmail(String to, String firstName, Locale locale);
    void sendResetPasswordEmail(String to, String token, Locale locale);
    void sendVerificationEmail(String to, String token, Locale locale);
}