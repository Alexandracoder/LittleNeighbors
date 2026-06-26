package com.alexandracoder.littleneighbors.email.service;

import com.alexandracoder.littleneighbors.shared.config.AppMailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final AppMailProperties mailProperties;

    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String firstName) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        String html = templateEngine.process("welcome", context);
        sendEmail(to, "Welcome to LittleNeighbors!", html);
    }

    @Override
    public void sendResetPasswordEmail(String to, String token) {
        Context context = new Context();
        context.setVariable("resetLink", mailProperties.getFrontendUrl() + "/reset-password/" + token);
        String html = templateEngine.process("password-reset", context);
        sendEmail(to, "Password Recovery", html);
    }
}