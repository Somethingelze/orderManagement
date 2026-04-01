package com.some.notificationservice.mapper;

import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.event.OrderEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderEntity orderEventToOrderEntity(OrderEvent orderEvent);

}
