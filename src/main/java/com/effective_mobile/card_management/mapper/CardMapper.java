package com.effective_mobile.card_management.mapper;

import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.entity.Card;
import com.effective_mobile.card_management.util.CardNumberMasker;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDto toDto(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setId(card.getId());
        cardDto.setCardNumberMasked(CardNumberMasker.maskCardNumber(card.getCardNumber()));
        cardDto.setOwner(card.getOwner());
        cardDto.setExpiryDate(card.getExpiryDate());
        cardDto.setStatus(card.getStatus());
        cardDto.setBalance(card.getBalance());
        return cardDto;
    }
}