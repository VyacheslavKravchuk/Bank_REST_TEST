package com.effective_mobile.card_management.dto;
import com.effective_mobile.card_management.enums.Role;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserDto {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть от 3 до 20 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя может содержать только буквы, цифры и подчеркивание")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат Email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Неверный формат Email")
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 50, message = "Имя не может превышать 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(max = 50, message = "Фамилия не может превышать 50 символов")
    private String lastName;

    @NotNull(message = "Роль не может быть пустой")
    private Role role;
}
