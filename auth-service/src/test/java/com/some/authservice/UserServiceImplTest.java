package com.some.authservice;

import com.some.authservice.exceptions.UserNotFoundException;
import com.some.authservice.mappers.UserMapper;
import com.some.authservice.model.dto.Request.UserRequestDto;
import com.some.authservice.model.dto.Response.UserResponseDto;
import com.some.authservice.model.entities.UserEntity;
import com.some.authservice.model.enums.Role;
import com.some.authservice.repositories.UserRepository;
import com.some.authservice.services.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID USER_ID = UUID.randomUUID();
    private final String USERNAME = "test_user";
    private final String EMAIL = "test@example.com";

    @Test
    @DisplayName("Should return all users mapped to DTOs")
    void getAllUsers_returnsDtoList() {
        UserEntity user = new UserEntity();
        UserResponseDto responseDto = new UserResponseDto(USERNAME, EMAIL, Role.ROLE_USER);

        given(userRepository.findAll()).willReturn(List.of(user));
        given(userMapper.toUserResponseDto(user)).willReturn(responseDto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo(USERNAME);
    }

    @Nested
    @DisplayName("Get User by ID")
    class GetUserByIdTests {
        @Test
        void getUserById_found_returnsDto() {
            UserEntity user = new UserEntity();
            UserResponseDto responseDto = new UserResponseDto(USERNAME, EMAIL, Role.ROLE_USER);

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
            given(userMapper.toUserResponseDto(user)).willReturn(responseDto);

            UserResponseDto result = userService.getUserById(USER_ID);

            assertThat(result.username()).isEqualTo(USERNAME);
        }

        @Test
        void getUserById_notFound_throwsUsernameNotFoundException() {
            given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(USER_ID))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    @Test
    @DisplayName("Should encode password and save new user")
    void createUser_encodesAndSaves() {
        UserRequestDto request = new UserRequestDto(USERNAME, "raw_password", EMAIL);
        UserResponseDto responseDto = new UserResponseDto(USERNAME, EMAIL, Role.ROLE_USER);

        given(passwordEncoder.encode("raw_password")).willReturn("encoded_password");
        given(userMapper.toUserResponseDto(any(UserEntity.class))).willReturn(responseDto);

        UserResponseDto result = userService.createUser(request);

        assertThat(result.username()).isEqualTo(USERNAME);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUserTests {
        @Test
        void updateUser_found_updatesFieldsAndReturnsDto() {
            UserRequestDto updateRequest = new UserRequestDto("new_name", null, "new@example.com");
            UserEntity existingUser = UserEntity.builder()
                    .id(USER_ID)
                    .username(USERNAME)
                    .email(EMAIL)
                    .build();
            UserResponseDto responseDto = new UserResponseDto("new_name", "new@example.com", Role.ROLE_USER);

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(existingUser));
            given(userMapper.toUserResponseDto(existingUser)).willReturn(responseDto);

            UserResponseDto result = userService.updateUser(USER_ID, updateRequest);

            assertThat(result.username()).isEqualTo("new_name");
            assertThat(existingUser.getUsername()).isEqualTo("new_name");
            assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        void updateUser_notFound_throwsUserNotFoundException() {
            UserRequestDto request = new UserRequestDto(USERNAME, null, EMAIL);
            given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(USER_ID, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Should change role and save user")
    void changeUserRole_updatesRoleAndSaves() {
        UserEntity user = UserEntity.builder().id(USER_ID).role(Role.ROLE_USER).build();
        UserResponseDto responseDto = new UserResponseDto(USERNAME, EMAIL, Role.ROLE_ADMIN);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));
        given(userMapper.toUserResponseDto(user)).willReturn(responseDto);

        UserResponseDto result = userService.changeUserRole(Role.ROLE_ADMIN, USER_ID);

        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should call repository delete method")
    void deleteUserById_callsRepository() {
        userService.deleteUserById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    @DisplayName("Should return true if username exists")
    void existsByUsername_checksRepository() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(new UserEntity()));

        boolean exists = userService.existsByUsername(USERNAME);

        assertThat(exists).isTrue();
        verify(userRepository).findByUsername(USERNAME);
    }
}