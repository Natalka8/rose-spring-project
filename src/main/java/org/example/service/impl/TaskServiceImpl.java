package org.example.service.impl;

import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.dto.UserResponse;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.TaskService;
import org.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        UserResponse currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Get full User object from database
        User user = getUserById(currentUser.getId());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(Task.Status.PENDING)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created with id: {}", savedTask.getId());

        return convertToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        UserResponse currentUser = userService.getCurrentUser();
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getUserTasks(Pageable pageable) {
        UserResponse currentUser = userService.getCurrentUser();
        Page<Task> tasksPage = taskRepository.findByUserId(currentUser.getId(), pageable);

        return tasksPage.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> filterTasks(Task.Status status,
                                          Task.Priority priority,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate) {
        UserResponse currentUser = userService.getCurrentUser();
        List<Task> tasks = taskRepository.findFilteredTasks(
                currentUser.getId(),
                status,
                priority,
                startDate,
                endDate
        );

        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        UserResponse currentUser = userService.getCurrentUser();
        List<Task> tasks = taskRepository.findOverdueTasks(currentUser.getId(), LocalDateTime.now());

        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        UserResponse currentUser = userService.getCurrentUser();
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated with id: {}", taskId);

        return convertToResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Task.Status status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        UserResponse currentUser = userService.getCurrentUser();
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());

        if (status == Task.Status.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task savedTask = taskRepository.save(task);
        log.info("Task status updated to {} for task id: {}", status, taskId);

        return convertToResponse(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        UserResponse currentUser = userService.getCurrentUser();
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        taskRepository.delete(task);
        log.info("Task deleted with id: {}", taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStatistics() {
        UserResponse currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        Map<String, Object> statistics = new HashMap<>();

        // Total number of tasks
        long totalTasks = taskRepository.findByUserId(userId).size();
        statistics.put("totalTasks", totalTasks);

        // Number of tasks by status
        List<Object[]> statusCounts = taskRepository.countTasksByStatus(userId);
        Map<String, Long> tasksByStatus = new HashMap<>();

        for (Object[] row : statusCounts) {
            Task.Status status = (Task.Status) row[0];
            Long count = (Long) row[1];
            tasksByStatus.put(status.toString(), count);
        }

        // Fill missing statuses with zeros
        for (Task.Status status : Task.Status.values()) {
            tasksByStatus.putIfAbsent(status.toString(), 0L);
        }

        statistics.put("tasksByStatus", tasksByStatus);

        // Number of overdue tasks
        long overdueTasks = taskRepository.findOverdueTasks(userId, LocalDateTime.now()).size();
        statistics.put("overdueTasks", overdueTasks);

        // Percentage of completed tasks
        long completedTasks = tasksByStatus.getOrDefault(Task.Status.COMPLETED.toString(), 0L);
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        // Number of tasks by priority
        Map<String, Long> tasksByPriority = new HashMap<>();
        for (Task.Priority priority : Task.Priority.values()) {
            long count = taskRepository.findByUserIdAndPriority(userId, priority).size();
            tasksByPriority.put(priority.toString(), count);
        }
        statistics.put("tasksByPriority", tasksByPriority);

        return statistics;
    }

    // Helper methods (not from interface, but can be useful)

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(Task.Status status) {
        UserResponse currentUser = userService.getCurrentUser();
        List<Task> tasks = taskRepository.findByUserIdAndStatus(currentUser.getId(), status);

        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByPriority(Task.Priority priority) {
        UserResponse currentUser = userService.getCurrentUser();
        List<Task> tasks = taskRepository.findByUserIdAndPriority(currentUser.getId(), priority);

        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse completeTask(Long taskId) {
        return updateTaskStatus(taskId, Task.Status.COMPLETED);
    }

    private TaskResponse convertToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .userId(task.getUser().getId())
                .build();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}