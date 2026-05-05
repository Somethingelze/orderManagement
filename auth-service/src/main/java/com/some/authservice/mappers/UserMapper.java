package com.some.authservice.mappers;


import com.some.authservice.model.dto.Response.UserResponseDto;
import com.some.authservice.model.entities.UserEntity;
import com.some.commonlib.annotations.Loggable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Loggable
public interface UserMapper {

    UserResponseDto toUserResponseDto(UserEntity userEntity);
}
