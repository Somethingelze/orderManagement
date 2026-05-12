package com.some.authservice.services;

import com.some.authservice.model.dto.Request.UserRequestDto;
import com.some.authservice.model.dto.Response.UserResponseDto;
import com.some.authservice.model.enums.Role;
import com.some.authservice.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

public interface UserService {


    boolean existsByUsername(String username);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(UUID id);

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto);

    UserResponseDto changeUserRole(Role role, UUID id);

    void deleteUserById(UUID id);
}