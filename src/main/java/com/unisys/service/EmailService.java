package com.unisys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.unisys.errors.EmailServiceException;

/**
 * Service responsible for handling email operations.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    /**
     * Constructs an instance of EmailService.
     *
     * @param mailSender the JavaMailSender object for sending emails.
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email.
     *
     * @param to      the recipient's email address.
     * @param subject the subject of the email.
     * @param body    the body of the email.
     * @throws IllegalArgumentException if the recipient's email is null or blank.
     * @throws EmailServiceException    if email sending fails.
     */
    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or empty.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Error sending email to {}", to, e);
            throw new EmailServiceException("Failed to send email to " + to, e);
        }
    }
}
