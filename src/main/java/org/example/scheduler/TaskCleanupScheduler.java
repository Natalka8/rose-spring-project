package org.example.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Task;
import org.example.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCleanupScheduler {

    private final TaskRepository taskRepository;

    // Every day at 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldCompletedTasks() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(3);

        List<Task> oldTasks = taskRepository
                .findByStatusAndCompletedAtBefore(Task.Status.COMPLETED, cutoffDate);

        if (!oldTasks.isEmpty()) {
            log.info("Удаление {} старых выполненных задач", oldTasks.size());
            taskRepository.deleteAll(oldTasks);
        }
    }

    // Every hour
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void checkOverdueTasks() {
        List<Task> overdueTasks = taskRepository
                .findByStatusAndDueDateBefore(Task.Status.PENDING, LocalDateTime.now());

        overdueTasks.forEach(task -> {
            // You don't have OVERDUE status in Task.Status, using BLOCKED
            task.setStatus(Task.Status.BLOCKED);
            log.warn("Задача {} '{}' просрочена", task.getId(), task.getTitle());
        });

        if (!overdueTasks.isEmpty()) {
            taskRepository.saveAll(overdueTasks);
        }
    }
}