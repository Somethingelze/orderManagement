package com.some.orderservice.services.impl;

import com.some.orderservice.annotations.Loggable;
import com.some.orderservice.exceptions.UserNotFoundException;
import com.some.orderservice.mappers.UserMapper;
import com.some.orderservice.model.dto.Request.UserRequestDto;
import com.some.orderservice.model.dto.Responce.UserResponseDto;
import com.some.orderservice.model.entities.UserEntity;
import com.some.orderservice.model.enums.Role;
import com.some.orderservice.repositories.UserRepository;
import com.some.orderservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Loggable
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
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
                .role(Role.USER)
                .build();
        userRepository.save(userEntity);

        return userMapper.toUserResponseDto(userEntity);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto) {

        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userRequestDto.username());
                    user.setEmail(userRequestDto.email());
                    log.info("User with id: {} was updated", id);
                    return userMapper.toUserResponseDto(user);
                })
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not found"));
    }

    @Override
    @Transactional
    public UserResponseDto changeUserRole(Role role, UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not found"));
        userEntity.setRole(role);
        userRepository.save(userEntity);
        return userMapper.toUserResponseDto(userEntity);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void registerUser(String username, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        if (encodedPassword != null) {
            UserEntity user = UserEntity.builder()
                    .username(username)
                    .password(encodedPassword)
                    .role(Role.USER)
                    .build();

            userRepository.save(user);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean existsById(UUID id) {
        return userRepository.findById(id).isPresent();
    }

    @Override
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public UUID getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                    .getId();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User with username: " + username +  " doesn't exist"));
    }

    @Override
    public String getCurrentUserEmail() {
        UserEntity user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getEmail();
    }
}
