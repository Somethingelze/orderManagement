package com.some.notificationservice;

import com.some.commonlib.model.entity.Order;
import com.some.commonlib.model.entity.OrderItem;
import com.some.notificationservice.service.EmailNotificationService;
import com.some.notificationservice.service.WebSocketNotificationService;
import com.some.notificationservice.service.impl.NotificationSenderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Sender Logic Tests")
class NotificationSenderServiceTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private NotificationSenderServiceImpl notificationSenderService;

    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID USER_ID = UUID.randomUUID();
    private final String EMAIL = "user@example.com";

    @Test
    @DisplayName("Should send decline notification when all items are unavailable")
    void sendNotification_allUnavailable_callsDeclineMethods() {
        OrderItem item1 = OrderItem.builder()
                .productName("Product 1")
                .available(false)
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Product 2")
                .available(false)
                .build();

        Order order = new Order(ORDER_ID, USER_ID, EMAIL, List.of(item1, item2), BigDecimal.valueOf(20));

        notificationSenderService.sendNotification(order);

        List<String> expectedUnavailable = List.of("Product 1", "Product 2");
        verify(emailNotificationService).sendDeclineNotification(EMAIL, ORDER_ID, expectedUnavailable);
        verify(webSocketNotificationService).notifyUserDeclineOrder(USER_ID, ORDER_ID, expectedUnavailable);
    }

    @Test
    @DisplayName("Should send confirmation with warnings when some items are unavailable")
    void sendNotification_partiallyAvailable_callsConfirmationWithLists() {
        OrderItem item1 = OrderItem.builder()
                .productName("Available Product")
                .available(true)
                .build();

        OrderItem item2 = OrderItem.builder()
                .productName("Missing Product")
                .available(false)
                .build();

        Order order = new Order(ORDER_ID, USER_ID, EMAIL, List.of(item1, item2), BigDecimal.valueOf(20));

        notificationSenderService.sendNotification(order);

        List<String> expectedUnavailable = List.of("Missing Product");
        verify(emailNotificationService).sendOrderConfirmation(EMAIL, ORDER_ID, expectedUnavailable);
        verify(webSocketNotificationService).notifyUser(USER_ID, ORDER_ID, expectedUnavailable);
    }

    @Test
    @DisplayName("Should send simple confirmation when all items are available")
    void sendNotification_allAvailable_callsSimpleConfirmation() {
        OrderItem item = OrderItem.builder()
                .productName("Product 1")
                .available(true)
                .build();

        Order order = new Order(ORDER_ID, USER_ID, EMAIL, List.of(item), BigDecimal.TEN);

        notificationSenderService.sendNotification(order);

        verify(emailNotificationService).sendOrderConfirmation(EMAIL, ORDER_ID);
        verify(webSocketNotificationService).notifyUser(USER_ID, ORDER_ID);
    }

    @Test
    @DisplayName("Should correctly extract unavailable product names")
    void getUnavailableProductName_filtersCorrectly() {
        OrderItem item1 = OrderItem.builder().productName("A").available(true).build();
        OrderItem item2 = OrderItem.builder().productName("B").available(false).build();

        Order order = new Order(ORDER_ID, USER_ID, EMAIL, List.of(item1, item2), BigDecimal.ZERO);

        List<String> result = notificationSenderService.getUnavailableProductName(order);

        assertThat(result).containsExactly("B");
    }
}