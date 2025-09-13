package com.effective_mobile.card_management.controller;


import com.effective_mobile.card_management.dto.CardCreateDto;
import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.dto.CardUpdateDto;
import com.effective_mobile.card_management.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Контроллер администратора", description = "Контроллер с защищенными эндпоинтами")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final CardService cardService;

    public AdminController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Регистрация карты пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Карта успешно создана", content = @Content(schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "400", description =
                    "Неверный запрос (например, неверный формат данных)",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description =
                    "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description =
                    "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CardCreateDto cardCreateDto) {
        logger.info("Запрос на создание карты: {}", cardCreateDto);
        CardDto createdCard = cardService.createCard(cardCreateDto);
        logger.info("Карта успешно создана: {}", createdCard);
        return ResponseEntity.ok(createdCard);
    }

    @Operation(summary = "Обновление карты пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Карта успешно обновлена", content = @Content(schema = @Schema(implementation = CardDto.class))),
            @ApiResponse(responseCode = "400", description =
                    "Неверный запрос (например, неверный формат данных)",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description =
                    "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description =
                    "Карта не найдена", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description =
                    "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id, @RequestBody @Valid CardUpdateDto cardUpdateDto) {
        logger.info("Запрос на обновление карты с ID: {}, Данные: {}", id, cardUpdateDto);
        CardDto updatedCard = cardService.updateCard(id, cardUpdateDto);
        logger.info("Карта с ID: {} успешно обновлена: {}", id, updatedCard);
        return ResponseEntity.ok(updatedCard);
    }

    @Operation(summary = "Удаление карты пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        logger.info("Запрос на удаление карты с ID: {}", id);
        cardService.deleteCard(id);
        logger.info("Карта с ID: {} успешно удалена", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение всех карт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт получен",
                    content = @Content(schema = @Schema(implementation = CardDto.class, type = "array"))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping
    public ResponseEntity<List<CardDto>> getAllCards() {
        logger.info("Запрос на получение всех карт");
        List<CardDto> cards = cardService.getAllCards();
        logger.info("Получено {} карт", cards.size());
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Блокировка карты пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content =
            @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long id) {
        logger.info("Запрос на блокировку карты с ID: {}", id);
        cardService.blockCard(id);
        logger.info("Карта с ID: {} успешно заблокирована", id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Активация карты пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно активирована"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content =
            @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long id) {
        logger.info("Запрос на активацию карты с ID: {}", id);
        cardService.activateCard(id);
        logger.info("Карта с ID: {} успешно активирована", id);
        return ResponseEntity.ok().build();
    }
}

