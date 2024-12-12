package com.kenny.uaa.service;

import com.sendgrid.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@ConditionalOnProperty(prefix = "kenny.email-provider", name = "name", havingValue = "api")
@RequiredArgsConstructor
@Service
public class EmailServiceApiImpl implements EmailService {
    private final SendGrid sendGrid;
    @Override
    public void send(String email, String msg) {
        Email from = new Email("service@kenny.com");
        String subject = "kenny.com Practical Spring Security Login Verification Code";
        Email to = new Email(email);
        Content content = new Content("text/plain", "Your verification code is: " + msg);
        Mail mail = new Mail(from, subject, to, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() == 202) {
                log.info("Email sent successfully");
            } else {
                log.error(response.getBody());
            }
        } catch (IOException e) {
            log.error("Exception occurred during request: {}", e.getLocalizedMessage());
        }
    }
}
