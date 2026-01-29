package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.entity.Task;

import java.time.LocalDateTime;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    private String description;

    private Task.Status status = Task.Status.PENDING;

    private Task.Priority priority = Task.Priority.MEDIUM;

    private LocalDateTime dueDate;

    private Integer estimatedHours;

    private Integer actualHours;
}