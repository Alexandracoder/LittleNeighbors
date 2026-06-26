package com.alexandracoder.littleneighbors.email.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);

    void sendWelcomeEmail(String to, String firstName);

    void sendResetPasswordEmail(String to, String token);
}