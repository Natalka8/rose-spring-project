package org.example.service;

import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.dto.UserResponse;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.entity.User.UserStatus;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private UserResponse testUserResponse;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();

        testUserResponse = UserResponse.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(Task.Status.PENDING)
                .priority(Task.Priority.MEDIUM)
                .dueDate(LocalDateTime.now().plusDays(1))
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createTask_ShouldReturnTaskResponse() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");
        request.setPriority(Task.Priority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(2));

        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse result = taskService.createTask(request);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(userService, times(1)).getCurrentUser();
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createTask_ShouldThrowException_WhenUserNotAuthenticated() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("New Description");

        when(userService.getCurrentUser()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(request));
        verify(userService, times(1)).getCurrentUser();
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTask_ShouldReturnTaskResponse() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.getCurrentUser()).thenReturn(testUserResponse);

        // Act
        TaskResponse result = taskService.getTask(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTask_ShouldThrowException_WhenTaskNotFound() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        // REMOVE this call as it's not used in this test:
        // when(userService.getCurrentUser()).thenReturn(testUserResponse);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTask(taskId));
        verify(taskRepository, times(1)).findById(taskId);
        // This verify is also not needed:
        // verify(userService, never()).getCurrentUser();
    }

    @Test
    void getTask_ShouldThrowException_WhenAccessDenied() {
        // Arrange
        UserResponse otherUser = UserResponse.builder()
                .id(2L)
                .username("otherUser")
                .email("other@example.com")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.getCurrentUser()).thenReturn(otherUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTask(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getUserTasks_ShouldReturnPageOfTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks);
        Pageable pageable = Pageable.ofSize(10);

        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(taskPage);

        // Act
        Page<TaskResponse> result = taskService.getUserTasks(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository, times(1)).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    void filterTasks_ShouldReturnFilteredTasks() {
        // Arrange
        Task.Status status = Task.Status.PENDING;
        Task.Priority priority = Task.Priority.MEDIUM;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        List<Task> tasks = Arrays.asList(testTask);

        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.findFilteredTasks(1L, status, priority, startDate, endDate)).thenReturn(tasks);

        // Act
        List<TaskResponse> result = taskService.filterTasks(status, priority, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findFilteredTasks(1L, status, priority, startDate, endDate);
    }

    @Test
    void getOverdueTasks_ShouldReturnOverdueTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);

        // FIXED: use eq() for specific value and any() for LocalDateTime
        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.findOverdueTasks(eq(1L), any(LocalDateTime.class))).thenReturn(tasks);

        // Act
        List<TaskResponse> result = taskService.getOverdueTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findOverdueTasks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void updateTask_ShouldUpdateTaskSuccessfully() {
        // Arrange
        Long taskId = 1L;
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated Description");
        request.setPriority(Task.Priority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(3));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse result = taskService.updateTask(taskId, request);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        // Arrange
        Long taskId = 1L;
        TaskRequest request = new TaskRequest();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        // REMOVE this call as it's not used:
        // when(userService.getCurrentUser()).thenReturn(testUserResponse);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.updateTask(taskId, request));
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatusSuccessfully() {
        // Arrange
        Long taskId = 1L;
        Task.Status newStatus = Task.Status.IN_PROGRESS;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setStatus(newStatus);
            return savedTask;
        });

        // Act
        TaskResponse result = taskService.updateTaskStatus(taskId, newStatus);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void completeTask_ShouldUpdateStatusToCompleted() {
        // Arrange
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setStatus(Task.Status.COMPLETED);
            savedTask.setCompletedAt(LocalDateTime.now());
            return savedTask;
        });

        // Act
        TaskResponse result = taskService.completeTask(taskId);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTaskSuccessfully() {
        // Arrange
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userService.getCurrentUser()).thenReturn(testUserResponse);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        // Arrange
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        // REMOVE this call as it's not used:
        // when(userService.getCurrentUser()).thenReturn(testUserResponse);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskId));
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void getTaskStatistics_ShouldReturnStatistics() {
        // Arrange
        List<Task> allTasks = Arrays.asList(testTask);
        List<Task> overdueTasks = Collections.emptyList();
        List<Task> mediumPriorityTasks = Arrays.asList(testTask);

        List<Object[]> statusCounts = Arrays.<Object[]>asList(
                new Object[]{Task.Status.PENDING, 1L}
        );

        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.findByUserId(1L)).thenReturn(allTasks);
        when(taskRepository.findOverdueTasks(eq(1L), any(LocalDateTime.class))).thenReturn(overdueTasks);
        when(taskRepository.findByUserIdAndPriority(1L, Task.Priority.MEDIUM)).thenReturn(mediumPriorityTasks);
        when(taskRepository.findByUserIdAndPriority(1L, Task.Priority.HIGH)).thenReturn(Collections.emptyList());
        when(taskRepository.findByUserIdAndPriority(1L, Task.Priority.LOW)).thenReturn(Collections.emptyList());
        when(taskRepository.countTasksByStatus(1L)).thenReturn(statusCounts);

        // Act
        Map<String, Object> statistics = taskService.getTaskStatistics();

        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.containsKey("totalTasks"));
        assertTrue(statistics.containsKey("overdueTasks"));
        assertTrue(statistics.containsKey("completionRate"));
        assertTrue(statistics.containsKey("tasksByStatus"));
        assertTrue(statistics.containsKey("tasksByPriority"));

        assertEquals(1L, statistics.get("totalTasks"));
        assertEquals(0L, statistics.get("overdueTasks"));

        verify(taskRepository, times(1)).findByUserId(1L);
        verify(taskRepository, times(1)).findOverdueTasks(eq(1L), any(LocalDateTime.class));
        verify(taskRepository, times(1)).countTasksByStatus(1L);
    }

    @Test
    void getTasksByStatus_ShouldReturnFilteredTasks() {
        // Arrange
        Task.Status status = Task.Status.PENDING;
        List<Task> tasks = Arrays.asList(testTask);

        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.findByUserIdAndStatus(1L, status)).thenReturn(tasks);

        // Act
        List<TaskResponse> result = taskService.getTasksByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Task.Status.PENDING, result.get(0).getStatus());
        verify(taskRepository, times(1)).findByUserIdAndStatus(1L, status);
    }

    @Test
    void getTasksByPriority_ShouldReturnFilteredTasks() {
        // Arrange
        Task.Priority priority = Task.Priority.MEDIUM;
        List<Task> tasks = Arrays.asList(testTask);

        when(userService.getCurrentUser()).thenReturn(testUserResponse);
        when(taskRepository.findByUserIdAndPriority(1L, priority)).thenReturn(tasks);

        // Act
        List<TaskResponse> result = taskService.getTasksByPriority(priority);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Task.Priority.MEDIUM, result.get(0).getPriority());
        verify(taskRepository, times(1)).findByUserIdAndPriority(1L, priority);
    }
}