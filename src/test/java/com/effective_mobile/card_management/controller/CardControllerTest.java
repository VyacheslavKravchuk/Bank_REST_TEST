package com.effective_mobile.card_management.controller;

import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.dto.TransferDto;
import com.effective_mobile.card_management.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @Mock
    private Principal principal;

    @Mock
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup Principal before each test
        when(principal.getName()).thenReturn("testuser");
    }


    @Test
    void getUserCards_ReturnsOkWithCards() {
        Page<CardDto> cardPage = new PageImpl<>(Collections.singletonList(new CardDto()));
        when(cardService.getUserCards("testuser", pageable)).thenReturn(cardPage);

        ResponseEntity<Page<CardDto>> response = cardController.getUserCards(principal, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cardPage, response.getBody());
        verify(cardService).getUserCards("testuser", pageable);
    }

    @Test
    void transferFunds_ReturnsOkOnSuccess() {
        TransferDto transferDto = new TransferDto();
        ResponseEntity<Void> response = cardController.transferFunds(transferDto, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cardService).transferFunds("testuser", transferDto);
    }

    @Test
    void getCardBalance_ReturnsOkWithBalance() {
        when(cardService.getCardBalance("testuser", 1L)).thenReturn(100.0);

        ResponseEntity<Double> response = cardController.getCardBalance(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100.0, response.getBody());
        verify(cardService).getCardBalance("testuser", 1L);
    }

    @Test
    void requestBlockCard_ReturnsOkOnSuccess() {
        ResponseEntity<Void> response = cardController.requestBlockCard(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cardService).requestBlockCard("testuser", 1L);
    }

    @Test
    void getCardBalance_CardNotFound_ThrowsNotFound() {
        when(cardService.getCardBalance("testuser", 1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            cardController.getCardBalance(1L, principal);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(cardService).getCardBalance("testuser", 1L);
    }

    @Test
    void transferFunds_InsufficientFunds_ThrowsBadRequest() {
        TransferDto transferDto = new TransferDto();
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds"))
                .when(cardService).transferFunds("testuser", transferDto);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            cardController.transferFunds(transferDto, principal);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(cardService).transferFunds("testuser", transferDto);
    }

    @Test
    void requestBlockCard_CardAlreadyBlocked_ThrowsConflict() {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Card already blocked"))
                .when(cardService).requestBlockCard("testuser", 1L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            cardController.requestBlockCard(1L, principal);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(cardService).requestBlockCard("testuser", 1L);
    }

    @Test
    void getUserCards_NoCardsFound_ReturnsOkWithEmptyPage() {
        Page<CardDto> emptyPage = new PageImpl<>(Collections.emptyList());
        when(cardService.getUserCards("testuser", pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<CardDto>> response = cardController.getUserCards(principal, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyPage, response.getBody());
        verify(cardService).getUserCards("testuser", pageable);
    }
}
