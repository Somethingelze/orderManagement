package com.some.authservice.services.impl;


import com.some.authservice.exceptions.UserNotFoundException;
import com.some.authservice.mappers.UserMapper;
import com.some.authservice.model.dto.Request.UserRequestDto;
import com.some.authservice.model.dto.Response.UserResponseDto;
import com.some.authservice.model.entities.UserEntity;
import com.some.authservice.model.enums.Role;
import com.some.authservice.repositories.UserRepository;
import com.some.authservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).toList();
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
        log.info("Getting user by id: {}", id);
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        UserEntity userEntity = UserEntity.builder()
                .username(userRequestDto.username())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .email(userRequestDto.email())
                .role(Role.ROLE_USER).build();
        userRepository.save(userEntity);

        return userMapper.toUserResponseDto(userEntity);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto) {
        log.info("Start updating user by id: {}", id);

        return userRepository.findById(id).map(user -> {
            user.setUsername(userRequestDto.username());
            user.setEmail(userRequestDto.email());
            log.info("User with id: {} was updated", id);
            return userMapper.toUserResponseDto(user);
        }).orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not found"));
    }

    @Override
    @Transactional
    public UserResponseDto changeUserRole(Role role, UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not found"));
        userEntity.setRole(role);
        userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Override
    public void deleteUserById(UUID id) {
        log.info("Deleting user by id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}

