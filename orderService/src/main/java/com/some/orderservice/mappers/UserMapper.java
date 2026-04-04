package com.some.orderservice.mappers;

import com.some.orderservice.model.dto.Responce.UserResponseDto;
import com.some.orderservice.model.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseDto toUserResponseDto(UserEntity userEntity);
}
