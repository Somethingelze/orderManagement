package com.some.orderservice.mappers;

import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponseDto toDto(OrderEntity orderEntity);

    OrderEntity toEntity(OrderResponseDto orderResponseDto);
}
