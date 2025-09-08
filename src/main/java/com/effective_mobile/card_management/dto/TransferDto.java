package com.effective_mobile.card_management.dto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Data
public class TransferDto {

    @NotNull(message = "Идентификатор не может быть пустым")
    @Positive(message = "Идентификатор должен быть положительным числом")
    private Long fromCardId;

    @NotNull(message = "Идентификатор не может быть пустым")
    @Positive(message = "Идентификатор должен быть положительным числом")
    private Long toCardId;

    @NotNull(message = "Сумма перевода не может быть пустой")
    @Positive(message = "Сумма перевода должна быть положительным числом")
    private Double amount;
}