"# rose-spring-final" 
# Rose - Spring Boot Application

Веб-приложение на Spring Boot для управления игровыми сессиями и статистикой игроков.

## 🚀 Основной функционал
- ✅ **Spring Security** - аутентификация и авторизация
- ✅ **Spring Data JPA** - работа с базой данных
- ✅ **Spring MVC** - REST API и представления
- ✅ **Spring Boot** - авто-конфигурация и запуск
- ✅ **Thymeleaf** или **JSP** - шаблонизация

## 🏗️ Архитектура проекта
rose-spring-final/
├── src/main/java/org/example/
│ ├── controller/ # Контроллеры (REST/Spring MVC)
│ ├── service/ # Сервисный слой (бизнес-логика)
│ ├── repository/ # Репозитории (Spring Data JPA)
│ ├── entity/ # Сущности БД (@Entity)
│ ├── config/ # Конфигурационные классы
│ └── Application.java # Главный класс Spring Boot
├── src/main/resources/
│ ├── static/ # CSS, JS, 
│ ├── templates/ # HTML/Thymeleaf шаблоны
│ ├── application.properties # Настройки Spring
│ └── schema.sql # Инициализация БД
└── pom.xml # Maven зависимости
