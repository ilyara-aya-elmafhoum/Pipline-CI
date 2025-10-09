package com.wesports.backend.infrastructure.email;

import com.wesports.backend.application.port.EmailService;
import com.wesports.backend.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email service implementation for sending transactional emails
 * Note: Currently uses plain text emails, HTML templates will be added in future iterations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean emailEnabled;

    @Value("${spring.mail.username:noreply@ilyara.com}")
    private String fromEmail;

    @Override
    public void sendRegistrationOtp(Email email, String otpCode, String language) {
        // Check if we have valid email configuration
        boolean hasValidConfig = emailEnabled && 
                                !fromEmail.equals("your-email@gmail.com") && 
                                !fromEmail.equals("noreply@ilyara.com");
        
        if (!hasValidConfig) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email.getValue());
            message.setSubject(getOtpSubject(language));
            message.setText(getOtpBody(otpCode, language));

            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send registration OTP email to: {}", email.getValue());
        }
    }

    @Override
    public void sendWelcomeEmail(Email email, String firstName, String language) {
        // Check if we have valid email configuration
        boolean hasValidConfig = emailEnabled && 
                                !fromEmail.equals("your-email@gmail.com") && 
                                !fromEmail.equals("noreply@ilyara.com");
                                
        if (!hasValidConfig) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email.getValue());
            message.setSubject(getWelcomeSubject(language));
            message.setText(getWelcomeBody(firstName, language));

            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send welcome email to: {}", email.getValue());
        }
    }

    private String getOtpSubject(String language) {
        return switch (language.toLowerCase()) {
            case "fr" -> "Code de vérification - Ilyara";
            case "ar" -> "رمز التحقق - إلْيارا";
            default -> "Verification Code - Ilyara";
        };
    }

    private String getWelcomeSubject(String language) {
        return switch (language.toLowerCase()) {
            case "fr" -> "Bienvenue sur Ilyara!";
            case "ar" -> "مرحباً بك في إلْيارا!";
            default -> "Welcome to Ilyara!";
        };
    }

    private String getOtpBody(String otp, String language) {
        return switch (language.toLowerCase()) {
            case "fr" -> String.format(
                "Votre code de vérification est: %s\n\n" +
                "Ce code expire dans 10 minutes.\n" +
                "Si vous n'avez pas demandé ce code, veuillez ignorer cet email.",
                otp
            );
            case "ar" -> String.format(
                "رمز التحقق الخاص بك هو: %s\n\n" +
                "هذا الرمز صالح لمدة 10 دقائق.\n" +
                "إذا لم تطلب هذا الرمز، يرجى تجاهل هذا البريد الإلكتروني.",
                otp
            );
            default -> String.format(
                "Your verification code is: %s\n\n" +
                "This code expires in 10 minutes.\n" +
                "If you didn't request this code, please ignore this email.",
                otp
            );
        };
    }

    private String getWelcomeBody(String firstName, String language) {
        return switch (language.toLowerCase()) {
            case "fr" -> String.format(
                "Bonjour %s,\n\n" +
                "Bienvenue sur Ilyara! Votre compte a été créé avec succès.\n\n" +
                "Nous sommes ravis de vous avoir parmi nous.\n\n" +
                "Cordialement,\n" +
                "L'équipe Ilyara",
                firstName
            );
            case "ar" -> String.format(
                "مرحباً %s،\n\n" +
                "مرحباً بك في إلْيارا! تم إنشاء حسابك بنجاح.\n\n" +
                "نحن سعداء لانضمامك إلينا.\n\n" +
                "مع أطيب التحيات،\n" +
                "فريق إلْيارا",
                firstName
            );
            default -> String.format(
                "Hello %s,\n\n" +
                "Welcome to Ilyara! Your account has been successfully created.\n\n" +
                "We're excited to have you on board.\n\n" +
                "Best regards,\n" +
                "The Ilyara Team",
                firstName
            );
        };
    }
}
