package com.alexandracoder.littleneighbors.email.service;

import com.alexandracoder.littleneighbors.shared.config.AppMailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource; // Importante
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Locale; // Importante

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final AppMailProperties mailProperties;
    private final MessageSource messageSource; // Inyección de MessageSource

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
    public void sendWelcomeEmail(String to, String firstName, Locale locale) {
        // Pasamos el locale al contexto para que Thymeleaf use el archivo correcto
        Context context = new Context(locale);
        context.setVariable("firstName", firstName);

        String html = templateEngine.process("welcome", context);

        // Obtenemos el asunto traducido usando messageSource
        String subject = messageSource.getMessage("welcome.subject", null, locale);

        sendEmail(to, subject, html);
    }

    @Override
    public void sendResetPasswordEmail(String to, String token, Locale locale) {
        Context context = new Context(locale);
        context.setVariable("resetLink", mailProperties.getFrontendUrl() + "/reset-password/" + token);
        String html = templateEngine.process("password-reset", context);

        String subject = messageSource.getMessage("password.subject", null, locale);

        sendEmail(to, subject, html);
    }
}