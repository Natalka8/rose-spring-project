package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatistics {
    private Long totalTasks;
    private Long pendingTasks;
    private Long inProgressTasks;
    private Long completedTasks;
    private Long overdueTasks;
    private Double completionRate;

    public Double getCompletionRate() {
        if (totalTasks == null || totalTasks == 0) {
            return 0.0;
        }
        return (completedTasks != null ? completedTasks.doubleValue() : 0.0) / totalTasks * 100;
    }
}