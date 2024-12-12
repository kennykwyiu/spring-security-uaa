package com.kenny.uaa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "kenny.email-provider", name = "name", havingValue = "smtp")
@RequiredArgsConstructor
@Service
public class EmailServiceSmtpImpl implements EmailService {
    private final JavaMailSender emailSender;
    @Override
    public void send(String email, String msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("service@kenny.com");
        message.setSubject("kenny.com Practical Spring Security Login Verification Code");
        message.setText("Your verification code is: " + msg);
        emailSender.send(message);
    }
}
