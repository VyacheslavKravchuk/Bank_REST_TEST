package com.effective_mobile.card_management.controller;
import com.effective_mobile.card_management.dto.CardDto;
import com.effective_mobile.card_management.dto.TransferDto;
import com.effective_mobile.card_management.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Tag(name = "Контроллер для карт пользователя", description = "Контроллер с защищенными эндпоинтами")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/cards")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Получение всех карт пользователя",
            description = "Возвращает список карт, принадлежащих аутентифицированному пользователю," +
                    " с поддержкой пагинации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class,
                                            subTypes = {CardDto.class}))),
                    @ApiResponse(responseCode = "400", description = "Неверный запрос",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardDto>> getUserCards(
            Principal principal,
            @Parameter(in = ParameterIn.QUERY,
                    description = "Параметры пагинации.  Например: `page=0&size=10&sort=cardNumberMasked,asc`",
                    name = "pageable",
                    schema = @Schema(type = "string")) Pageable pageable) {

        String username = principal.getName();
        logger.info("Запрос на получение карт пользователя {}. Параметры пагинации: {}",
                username, pageable);
        Page<CardDto> cards = cardService.getUserCards(username, pageable);
        logger.info("Получено {} карт для пользователя {}", cards.getTotalElements(), username);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Перевод средств между картами пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ"),
                    @ApiResponse(responseCode = "400", description = "Неверный запрос",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "Stringapplication/json",
                                    schema = @Schema(implementation = String.class)))
            })
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> transferFunds(@RequestBody TransferDto transferDto,
                                              Principal principal) {
        String username = principal.getName();
        logger.info("Запрос на перевод средств от пользователя {}. Данные перевода: {}",
                username, transferDto);
        cardService.transferFunds(username, transferDto);
        logger.info("Успешно выполнен перевод средств от пользователя {}", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить баланс карты пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Double.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный запрос",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Double> getCardBalance(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        logger.info("Запрос на получение баланса карты с ID {} от пользователя {}", id, username);
        Double balance = cardService.getCardBalance(username, id);
        logger.info("Баланс карты с ID {} пользователя {} равен {}", id, username, balance);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Запрос блокировки карты пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ"),
                    @ApiResponse(responseCode = "400", description = "Неверный запрос",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    @PostMapping("/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> requestBlockCard(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        logger.info("Запрос на блокировку карты с ID {} от пользователя {}", id, username);
        cardService.requestBlockCard(username, id);
        logger.info("Запрос на блокировку карты с ID {} от пользователя {} успешно обработан", id, username);
        return ResponseEntity.ok().build();
    }
}



