//package com.some.orderservice;
//
//import com.some.commonlib.model.UserPrincipal;
//import com.some.commonlib.model.OrderItem;
//import com.some.commonlib.model.enums.Status;
//import com.some.commonlib.model.event.OrderEvent;
//import com.some.grpc.inventory.OrderItemDto;
//import com.some.grpc.inventory.ProductRequestDto;
//import com.some.grpc.inventory.ProductResponseDto;
//import com.some.orderservice.grpc.InventoryGrpcClient;
//import com.some.orderservice.mappers.OrderMapper;
//import com.some.orderservice.model.dto.Request.OrderRequestDto;
//import com.some.orderservice.model.dto.Responce.OrderResponseDto;
//import com.some.orderservice.model.entities.OrderEntity;
//import com.some.orderservice.model.entities.OrderItemEntity;
//import com.some.orderservice.repositories.OrderRepository;
//import com.some.orderservice.services.impl.OrderServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @Mock
//    private InventoryGrpcClient inventoryClient;
//    @Mock
//    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
//    @Mock
//    private OrderMapper orderMapper;
//    @Mock
//    private OrderRepository orderRepository;
//
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    private UserPrincipal userPrincipal;
//
//    static final UUID ID = UUID.randomUUID();
//    static final String EMAIL = "test@email.com";
//
//    @BeforeEach
//    void setUp() {
//        userPrincipal = new UserPrincipal(UUID.randomUUID(), "test@example.com", "User");
//        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//
//    @Test
//    void processOrder_ShouldReturnResponseDto_WhenOrderIsValid() {
//        OrderRequestDto requestDto = OrderRequestDto.builder().build();
//        ProductRequestDto productRequestDto = ProductRequestDto.newBuilder().build();
//        OrderEntity orderEntity = OrderEntity.builder().id(UUID.randomUUID()).build();
//        OrderResponseDto expectedResponse = new OrderResponseDto(ID);
//
//        when(orderMapper.toProductRequestDto(requestDto)).thenReturn(productRequestDto);
//
//        ProductResponseDto productResponse = ProductResponseDto.newBuilder().build();
//        when(inventoryClient.checkAvailability(any())).thenReturn(productResponse);
//        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
//
//        when(orderMapper.toOrderResponseDto(orderEntity)).thenReturn(expectedResponse);
//
//        OrderResponseDto result = orderService.processOrder(requestDto);
//
//        assertThat(result).isEqualTo(expectedResponse);
//        verify(kafkaTemplate, times(1)).send(eq("order-event"), any());
//    }
//
//    @Test
//    void checkAvailability_ShouldCalculateTotalPriceCorrectly() {
//        ProductRequestDto productRequestDto = ProductRequestDto.newBuilder().build();
//
//        ProductResponseDto grpcResponse = ProductResponseDto.newBuilder()
//                .addAllItems(List.of(OrderItemDto.newBuilder().build()))
//                .build();
//        when(inventoryClient.checkAvailability(productRequestDto)).thenReturn(grpcResponse);
//
//        OrderItemEntity item = new OrderItemEntity();
//        item.setAvailable(true);
//        item.setTotalPrice(new BigDecimal("100.00"));
//
//        when(orderMapper.toOrderItemEntity(any())).thenReturn(item);
//        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        OrderEntity result = orderService.checkAvailability(productRequestDto);
//
//        assertThat(result.getTotalPrice()).isEqualByComparingTo("100.00");
//        assertThat(result.getUserId()).isEqualTo(userPrincipal.id());
//        verify(orderRepository).save(result);
//    }
//
//    @Test
//    void getUnavailableProducts_ShouldReturnOrderEntity_WhenStausIsUnavailable() {
//        OrderEntity orderEntity = OrderEntity.builder()
//                .id(ID)
//                .build();
//        OrderItemEntity item1 = OrderItemEntity.builder()
//                .productName("test1")
//                .available(false)
//                .build();
//        OrderItemEntity item2 = OrderItemEntity.builder()
//                .productName("test2")
//                .available(false)
//                .build();
//
//        orderEntity.addOrderItem(item1);
//        orderEntity.addOrderItem(item2);
//
//        List<String> unavailableProducts = List.of("test1", "test2");
//
//        assertThat(orderService.getUnavailableProductsName(orderEntity)).isEqualTo(unavailableProducts);
//        assertThat(orderService.setOrderStatus(orderEntity.getOrderItems())).isEqualTo(Status.UNAVAILABLE);
//
//    }
//
//    @Test
//    void sendOrderEvent_ShouldCallKafkaTemplate() {
//        List<OrderItemEntity> itemEntities = new ArrayList<>(List.of(new OrderItemEntity()));
//        List<OrderItem> items = itemEntities.stream()
//                .map(item -> orderMapper.toOrderItem(item))
//                .toList();
//        List<String> unavailableProducts = new ArrayList<>();
//
//        OrderEntity orderEntity = OrderEntity.builder()
//                .orderItems(itemEntities)
//                .build();
//        OrderEvent event = new OrderEvent(ID, ID, EMAIL, items, BigDecimal.TEN, Status.AVAILABLE, unavailableProducts);
//        when(orderMapper.toOrderEvent(orderEntity)).thenReturn(event);
//
//        orderService.sendOrderEvent(orderEntity);
//
//        verify(kafkaTemplate).send(eq("order-event"), event);
//    }
//}