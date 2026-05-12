package com.some.orderservice.mappers;

import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.OrderItem;
import com.some.commonlib.model.event.OrderEvent;
import com.some.grpc.inventory.OrderItemDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OrderItemEntity;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {BigDecimal.class, UUID.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)

@Loggable
public interface OrderMapper {

    @Mapping(target = "unavailableProducts", source = "unavailableProductsIds")
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "eventId", expression = "java(UUID.randomUUID())")
    OrderEvent toOrderEvent(OrderEntity orderEntity);

    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "price", expression = "java(BigDecimal.valueOf(source.getPricePennies(), 2))")
    @Mapping(target = "sale", expression = "java(BigDecimal.valueOf(source.getSalePennies(), 2))")
    @Mapping(target = "totalPrice", expression = "java(BigDecimal.valueOf(source.getTotalPrice(), 2))")
    OrderItemEntity toOrderItemEntity (OrderItemDto source);

    @Mapping(target = "orderId", source = "id")
    OrderResponseDto toOrderResponseDto(OrderEntity orderEntity);

    @Mapping(target = "orderId", source = "order.id")
    OrderItem toOrderItem(OrderItemEntity orderItemEntity);

//    @AfterMapping
//    default void mapOrderItems(OrderRequestDto source, @MappingTarget ProductRequestDto.Builder target) {
//        if (source.orderItems() != null) {
//            target.putAllOrderItems(source.orderItems());
//        }
//    }
//
    @AfterMapping
    default void linkOrderItems(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getOrderItems() != null) {
            orderEntity.getOrderItems().forEach(item -> {
                item.setOrder(orderEntity);
            });
        }
    }
}
