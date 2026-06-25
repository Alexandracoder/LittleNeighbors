package com.alexandracoder.littleneighbors.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendWelcomeEmail(String to, String firstName) {
        String subject = "Welcome to LittleNeighbors!";
        String text = "Hello " + firstName + ",\n\n" +
                "Welcome to our community! We are happy to have you with us.";
        sendEmail(to, subject, text);
    }

    @Override
    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Password Recovery";
        String resetLink = frontendUrl + "/reset-password/" + token;
        String text = "Hello,\n\n" +
                "You have requested to recover your password. Click the link below to proceed:\n" +
                resetLink + "\n\n" +
                "This link will expire in 15 minutes.";
        sendEmail(to, subject, text);
    }
}