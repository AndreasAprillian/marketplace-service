package org.acme.notification.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.notification.entity.EmailLog;
import org.acme.notification.repository.EmailLogRepository;

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
    }
}
