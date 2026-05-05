package com.some.notificationservice.service.impl;

import com.some.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;


    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.debug("Email successfully sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendNotification(UUID userId, String message) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/orders",
                    message
            );
            log.info("WebSocket message sent to user {}", userId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message to user {}: {}", userId, e.getMessage());
        }
    }
}