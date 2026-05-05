package com.some.authservice.controllers;

import com.some.authservice.model.dto.Request.UserRequestDto;
import com.some.authservice.model.dto.Response.UserResponseDto;
import com.some.authservice.model.enums.Role;
import com.some.authservice.services.UserService;
import com.some.commonlib.annotations.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Loggable
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponseDto findById(@PathVariable UUID id) {
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

    @PutMapping("/{id}/role")
    public UserResponseDto changeRole(@RequestParam Role role, @PathVariable UUID id) {
        return userService.changeUserRole(role, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
    }
}

