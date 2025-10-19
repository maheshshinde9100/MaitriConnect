package com.maitriconnect.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public void sendVerificationEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - MaitriConnect");
            message.setText("Hello " + username + ",\n\n" +
                    "Thank you for registering with MaitriConnect!\n\n" +
                    "Please verify your email by clicking the link below:\n" +
                    frontendUrl + "/verify-email?token=VERIFICATION_TOKEN\n\n" +
                    "If you didn't create this account, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "MaitriConnect Team");
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request - MaitriConnect");
            message.setText("Hello,\n\n" +
                    "We received a request to reset your password.\n\n" +
                    "Please click the link below to reset your password:\n" +
                    frontendUrl + "/reset-password?token=" + resetToken + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "MaitriConnect Team");
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
        }
    }
}
