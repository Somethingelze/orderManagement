package com.some.notificationservice;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.factory.NotificationMessageFactory;
import com.some.notificationservice.model.NotificationContent;
import com.some.notificationservice.service.impl.NotificationSenderServiceImpl;
import com.some.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class NotificationSenderServicesTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationMessageFactory messageFactory;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationSenderServiceImpl notificationSenderService;

    private UUID userId;
    private UUID orderId;
    private String userEmail;
    private NotificationContent testContent;

    @BeforeEach
    void setUp() {
        notificationSenderService = new NotificationSenderServiceImpl(notificationService, messageFactory);
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        userEmail = "test@example.com";
        testContent = new NotificationContent("Subject", "Body", "WS Message");
    }

    @Test
    void sendNotification_CreatedStatus_ShouldSendEmailAndWebSocket() {
        OrderEvent event = createEvent(Status.CREATED);
        Mockito.when(messageFactory.buildConfirmation(orderId)).thenReturn(testContent);

        notificationSenderService.sendNotification(event);

        Mockito.verify(mailSender, Mockito.times(1)).send(ArgumentMatchers.any(SimpleMailMessage.class));
        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSendToUser(
                ArgumentMatchers.eq(userId.toString()),
                ArgumentMatchers.eq("/queue/orders"),
                ArgumentMatchers.eq("WS Message")
        );
    }

    @Test
    void sendNotification_SuccessStatus_ShouldSendOnlyWebSocket() {
        OrderEvent event = createEvent(Status.SUCCESS);
        Mockito.when(messageFactory.buildSuccess(orderId)).thenReturn(testContent);

        notificationSenderService.sendNotification(event);

        Mockito.verify(mailSender, Mockito.never()).send(ArgumentMatchers.any(SimpleMailMessage.class));
        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSendToUser(
                ArgumentMatchers.eq(userId.toString()),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
        );
    }

    @Test
    void sendNotification_PartialReserved_ShouldSendEmailWithUnavailableProducts() {
        List<String> unavailable = List.of("Product1");
        OrderEvent event = createEvent(Status.PARTIAL_RESERVED, unavailable);
        Mockito.when(messageFactory.buildPartialConfirmation(orderId, unavailable)).thenReturn(testContent);

        notificationSenderService.sendNotification(event);

        Mockito.verify(mailSender, Mockito.times(1)).send(ArgumentMatchers.any(SimpleMailMessage.class));
        Mockito.verify(messageFactory).buildPartialConfirmation(orderId, unavailable);
    }

    @Test
    void sendNotification_ErrorStatus_NoAction() {
        OrderEvent event = createEvent(Status.ERROR);

        notificationSenderService.sendNotification(event);

        Mockito.verifyNoInteractions(mailSender);
        Mockito.verifyNoInteractions(messagingTemplate);
    }

    @Test
    void sendEmail_HandleException_ShouldNotThrow() {
        Mockito.doThrow(new RuntimeException("Mail error")).when(mailSender).send(ArgumentMatchers.any(SimpleMailMessage.class));

        Assertions.assertDoesNotThrow(() ->
                notificationService.sendEmail(userEmail, "Subject", "Body")
        );
    }

    private OrderEvent createEvent(Status status) {
        return createEvent(status, List.of());
    }

    private OrderEvent createEvent(Status status, List<String> unavailable) {
        return new OrderEvent(
                UUID.randomUUID(),
                orderId,
                status,
                userId,
                userEmail,
                BigDecimal.ZERO,
                List.of(),
                unavailable
        );
    }
}