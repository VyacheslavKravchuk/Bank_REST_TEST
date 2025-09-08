
## Описание компонентов

*   **`CardManagementSystemApplication.java`**: Главный класс приложения Spring Boot.
*   **`config/`**: Содержит конфигурации.
    *   `SecurityConfig.java`: Конфигурация безопасности Spring Security.
    *   `OpenApiConfig.java`: Конфигурация для документирования.
    *   `JwtRequestFilter.java`: Конфигурация фильтра для перехвата входящих HTTP-запросов и проверки JWT.
    *   `JwtAuthenticationEntryPoint.java`: Конфигурация обработки неаутентифицированных запросов в Spring Security.
*   **`controller/`**:  Содержит контроллеры REST API для управления картами, пользователями и администрированием.
    *   `AdminController.java`: Контроллер для администрирования.
    *   `CardController.java`: Контроллер для управления картами.
    *   `UserController.java`: Контроллер для управления пользователями.
*   **`dto/`**: Data Transfer Objects для передачи данных между слоями.
    *   `CardDto.java`: DTO для карт.
    *   `CardCreateDto.java`: DTO для создания карт.
    *   `CardUpdateDto.java`: DTO для обновления карт.
    *   `TransferDto.java`: DTO для перевода средств.
    *   `UserDto.java`: DTO для пользователей.
*   **`entity/`**:  Сущности, представляющие таблицы базы данных.
    *   `Card.java`:  Сущность карты.
    *   `User.java`: Сущность пользователя.
*   **`enums/`**: Enum-классы.
    *   `CardStatus.java`: Статус карты (активна, заблокирована, просрочена.).
    *   `RoleName.java`:  Название роли пользователя (ADMIN, USER).
*   **`exception/`**:  Пользовательские исключения.
    *   `CardNotFoundException.java`: Исключение, если карта не найдена.
    *   `InsufficientFundsException.java`: Исключение, если недостаточно средств.
*   **`mapper/`**:  Мапперы для преобразования сущностей в DTO и обратно.
    *   `CardMapper.java`: Маппер для карт.
*   **`repository/`**:  Репозитории для работы с базой данных через Spring Data JPA.
    *   `CardRepository.java`: Репозиторий для карт.
    *   `UserRepository.java`: Репозиторий для пользователей.
*   **`service/`**:  Интерфейсы сервисов, содержащие бизнес-логику.
    *   `CardService.java`: Сервис для работы с картами.
    *   `UserDetailsService.java`:  Сервис для работы с деталями пользователей (Spring Security).
    *   `UserService.java`: Сервис для работы с пользователями.
*   **`service.impl/`**:  Реализации интерфейсов сервисов.
    *   `CardServiceImpl.java`: Реализация сервиса для работы с картами.
    *   `UserDetailsServiceImpl.java`: Реализация сервиса для работы с деталями пользователей (Spring Security).
    *   `UserServiceImpl.java`: Реализация сервиса для работы с пользователями.
*   **`util/`**:  Вспомогательные классы.
    *   `CardNumberMasker.java`: Утилита для маскировки номеров карт.
    *   `JwtUtil.java`: Утилита для работы с JWT (JSON Web Tokens).
*   **`src/main/resources/application.properties`**: Файл конфигурации приложения.
*   **`src/main/resources/liquibase/scripts`**:  SQL-скрипты для миграции базы данных (с использованием Liquibase).
    *   `users.sql`: Запрос на создание таблицы users.
    *   `cards.sql`:  Запрос на создание таблицы cards.
*   **`src/main/resources/liquibase/changelog-master.yml`**: Файл адресов миграций liquibase.
*   **`test/java/com/effective_mobile/card_management/controller`**: Тесты контроллеров приложения.
    *   `AdminControllerTest.java`:  Тест контроллера администратора.
    *   `CardControllerTest.java`:  Тест контроллера карт.
    *   `UserControllerTest.java`:  Тест контроллера для пользователя.
*   **`test/java/com/effective_mobile/card_management/service`**: Тесты сервисов приложения.
    *   `CardServiceTest.java`:  Тест для сервиса карт.
    *   `UserDetailsServiceImplTest.java`:  Тест для сервиса деталей о пользователе.
    *   `UserServiceImplTest.java`:  Тест сервиса для пользователей.
*   **`pom.xml`**:  Файл конфигурации Maven.
*   **`Dockerfile`**:  Dockerfile для создания Docker-образа приложения.
*   **`docker-compose.yml`**: Файл для оркестровки Docker-контейнеров (например, для запуска приложения и базы данных).
*   **`README.md`**: Файл с описанием проекта.
*   **`docs/openapi.yaml`**: Файл OpenAPI (Swagger) для документации API.


## Описание базы данных: Cards и Users

Эта база данных содержит информацию о пользователях (Users) и картах (Cards), связанных между собой отношением "один ко многим". Каждый пользователь может иметь несколько карт, а каждая карта принадлежит одному пользователю.  Описания соответствуют Java Entity классам.

### 1. Таблица: Users

Содержит информацию о пользователях. Соответствует классу `User`.

*   **Название:** `users` (как указано в `@Table(name = "users")`)
*   **Описание:** Хранит данные о зарегистрированных пользователях.

| Поле        | Тип данных        | Описание                                                                     | Ключ      | Аннотация Java                                        |
| ----------- | ------------------ | ---------------------------------------------------------------------------- | --------- | ------------------------------------------------------- |
| `id`        | BIGINT (Long)      | Уникальный идентификатор пользователя.                                          | PRIMARY KEY | `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `username`  | VARCHAR (String)   | Имя пользователя.                                                             | UNIQUE, NOT NULL | `@Column(unique = true, nullable = false)`                 |
| `password`  | VARCHAR (String)   | Хеш пароля пользователя.                                                       | NOT NULL | `@Column(nullable = false)`                           |
| `email`     | VARCHAR (String)   | Электронная почта пользователя.                                                  | UNIQUE, NOT NULL | `@Column(unique = true, nullable = false)`                 |
| `firstName` | VARCHAR (String)   | Имя пользователя.                                                              | NOT NULL | `@Column(nullable = false)`                           |
| `lastName`  | VARCHAR (String)   | Фамилия пользователя.                                                            | NOT NULL | `@Column(nullable = false)`                           |
| `role`      | VARCHAR (String)   | Роль пользователя (например, ADMIN, USER).  Определена как Enum `Role`.       |           | `@Enumerated(EnumType.STRING)`                        |

### 2. Таблица: Cards

Содержит информацию о картах, принадлежащих пользователям.  Соответствует классу `Card`.

*   **Название:** `Cards` (неявно, поскольку `@Table` не указан, имя будет сгенерировано на основе имени класса)
*   **Описание:** Хранит информацию о картах (например, кредитных или дебетовых), принадлежащих пользователям.

| Поле         | Тип данных          | Описание                                                                     | Ключ       | Аннотация Java                                          |
| ------------ | -------------------- | ---------------------------------------------------------------------------- | ---------- | -------------------------------------------------------- |
| `id`         | BIGINT (Long)        | Уникальный идентификатор карты.                                              | PRIMARY KEY | `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `cardNumber` | VARCHAR (String)     | Номер карты.                                                                 | UNIQUE, NOT NULL | `@Column(unique = true, nullable = false)`                 |
| `owner`      | VARCHAR (String)     | Имя владельца карты (возможно, задублировано с User).                         | NOT NULL | `@Column(nullable = false)`                           |
| `expiryDate` | DATE (LocalDate)     | Срок действия карты.                                                         | NOT NULL | `@Column(nullable = false)`                           |
| `status`     | VARCHAR (String)     | Статус карты (например, ACTIVE, BLOCKED). Определен как Enum `CardStatus`. | NOT NULL | `@Enumerated(EnumType.STRING) @Column(nullable = false)` |
| `balance`    | DOUBLE (Double)      | Баланс карты.                                                                 | NOT NULL | `@Column(nullable = false)`                           |
| `user_id`    | BIGINT (Long)        | Идентификатор пользователя, владеющего картой.                                 | FOREIGN KEY (users.id), NOT NULL | `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)` |

### Связи

*   **Один ко многим:**  Один пользователь (из таблицы `users`) может иметь несколько карт (в таблице `Cards`). Связь устанавливается через поле `user_id` в таблице `Cards`, которое является внешним ключом (FOREIGN KEY), ссылающимся на поле `id` в таблице `users`.  Это означает, что каждая карта связана с конкретным пользователем.  Связь реализована с помощью `@ManyToOne` в классе `Card`, указывающей на класс `User`.
