package com.some.orderservice.mappers;

import com.some.grpc.inventory.OrderItemDto;
import com.some.orderservice.model.entities.Order;
import com.some.orderservice.model.entities.OrderItem;
import com.some.orderservice.model.event.OrderEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {BigDecimal.class, UUID.class})
public interface OrderMapper {

    OrderEvent toOrderEvent(Order order);

    @Mapping(target = "id", expression = "java(UUID.fromString(source.id()))")
    @Mapping(target = "price", expression = "java(BigDecimal.valueOf(source.price(), 2))")
    @Mapping(target = "sale", expression = "java(BigDecimal.valueOf(source.sale(), 2))")
    @Mapping(target = "totalPrice", expression = "java(BigDecimal.valueOf(source.price(), 2).subtract(BigDecimal.valueOf(source.sale(), 2)))")
    OrderItem toOrderItem (OrderItemDto source);
}
