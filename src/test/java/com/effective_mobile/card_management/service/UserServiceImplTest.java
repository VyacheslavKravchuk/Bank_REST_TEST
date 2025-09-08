package com.effective_mobile.card_management.service;

import com.effective_mobile.card_management.dto.UserDto;
import com.effective_mobile.card_management.entity.User;
import com.effective_mobile.card_management.repository.UserRepository;
import com.effective_mobile.card_management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.effective_mobile.card_management.enums.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("password");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setRole(ROLE_USER);

        user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(ROLE_USER);
    }

    @Test
    void registerNewUser_SuccessfulRegistration() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.registerNewUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerNewUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerNewUser(userDto);
        });

        assertEquals("Имя пользователя уже существует.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerNewUser_EmailAlreadyExists() {
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerNewUser(userDto);
        });

        assertEquals("Email уже существует.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
