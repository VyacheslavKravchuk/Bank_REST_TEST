package com.effective_mobile.card_management.dto;
import com.effective_mobile.card_management.enums.Role;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class UserDto {
    @NotBlank(message = "Имя владельца не может быть пустым")
    private String username;

    @NotBlank(message = "Пароль владельца не может быть пустым")
    private String password;

    @NotBlank(message = "Email владельца не может быть пустым")
    private String email;

    @NotBlank(message = "Имя владельца не может быть пустым")
    private String firstName;

    @NotBlank(message = "Имя владельца не может быть пустым")
    private String lastName;

    @NotBlank(message = "Роль владельца не может быть пустым")
    private Role role;
}