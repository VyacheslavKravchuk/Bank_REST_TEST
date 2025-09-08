package com.effective_mobile.card_management.controller;
import com.effective_mobile.card_management.dto.CardCreateDto;
import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.dto.CardUpdateDto;
import com.effective_mobile.card_management.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.effective_mobile.card_management.enums.CardStatus.ACTIVE;
import static com.effective_mobile.card_management.enums.CardStatus.EXPIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private AdminController adminController;

    private CardDto cardDto;
    private CardCreateDto cardCreateDto;
    private CardUpdateDto cardUpdateDto;
    private Long cardId;

    @BeforeEach
    void setUp() {
        cardId = 1L;

        cardDto = new CardDto();
        cardDto.setId(cardId);
        cardDto.setCardNumberMasked("5167-****-****-1234");
        cardDto.setBalance(1000.50);
        cardDto.setOwner("John Smith");
        cardDto.setExpiryDate(LocalDate.parse("2026-12-31"));
        cardDto.setStatus(ACTIVE);

        cardCreateDto = new CardCreateDto();
        cardCreateDto.setExpiryDate(LocalDate.parse("2026-12-31"));
        cardCreateDto.setOwner("John Smith");
        cardCreateDto.setBalance(1000.50);
        cardCreateDto.setUserId(cardId);

        cardUpdateDto = new CardUpdateDto();
        cardUpdateDto.setStatus(EXPIRED);
    }

    @Test
    void createCard_ShouldReturnOkAndCardDto() {
        when(cardService.createCard(cardCreateDto)).thenReturn(cardDto);

        ResponseEntity<CardDto> response = adminController.createCard(cardCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cardDto, response.getBody());
        verify(cardService, times(1)).createCard(cardCreateDto);
    }

    @Test
    void updateCard_ShouldReturnOkAndCardDto() {
        when(cardService.updateCard(cardId, cardUpdateDto)).thenReturn(cardDto);

        ResponseEntity<CardDto> response = adminController.updateCard(cardId, cardUpdateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cardDto, response.getBody());
        verify(cardService, times(1)).updateCard(cardId, cardUpdateDto);
    }

    @Test
    void deleteCard_ShouldReturnNoContent() {
        ResponseEntity<Void> response = adminController.deleteCard(cardId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cardService, times(1)).deleteCard(cardId);
    }

    @Test
    void getAllCards_ShouldReturnOkAndListOfCardDto() {
        List<CardDto> cardList = Arrays.asList(cardDto, new CardDto());
        when(cardService.getAllCards()).thenReturn(cardList);

        ResponseEntity<List<CardDto>> response = adminController.getAllCards();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cardList, response.getBody());
        verify(cardService, times(1)).getAllCards();
    }

    @Test
    void blockCard_ShouldReturnOk() {
        ResponseEntity<Void> response = adminController.blockCard(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cardService, times(1)).blockCard(cardId);
    }

    @Test
    void activateCard_ShouldReturnOk() {
        ResponseEntity<Void> response = adminController.activateCard(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cardService, times(1)).activateCard(cardId);
    }
}
