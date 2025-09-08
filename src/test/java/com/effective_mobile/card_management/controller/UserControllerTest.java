package com.effective_mobile.card_management.controller;


import com.effective_mobile.card_management.dto.AuthenticationResponse;
import com.effective_mobile.card_management.dto.UserDto;
import com.effective_mobile.card_management.security.UserDetailsServiceImpl;
import com.effective_mobile.card_management.service.UserService;
import com.effective_mobile.card_management.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static com.effective_mobile.card_management.enums.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.ArgumentMatchers.any;



public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private UserDto userDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Инициализируем UserDto с данными
        userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("password");
        userDto.setEmail("test@mail.ru");
        userDto.setFirstName("First Name");
        userDto.setLastName("Last Name");
        userDto.setRole(ROLE_USER);
    }

    @Test
    public void testRegisterUser_Success() {

        ResponseEntity<?> response = userController.registerUser(userDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Пользователь зарегистрирован", response.getBody());
        verify(userService, times(1)).registerNewUser(userDto);
    }

    @Test
    public void testGenerateToken_Success() throws Exception {

        UserDetails userDetails = new User("testUser", "password", new ArrayList<>());
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("testUser", "password", new ArrayList<>());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mockedToken"); // Передаем userDetails в generateToken

        ResponseEntity<?> response = userController.generateToken(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mockedToken", ((AuthenticationResponse) response.getBody()).getJwt());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername("testUser");
        verify(jwtUtil, times(1)).generateToken(userDetails);
    }


    @Test
    public void testGenerateToken_InvalidCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Неверные учетные данные"));

        ResponseEntity<?> response = userController.generateToken(userDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неверные учетные данные", response.getBody());
    }

    @Test
    public void testGenerateToken_AuthenticationFails() {
        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Неверные учетные данные"));

        ResponseEntity<?> response = userController.generateToken(userDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()); // Ожидаем 401, а не 404
        assertEquals("Неверные учетные данные", response.getBody()); // Ожидаем сообщение об аутентификации
    }

    @Test
    public void testGenerateToken_UserNotFoundAfterAuthentication() {
        Authentication authenticationResult = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationResult);
        when(userDetailsService.loadUserByUsername(userDto.getUsername())).thenReturn(null);

        ResponseEntity<?> response = userController.generateToken(userDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Пользователь не найден", response.getBody());
    }
}
