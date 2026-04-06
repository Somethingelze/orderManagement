package com.some.orderservice.services;

import com.some.orderservice.model.dto.Request.UserRequestDto;
import com.some.orderservice.model.dto.Responce.UserResponseDto;
import com.some.orderservice.model.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    List<UserResponseDto> getAllUsers();

    boolean existsByUsername(String username);

    boolean existsById(UUID id);

    void deleteUserById(UUID id);

    UserResponseDto getUserById(UUID id);

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto);

    UserResponseDto changeUserRole(Role role, UUID id);

    void deleteUser(UUID id);

    void registerUser(String username, String rawPassword);

    UserDetails loadUserByUsername(String username);

    UUID getCurrentUserId();

    String getCurrentUserEmail();
}
