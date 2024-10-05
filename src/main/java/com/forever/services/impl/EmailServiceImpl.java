package com.forever.services.impl;

import com.forever.entities.User;
import com.forever.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendMail(User user) {
        String subject = "Verify your email";
        String senderName = "Forever Ecommerce";
        String mailContent = "Hello " + user.getFirstName() + ",\n";
        mailContent += "Your verification code: " + user.getVerificationCode() + "\n";
        mailContent += "Please enter this code to verify your email." + "\n";
        mailContent += "\nBest regards,\n" + senderName;

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setSubject(subject);
            mailMessage.setTo(user.getEmail());
            mailMessage.setText(mailContent);

            javaMailSender.send(mailMessage);
            logger.info("Email sent successfully to {}", user.getEmail());
        } catch (MailException e) {
            logger.error("Error while sending email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
