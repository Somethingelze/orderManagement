package com.some.orderservice.mappers;

import com.some.grpc.inventory.OrderItemDto;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.entities.Order;
import com.some.orderservice.model.entities.OrderItem;
import com.some.orderservice.model.event.OrderEvent;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {BigDecimal.class, UUID.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface OrderMapper {

    OrderEvent toOrderEvent(Order order);

    @Mapping(target = "name", source = "productName")
    @Mapping(target = "productId", expression = "java(UUID.fromString(source.getProductId()))")
    @Mapping(target = "price", expression = "java(BigDecimal.valueOf(source.getPricePennies(), 2))")
    @Mapping(target = "sale", expression = "java(BigDecimal.valueOf(source.getSalePennies(), 2))")
    @Mapping(target = "totalPrice", expression = "java(BigDecimal.valueOf(source.getPricePennies(), 2).subtract(BigDecimal.valueOf(source.getSalePennies(), 2)))")
    OrderItem toOrderItem (OrderItemDto source);

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "orderItems", ignore = true)
    ProductRequestDto toProductRequestDto(OrderRequestDto orderRequestDto);

    @AfterMapping
    default void mapOrderItems(OrderRequestDto source, @MappingTarget ProductRequestDto.Builder target) {
        if (source.getOrderItems() != null) {
            target.putAllOrderItems(source.getOrderItems());
        }
    }
}
