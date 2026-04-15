package com.some.notificationservice.mapper;

import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.entity.Order;
import com.some.commonlib.model.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Slf4j
@Loggable
public abstract class OrderMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "totalPrice", source = "totalPrice")
    public abstract Order orderEventToOrderEntity(OrderEvent orderEvent);

}