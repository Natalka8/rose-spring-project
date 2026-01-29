package org.example.service;

import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse getTask(Long taskId);

    Page<TaskResponse> getUserTasks(Pageable pageable);

    List<TaskResponse> filterTasks(Task.Status status,
                                   Task.Priority priority,
                                   LocalDateTime startDate,
                                   LocalDateTime endDate);

    List<TaskResponse> getOverdueTasks();

    TaskResponse updateTask(Long taskId, TaskRequest request);

    TaskResponse updateTaskStatus(Long taskId, Task.Status status);

    void deleteTask(Long taskId);

    Map<String, Object> getTaskStatistics();
}