package org.acme.email.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.email.entity.EmailLog;
import org.acme.email.repository.EmailLogRepository;

import java.time.LocalDateTime;

@ApplicationScoped
public class EmailService {

    @Inject
    EmailLogRepository emailLogRepository;

    @Transactional
    public void sendEmail(String orderId,String to, String subject, String body) {
        if (emailLogRepository.count("orderId", orderId) > 0){
            return;
        }
        System.out.println("Sending email to: " + to + ", subject: " + subject);
        EmailLog log = EmailLog.builder()
                .orderId(orderId)
                .recipient(to)
                .subject(subject)
                .body(body)
                .status("SENT")
                .createdAt(LocalDateTime.now())
                .build();
        EmailLog.persist(log);

        System.out.println("notification sent !");
    }
}
