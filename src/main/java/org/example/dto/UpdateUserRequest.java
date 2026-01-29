package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление пользователя")
public class UpdateUserRequest {

    @Size(min = 1, max = 50, message = "Имя должно быть от 1 до 50 символов")
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Size(min = 1, max = 50, message = "Фамилия должна быть от 1 до 50 символов")
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    @Schema(description = "Email пользователя", example = "ivan@example.com")
    private String email;

    @Size(max = 500, message = "Биография не должна превышать 500 символов")
    @Schema(description = "Биография пользователя", example = "Люблю программирование и игры")
    private String bio;
}