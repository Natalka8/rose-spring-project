package org.example.controller;

import org.example.dto.ApiResponse;
import org.example.dto.TaskRequest;
import org.example.dto.TaskResponse;
import org.example.entity.Task;
import org.example.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request) {

        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable Long taskId) {

        TaskResponse response = taskService.getTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user tasks")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getUserTasks(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<TaskResponse> tasks = taskService.getUserTasks(pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Filter tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> filterTasks(
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Start date in ISO format") LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "End date in ISO format") LocalDateTime endDate) {

        List<TaskResponse> tasks = taskService.filterTasks(status, priority, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/overdue")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get overdue tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update task status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam Task.Status status) {

        TaskResponse response = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get task statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTaskStatistics() {
        Map<String, Object> stats = taskService.getTaskStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}