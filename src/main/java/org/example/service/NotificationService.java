// src/main/java/org/example/service/NotificationService.java
package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Async
    public void sendTaskReminder(Long taskId, String username) {
        // Simulating email/notification sending
        log.info("Отправка напоминания для задачи {} пользователю {}",
                taskId, username);
        try {
            Thread.sleep(2000); // Simulating long operation
            log.info("Напоминание отправлено для задачи {}", taskId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}