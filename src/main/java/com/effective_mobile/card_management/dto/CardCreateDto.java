package com.effective_mobile.card_management.dto;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
public class CardCreateDto {

    @NotBlank(message = "Имя владельца не может быть пустым")
    @Size(max = 100, message = "Имя владельца не должно превышать 100 символов")
    private String owner;

    @NotNull(message = "Срок действия карты не может быть пустым")
    @Future(message = "Срок действия карты должен быть в будущем")
    private LocalDate expiryDate;

    @NotNull(message = "Баланс не может быть пустым")
    @PositiveOrZero(message = "Баланс должен быть положительным числом или нулем")
    private Double balance;

    @NotNull(message = "Идентификатор не может быть пустым")
    @Positive(message = "Идентификатор должен быть положительным числом")
    private Long userId;
}