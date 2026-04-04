package com.some.notificationservice.mapper;

import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.event.OrderEvent;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class OrderMapper {

    @Mapping(target = "id", source = "id")
    public abstract OrderEntity orderEventToOrderEntity(OrderEvent orderEvent);

    @AfterMapping
    protected void linkOrderItems(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getOrderItems() != null) {
            orderEntity.getOrderItems().forEach(item -> {
                item.setOrder(orderEntity);
            });
        }
    }
}