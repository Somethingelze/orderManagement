package com.some.notificationservice.mapper;

import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.entity.OrderItemEntity;
import com.some.notificationservice.model.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Slf4j
public abstract class OrderMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", ignore = true)
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