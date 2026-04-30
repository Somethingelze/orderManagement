package com.some.notificationservice;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.service.EmailNotificationService;
import com.some.notificationservice.service.WebSocketNotificationService;
import com.some.notificationservice.service.impl.NotificationSenderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSenderServiceImplTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private NotificationSenderServiceImpl notificationSenderService;

    private UUID userId;
    private UUID orderId;
    private String userEmail;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        userEmail = "user@example.com";
    }

    @Test
    void shouldSendNotifications_WhenStatusIsRejected() {
        List<String> unavailable = List.of("Product1");
        OrderEvent event = createEvent(Status.REJECTED, unavailable);

        notificationSenderService.sendNotification(event);

        verify(emailNotificationService).sendDeclineEmail(userEmail, orderId, unavailable);
        verify(webSocketNotificationService).notifyUserDeclineOrder(userId, orderId, unavailable);
        verifyNoMoreInteractions(emailNotificationService, webSocketNotificationService);
    }

    @Test
    void shouldSendNotifications_WhenStatusIsPartialReserved() {
        List<String> unavailable = List.of("Product2");
        OrderEvent event = createEvent(Status.PARTIAL_RESERVED, unavailable);

        notificationSenderService.sendNotification(event);

        verify(emailNotificationService).sendConfirmationEmail(userEmail, orderId, unavailable);
        verify(webSocketNotificationService).notifyUserConfirmOrder(userId, orderId, unavailable);
        verifyNoMoreInteractions(emailNotificationService, webSocketNotificationService);
    }

    @Test
    void shouldSendNotifications_WhenStatusIsCreated() {
        OrderEvent event = createEvent(Status.CREATED, null);

        notificationSenderService.sendNotification(event);

        verify(emailNotificationService).sendConfirmationEmail(userEmail, orderId);
        verify(webSocketNotificationService).notifyUserConfirmOrder(userId, orderId);
        verifyNoMoreInteractions(emailNotificationService, webSocketNotificationService);
    }

    @Test
    void shouldDoNothing_WhenStatusIsSuccess() {
        OrderEvent event = createEvent(Status.SUCCESS, null);

        notificationSenderService.sendNotification(event);

        verifyNoInteractions(emailNotificationService, webSocketNotificationService);
    }

    @Test
    void shouldDoNothing_WhenStatusIsCollected() {
        OrderEvent event = createEvent(Status.COLLECTED, null);

        notificationSenderService.sendNotification(event);

        verifyNoInteractions(emailNotificationService, webSocketNotificationService);
    }

    @Test
    void shouldDoNothing_WhenStatusIsReserved() {
        OrderEvent event = createEvent(Status.RESERVED, null);

        notificationSenderService.sendNotification(event);

        verifyNoInteractions(emailNotificationService, webSocketNotificationService);
    }

    private OrderEvent createEvent(Status status, List<String> unavailable) {
        return OrderEvent.builder()
                .userId(userId)
                .orderId(orderId)
                .userEmail(userEmail)
                .status(status)
                .unavailableProducts(unavailable)
                .build();
    }
}