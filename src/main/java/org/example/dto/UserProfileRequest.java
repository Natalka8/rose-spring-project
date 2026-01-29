package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление профиля пользователя")
public class UserProfileRequest {

    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Size(max = 500, message = "Биография не должна превышать 500 символов")
    @Schema(description = "О себе", example = "Разработчик игр и энтузиаст технологий")
    private String bio;

    @Size(max = 100, message = "Местоположение не должно превышать 100 символов")
    @Schema(description = "Местоположение", example = "Москва, Россия")
    private String location;

    @Size(max = 200, message = "Ссылка на аватар не должна превышать 200 символов")
    @Schema(description = "URL аватара", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Size(max = 100, message = "Заголовок профиля не должен превышать 100 символов")
    @Schema(description = "Заголовок профиля", example = "Senior Game Developer")
    private String profileTitle;
}