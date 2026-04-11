package com.some.notificationservice.mapper;

import com.some.commonlib.annotations.Loggable;
import com.some.notificationservice.model.entity.Order;
import com.some.notificationservice.model.entity.OrderItem;
import com.some.notificationservice.model.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Slf4j
@Loggable
public abstract class OrderMapper {

    @Mapping(target = "id", source = "id")
    public abstract Order orderEventToOrderEntity(OrderEvent orderEvent);

//    @AfterMapping
//    protected void linkOrderItems(@MappingTarget Order order) {
//        if (order.orderItems() != null) {
//            order.orderItems().forEach(OrderItem::orderId
//        }
//    }
}