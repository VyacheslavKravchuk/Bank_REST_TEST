package com.effective_mobile.card_management.service.impl;
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
import com.effective_mobile.card_management.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository,
                           CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
    }

    private Card findCardOrThrow(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException(cardId);
                });
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь с именем {} не найден", username);
                    return new IllegalArgumentException("User not found with username: " + username);
                });
    }

    private void verifyCardOwnership(User user, Card card, Long cardId) {
        if (!card.getUser().equals(user)) {
            logger.error("Пользователь {} не владеет картой с ID {}", user.getUsername(), cardId);
            throw new IllegalArgumentException("User does not own this card.");
        }
    }

    @Override
    public CardDto createCard(CardCreateDto cardCreateDto) {
        logger.info("Создание новой карты для пользователя с ID: {}", cardCreateDto.getUserId());
        User user = userRepository.findById(cardCreateDto.getUserId())
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", cardCreateDto.getUserId());
                    return new IllegalArgumentException("User not found with id: " + cardCreateDto.getUserId());
                });

        Card card = new Card();
        card.setCardNumber(generateRandomCardNumber());
        card.setOwner(cardCreateDto.getOwner());
        card.setExpiryDate(cardCreateDto.getExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(cardCreateDto.getBalance());
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        logger.info("Карта с ID {} успешно создана", savedCard.getId());
        return cardMapper.toDto(savedCard);
    }

    @Override
    public CardDto updateCard(Long id, CardUpdateDto cardUpdateDto) {
        logger.info("Обновление карты с ID: {}", id);
        Card card = findCardOrThrow(id);
        card.setStatus(cardUpdateDto.getStatus());
        Card updatedCard = cardRepository.save(card);
        logger.info("Карта с ID {} успешно обновлена", updatedCard.getId());
        return cardMapper.toDto(updatedCard);
    }

    @Override
    public void deleteCard(Long id) {
        logger.info("Удаление карты с ID: {}", id);
        cardRepository.deleteById(id);
        logger.info("Карта с ID {} успешно удалена", id);
    }

    @Override
    public CardDto getCardById(Long id) {
        logger.info("Получение карты с ID: {}", id);
        Card card = findCardOrThrow(id);
        CardDto cardDto = cardMapper.toDto(card);
        logger.info("Карта с ID {} успешно получена", id);
        return cardDto;
    }

    @Override
    public List<CardDto> getAllCards() {
        logger.info("Получение всех карт");
        List<CardDto> cards = cardRepository.findAll().stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Получено {} карт", cards.size());
        return cards;
    }

    @Override
    public Page<CardDto> getUserCards(String username, Pageable pageable) {
        logger.info("Получение карт пользователя {}. Параметры пагинации: {}", username, pageable);
        User user = findUserOrThrow(username);
        Page<CardDto> cards = cardRepository.findByUser(user, pageable)
                .map(cardMapper::toDto);
        logger.info("Получено {} карт для пользователя {}", cards.getTotalElements(), username);
        return cards;
    }

    @Override
    @Transactional
    public void transferFunds(String username, TransferDto transferDto) {
        logger.info("Перевод средств от пользователя {}. Данные перевода: {}", username, transferDto);
        Card fromCard = findCardOrThrow(transferDto.getFromCardId());
        Card toCard = findCardOrThrow(transferDto.getToCardId());
        User user = findUserOrThrow(username);

        if (!fromCard.getUser().equals(user) || !toCard.getUser().equals(user)) {
            logger.error("Пользователь {} не владеет одной или обеими картами (FromCardId: {}, ToCardId: {})", username, fromCard.getId(), toCard.getId());
            throw new IllegalArgumentException("User does not own one or both of the cards.");
        }

        if (fromCard.getBalance() < transferDto.getAmount()) {
            logger.warn("Недостаточно средств на карте с ID: {}. Баланс: {}, Сумма перевода: {}",
                    fromCard.getId(), fromCard.getBalance(), transferDto.getAmount());
            throw new InsufficientFundsException("Insufficient funds on card: " + fromCard.getId());
        }

        fromCard.setBalance(fromCard.getBalance() - transferDto.getAmount());
        toCard.setBalance(toCard.getBalance() + transferDto.getAmount());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        logger.info("Успешно выполнен перевод средств от пользователя {} с карты {} на карту {}", username, fromCard.getId(), toCard.getId());
    }

    @Override
    public Double getCardBalance(String username, Long cardId) {
        logger.info("Получение баланса карты с ID {} для пользователя {}", cardId, username);
        Card card = findCardOrThrow(cardId);
        User user = findUserOrThrow(username);
        verifyCardOwnership(user, card, cardId);

        double balance = card.getBalance();
        logger.info("Баланс карты с ID {} пользователя {} равен {}", cardId, username, balance);
        return balance;
    }

    @Override
    public void blockCard(Long cardId) {
        logger.info("Блокировка карты с ID: {}", cardId);
        Card card = findCardOrThrow(cardId);

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        logger.info("Карта с ID {} успешно заблокирована", cardId);
    }

    @Override
    public void activateCard(Long cardId) {
        logger.info("Активация карты с ID: {}", cardId);
        Card card = findCardOrThrow(cardId);

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        logger.info("Карта с ID {} успешно активирована", cardId);
    }

    @Override
    public void requestBlockCard(String username, Long cardId) {
        logger.info("Запрос на блокировку карты с ID {} от пользователя {}", cardId, username);
        Card card = findCardOrThrow(cardId);
        User user = findUserOrThrow(username);
        verifyCardOwnership(user, card, cardId);

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        logger.info("Запрос на блокировку карты с ID {} от пользователя {} успешно обработан", cardId, username);
    }


    public static String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        String cardNumber = sb.toString();
        logger.debug("Сгенерирован случайный номер карты: {}", cardNumber);
        return cardNumber;
    }
}
