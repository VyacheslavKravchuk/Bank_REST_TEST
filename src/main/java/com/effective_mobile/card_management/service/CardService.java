package com.effective_mobile.card_management.service;
import com.effective_mobile.card_management.dto.CardCreateDto;
import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.dto.CardUpdateDto;
import com.effective_mobile.card_management.dto.TransferDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
    CardDto createCard(CardCreateDto cardCreateDto);

    CardDto updateCard(Long id, CardUpdateDto cardUpdateDto);

    void deleteCard(Long id);

    CardDto getCardById(Long id);

    List<CardDto> getAllCards();

    Page<CardDto> getUserCards(String username, Pageable pageable);

    void transferFunds(String username, TransferDto transferDto);

    Double getCardBalance(String username, Long cardId);

    void blockCard(Long cardId);

    void activateCard(Long cardId);

    void requestBlockCard(String username, Long cardId);
}