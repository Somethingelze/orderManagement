package com.some.orderservice.controllers;

import com.some.orderservice.model.dto.Request.UserRequestDto;
import com.some.orderservice.model.dto.Responce.UserResponseDto;
import com.some.orderservice.model.enums.Role;
import com.some.orderservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponseDto findById(@RequestParam UUID id) {
            return userService.getUserById(id);
        }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
        }

    @PostMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable UUID id, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(id, userRequestDto);
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@RequestParam Role role, @PathVariable UUID id) {
        return userService.changeUserRole(role, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
    }
}

