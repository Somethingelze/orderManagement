package com.some.orderservice.mappers;

import com.some.orderservice.model.dto.Request.UserRequestDto;
import com.some.orderservice.model.dto.Responce.UserResponseDto;
import com.some.orderservice.model.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {


    @Mapping(target = "password", ignore = true)
    UserResponseDto toResponseDto(UserEntity userEntity);

    UserEntity toEntityFromResponseDto(UserResponseDto userResponseDto);
    UserEntity toEntityFromRequestDto(UserRequestDto userRequestDto);
    UserResponseDto toUserResponseDto(UserEntity userEntity);


    default UserEntity updateEntity(UserEntity source, UserEntity target) {
        target.setPassword(source.getPassword());
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        return target;
    }
}
