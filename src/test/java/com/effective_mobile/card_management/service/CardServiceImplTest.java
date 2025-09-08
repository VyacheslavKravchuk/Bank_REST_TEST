package com.effective_mobile.card_management.service;

import com.effective_mobile.card_management.dto.CardCreateDto;
import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.dto.CardUpdateDto;
import com.effective_mobile.card_management.dto.TransferDto;
import com.effective_mobile.card_management.entity.Card;
import com.effective_mobile.card_management.entity.User;
import com.effective_mobile.card_management.enums.CardStatus;
import com.effective_mobile.card_management.exception.CardNotFoundException;
import com.effective_mobile.card_management.exception.InsufficientFundsException;
import com.effective_mobile.card_management.mapper.CardMapper;
import com.effective_mobile.card_management.repository.CardRepository;
import com.effective_mobile.card_management.repository.UserRepository;
import com.effective_mobile.card_management.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private CardDto cardDto;
    private CardCreateDto cardCreateDto;
    private CardUpdateDto cardUpdateDto;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        card = new Card();
        card.setId(1L);
        card.setCardNumber("1234567890123456");
        card.setOwner("Test Owner");
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(100.0);
        card.setUser(user);

        cardDto = new CardDto();
        cardDto.setId(1L);
        cardDto.setCardNumberMasked("1234567890123456");
        cardDto.setOwner("Test Owner");
        cardDto.setStatus(CardStatus.ACTIVE);
        cardDto.setBalance(100.0);

        cardCreateDto = new CardCreateDto();
        cardCreateDto.setUserId(1L);
        cardCreateDto.setOwner("Test Owner");
        cardCreateDto.setBalance(100.0);

        cardUpdateDto = new CardUpdateDto();
        cardUpdateDto.setStatus(CardStatus.BLOCKED);
    }

    @Test
    void createCard_Successful() {

        when(userRepository.findById(cardCreateDto.getUserId())).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        CardDto result = cardService.createCard(cardCreateDto);

        assertNotNull(result);
        assertEquals(cardDto.getCardNumberMasked(), result.getCardNumberMasked());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void createCard_UserNotFound() {

        when(userRepository.findById(cardCreateDto.getUserId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(cardCreateDto));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void updateCard_Successful() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        CardDto result = cardService.updateCard(card.getId(), cardUpdateDto);

        assertNotNull(result);
        assertEquals(cardDto.getId(), result.getId());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void updateCard_CardNotFound() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.updateCard(1L, cardUpdateDto));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void deleteCard_Successful() {

        doNothing().when(cardRepository).deleteById(card.getId());

        cardService.deleteCard(card.getId());

        verify(cardRepository, times(1)).deleteById(card.getId());
    }

    @Test
    void getCardById_Successful() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        CardDto result = cardService.getCardById(card.getId());

        assertNotNull(result);
        assertEquals(cardDto.getId(), result.getId());
    }

    @Test
    void getCardById_CardNotFound() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1L));
    }

    @Test
    void getAllCards_Successful() {

        List<Card> cards = Collections.singletonList(card);
        List<CardDto> cardDtos = Collections.singletonList(cardDto);

        when(cardRepository.findAll()).thenReturn(cards);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        List<CardDto> result = cardService.getAllCards();

        assertNotNull(result);
        assertEquals(cardDtos.size(), result.size());
        assertEquals(cardDtos.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getUserCards_Successful() {

        Pageable pageable = Pageable.unpaged();
        List<Card> cards = Collections.singletonList(card);
        Page<Card> cardPage = new PageImpl<>(cards, pageable, cards.size());
        List<CardDto> cardDtos = Collections.singletonList(cardDto);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cardRepository.findByUser(user, pageable)).thenReturn(cardPage);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        Page<CardDto> result = cardService.getUserCards(user.getUsername(), pageable);

        assertNotNull(result);
        assertEquals(cardDtos.size(), result.getContent().size());
        assertEquals(cardDtos.get(0).getId(), result.getContent().get(0).getId());
    }

    @Test
    void getUserCards_UserNotFound() {

        Pageable pageable = Pageable.unpaged();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.getUserCards(user.getUsername(), pageable));
    }

    @Test
    void transferFunds_Successful() {

        TransferDto transferDto = new TransferDto();
        transferDto.setFromCardId(1L);
        transferDto.setToCardId(2L);
        transferDto.setAmount(50.0);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(50.0);
        toCard.setUser(user);

        when(cardRepository.findById(transferDto.getFromCardId())).thenReturn(Optional.of(card));
        when(cardRepository.findById(transferDto.getToCardId())).thenReturn(Optional.of(toCard));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.transferFunds(user.getUsername(), transferDto);

        assertEquals(50.0, card.getBalance());
        assertEquals(100.0, toCard.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferFunds_CardNotFound() {

        TransferDto transferDto = new TransferDto();
        transferDto.setFromCardId(1L);
        transferDto.setToCardId(2L);
        transferDto.setAmount(50.0);

        when(cardRepository.findById(transferDto.getFromCardId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.transferFunds(user.getUsername(), transferDto));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferFunds_UserNotFound() {

        TransferDto transferDto = new TransferDto();
        transferDto.setFromCardId(1L);
        transferDto.setToCardId(2L);
        transferDto.setAmount(50.0);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(50.0);
        toCard.setUser(user);

        when(cardRepository.findById(transferDto.getFromCardId())).thenReturn(Optional.of(card));
        when(cardRepository.findById(transferDto.getToCardId())).thenReturn(Optional.of(toCard));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.transferFunds(user.getUsername(), transferDto));
        verify(cardRepository, never()).save(any(Card.class));
    }


    @Test
    void transferFunds_InsufficientFunds() {

        TransferDto transferDto = new TransferDto();
        transferDto.setFromCardId(1L);
        transferDto.setToCardId(2L);
        transferDto.setAmount(150.0);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(50.0);
        toCard.setUser(user);

        when(cardRepository.findById(transferDto.getFromCardId())).thenReturn(Optional.of(card));
        when(cardRepository.findById(transferDto.getToCardId())).thenReturn(Optional.of(toCard));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(InsufficientFundsException.class, () -> cardService.transferFunds(user.getUsername(), transferDto));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void getCardBalance_Successful() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Double balance = cardService.getCardBalance(user.getUsername(), card.getId());

        assertEquals(card.getBalance(), balance);
    }

    @Test
    void getCardBalance_CardNotFound() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardBalance(user.getUsername(), card.getId()));
    }

    @Test
    void getCardBalance_UserNotFound() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.getCardBalance(user.getUsername(), card.getId()));
    }

    @Test
    void blockCard_Successful() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.blockCard(card.getId());

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void blockCard_CardNotFound() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.blockCard(card.getId()));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void activateCard_Successful() {

        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        cardService.activateCard(card.getId());

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void activateCard_CardNotFound() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.activateCard(card.getId()));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void requestBlockCard_Successful() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.requestBlockCard(user.getUsername(), card.getId());

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void requestBlockCard_CardNotFound() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.requestBlockCard(user.getUsername(), card.getId()));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void requestBlockCard_UserNotFound() {

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.requestBlockCard(user.getUsername(), card.getId()));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void requestBlockCard_UserNotOwner() {

        User anotherUser = new User();
        anotherUser.setId(2L);
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(userRepository.findByUsername(anotherUser.getUsername())).thenReturn(Optional.of(anotherUser));

        assertThrows(IllegalArgumentException.class, () -> cardService.requestBlockCard(anotherUser.getUsername(), card.getId()));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void generateRandomCardNumber_ValidLength() {
        String cardNumber = CardServiceImpl.generateRandomCardNumber();
        assertEquals(16, cardNumber.length());
    }

    @Test
    void generateRandomCardNumber_NumericCharacters() {
        String cardNumber = CardServiceImpl.generateRandomCardNumber();
        assertTrue(cardNumber.matches("\\d+"));
    }
}
