package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Task;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private Integer estimatedHours;
    private Integer actualHours;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isOverdue() {
        return dueDate != null &&
                dueDate.isBefore(LocalDateTime.now()) &&
                status != Task.Status.COMPLETED;
    }
}