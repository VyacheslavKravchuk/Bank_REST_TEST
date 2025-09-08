package com.effective_mobile.card_management.dto;
import com.effective_mobile.card_management.enums.CardStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class CardUpdateDto {

    @NotNull(message = "Статус карты не может быть пустым")
    private CardStatus status;
}
