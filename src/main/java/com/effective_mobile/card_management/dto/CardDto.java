package com.effective_mobile.card_management.dto;

import java.time.LocalDate;
import com.effective_mobile.card_management.enums.CardStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardDto {

    private Long id;

    @NotBlank(message = "Замаскированный номер карты не может быть пустым")
    private String cardNumberMasked;

    @NotBlank(message = "Имя владельца не может быть пустым")
    @Size(max = 100, message = "Имя владельца не должно превышать 100 символов")
    private String owner;

    @NotNull(message = "Срок действия карты не может быть пустым")
    @Future(message = "Срок действия карты должен быть в будущем")
    private LocalDate expiryDate;

    @NotNull(message = "Статус карты не может быть пустым")
    private CardStatus status;

    @NotNull(message = "Баланс не может быть пустым")
    @PositiveOrZero(message = "Баланс должен быть положительным числом или нулем")
    private Double balance;
}
