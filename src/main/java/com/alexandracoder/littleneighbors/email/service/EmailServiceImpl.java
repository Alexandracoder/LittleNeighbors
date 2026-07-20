package com.alexandracoder.littleneighbors.email.service;

import com.alexandracoder.littleneighbors.shared.config.AppMailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource; // Importante
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Locale; // Importante

@Service
@RequiredArgsConstructor
@Slf4j
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
    @Async("mailExecutor")
    public void sendWelcomeEmail(String to, String firstName, Locale locale) {
        // @Async: antes esta llamada bloqueaba el hilo HTTP hasta que el
        // SMTP terminaba (o colgaba). Ahora se ejecuta en segundo plano;
        // por eso hace falta capturar y loguear aquí dentro cualquier
        // fallo, ya que una excepción en un método @Async void no puede
        // propagarse de vuelta al código que llamó (AuthServiceImpl).
        try {
            // Pasamos el locale al contexto para que Thymeleaf use el archivo correcto
            Context context = new Context(locale);
            context.setVariable("firstName", firstName);

            String html = templateEngine.process("welcome", context);

            // Obtenemos el asunto traducido usando messageSource
            String subject = messageSource.getMessage("welcome.subject", null, locale);

            sendEmail(to, subject, html);
        } catch (Exception e) {
            log.error("FALLO AL ENVIAR EMAIL DE BIENVENIDA a {} - revisar configuración SMTP " +
                    "(SPRING_MAIL_HOST/USERNAME/PASSWORD) y APP_MAIL_FROM_ADDRESS en el entorno: {}",
                    to, e.getMessage(), e);
        }
    }

    @Override
    @Async("mailExecutor")
    public void sendResetPasswordEmail(String to, String token, Locale locale) {
        try {
            Context context = new Context(locale);
            context.setVariable("resetLink", mailProperties.getFrontendUrl() + "/reset-password/" + token);
            String html = templateEngine.process("password-reset", context);

            String subject = messageSource.getMessage("password.subject", null, locale);

            sendEmail(to, subject, html);
        } catch (Exception e) {
            log.error("FALLO AL ENVIAR EMAIL DE RECUPERACIÓN DE CONTRASEÑA a {} - revisar configuración SMTP " +
                    "(SPRING_MAIL_HOST/USERNAME/PASSWORD) y APP_MAIL_FROM_ADDRESS en el entorno: {}",
                    to, e.getMessage(), e);
        }
    }

    @Override
    @Async("mailExecutor")
    public void sendVerificationEmail(String to, String token, Locale locale) {
        try {
            Context context = new Context(locale);
            context.setVariable("verifyLink", mailProperties.getFrontendUrl() + "/verify-email/" + token);
            String html = templateEngine.process("verify-email", context);

            String subject = messageSource.getMessage("verify.subject", null, locale);

            sendEmail(to, subject, html);
        } catch (Exception e) {
            log.error("FALLO AL ENVIAR EMAIL DE VERIFICACIÓN a {} - revisar configuración SMTP " +
                    "(SPRING_MAIL_HOST/USERNAME/PASSWORD) y APP_MAIL_FROM_ADDRESS en el entorno: {}",
                    to, e.getMessage(), e);
        }
    }
}