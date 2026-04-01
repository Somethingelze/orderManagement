package com.some.orderservice.services;

import com.some.orderservice.model.dto.Request.UserRequestDto;
import com.some.orderservice.model.dto.Responce.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(UUID id);
    boolean existsByUsername(String username);
    boolean existsById(UUID id);
    void deleteUserById(UUID id);


    UserResponseDto createUser(UserRequestDto userRequestDto, String rawPassword);

    UserResponseDto updateUser(UUID id, UserRequestDto user);

    void registerUser(String username, String rawPassword);
    UserDetails getUserByUsername(String username) throws UsernameNotFoundException;

    UUID getCurrentUserId();
}
