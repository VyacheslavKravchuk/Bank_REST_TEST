package com.effective_mobile.card_management.controller;

import com.effective_mobile.card_management.dto.AuthenticationResponse;
import com.effective_mobile.card_management.dto.UserDto;
import com.effective_mobile.card_management.security.UserDetailsServiceImpl;
import com.effective_mobile.card_management.service.UserService;
import com.effective_mobile.card_management.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

/**
 * Класс контроллер для пользователей
 */

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос (например, неверный формат данных)"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        logger.info("Запрос регистрации");
        try {
            userService.registerNewUser(userDto);
            return ResponseEntity.ok("Пользователь зарегистрирован");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при регистрации пользователя: {}", e.getMessage());
            return ResponseEntity.status(400).body("Неверные данные пользователя: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Внутренняя ошибка сервера при регистрации пользователя: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера");
        }
    }


    @Operation(summary = "Аутентификация пользователя и генерация токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация, возвращается JWT токен"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody UserDto userDto) {
        try {
            logger.info("Запрос аутентификации");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
            );
        } catch (AuthenticationException e) {
            logger.error("Ошибка аутентификации для пользователя {}: {}", userDto.getUsername(),
                    e.getMessage(), e);
            return ResponseEntity.status(401).body("Неверные учетные данные");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());

        if (userDetails == null) {
            logger.warn("Пользователь {} не найден", userDto.getUsername());
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        final String jwt = jwtUtil.generateToken(userDetails);

        if (!jwtUtil.validateToken(jwt, userDetails)) {
            logger.error("Сгенерированный JWT недействителен сразу после создания!");
            return ResponseEntity.status(500).body("Ошибка создания токена"); // Или другое подходящее сообщение об ошибке
        }

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
