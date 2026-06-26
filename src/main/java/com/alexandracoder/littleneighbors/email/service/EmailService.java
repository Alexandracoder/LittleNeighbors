package com.alexandracoder.littleneighbors.email.service;

public interface EmailService {
    void sendEmail(String to, String subject, String htmlBody);
    void sendWelcomeEmail(String to, String firstName);
    void sendResetPasswordEmail(String to, String token);
}