package com.effective_mobile.card_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("Ошибка: Неверный аргумент: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверные данные: " + e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        logger.error("Ошибка аутентификации: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные учетные данные");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        logger.error("Ресурс не найден: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        logger.error("Ошибка валидации: {}", body);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        logger.error("Карта не найдена: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Карта не найдена");
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundsException ex) {
        logger.error("Недостаточно средств: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Недостаточно средств для перевода");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Ошибка HTTP статуса: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка сервера. Пожалуйста, попробуйте позже.");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        logger.warn("Пользователь не найден: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
    }

}
