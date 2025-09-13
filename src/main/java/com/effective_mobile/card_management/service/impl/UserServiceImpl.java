package com.effective_mobile.card_management.service.impl;
import com.effective_mobile.card_management.dto.UserDto;
import com.effective_mobile.card_management.entity.User;
import com.effective_mobile.card_management.enums.Role;
import com.effective_mobile.card_management.repository.UserRepository;
import com.effective_mobile.card_management.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerNewUser(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Имя пользователя уже существует.");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email уже существует.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRole(userDto.getRole());

        userRepository.save(user);
    }
}
