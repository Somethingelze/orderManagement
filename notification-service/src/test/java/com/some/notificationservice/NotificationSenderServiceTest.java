package com.some.notificationservice;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.notificationservice.service.EmailNotificationService;
import com.some.notificationservice.service.WebSocketNotificationService;
import com.some.notificationservice.service.impl.NotificationSenderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSenderServiceTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private NotificationSenderServiceImpl notificationSenderService;

    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID USER_ID = UUID.randomUUID();
    private final String EMAIL = "test@example.com";
    private final List<String> UNAVAILABLE_ITEMS = List.of("Product A", "Product B");

    @Test
    @DisplayName("Should send decline notifications when status is UNAVAILABLE")
    void shouldSendDeclineNotifications() {
        OrderEvent event = OrderEvent.builder()
                .id(ORDER_ID)
                .userId(USER_ID)
                .userEmail(EMAIL)
                .status(Status.UNAVAILABLE)
                .unavailableProducts(UNAVAILABLE_ITEMS)
                .build();

        notificationSenderService.sendNotification(event);

        verify(emailNotificationService).sendDeclineEmail(EMAIL, ORDER_ID, UNAVAILABLE_ITEMS);
        verify(webSocketNotificationService).notifyUserDeclineOrder(USER_ID, ORDER_ID, UNAVAILABLE_ITEMS);
    }

    @Test
    @DisplayName("Should send partial confirmation notifications when status is PARTIALLY_UNAVAILABLE")
    void shouldSendPartialConfirmationNotifications() {
        OrderEvent event = OrderEvent.builder()
                .id(ORDER_ID)
                .userId(USER_ID)
                .userEmail(EMAIL)
                .status(Status.PARTIALLY_UNAVAILABLE)
                .unavailableProducts(UNAVAILABLE_ITEMS)
                .build();

        notificationSenderService.sendNotification(event);

        verify(emailNotificationService).sendConfirmationEmail(EMAIL, ORDER_ID, UNAVAILABLE_ITEMS);
        verify(webSocketNotificationService).notifyUserConfirmOrder(USER_ID, ORDER_ID, UNAVAILABLE_ITEMS);
    }

    @Test
    @DisplayName("Should send simple confirmation for any other status")
    void shouldSendFullConfirmationNotifications() {
        OrderEvent event = OrderEvent.builder()
                .id(ORDER_ID)
                .userId(USER_ID)
                .userEmail(EMAIL)
                .status(Status.AVAILABLE)
                .build();

        notificationSenderService.sendNotification(event);

        verify(emailNotificationService).sendConfirmationEmail(EMAIL, ORDER_ID);
        verify(webSocketNotificationService).notifyUserConfirmOrder(USER_ID, ORDER_ID);
    }
}