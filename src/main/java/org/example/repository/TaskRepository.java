package org.example.repository;

import org.example.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Task.Status status);

    // ADD THIS METHOD!
    List<Task> findByUserIdAndPriority(Long userId, Task.Priority priority);

    Page<Task> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:startDate IS NULL OR t.dueDate >= :startDate) AND " +
            "(:endDate IS NULL OR t.dueDate <= :endDate) " +
            "ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findFilteredTasks(@Param("userId") Long userId,
                                 @Param("status") Task.Status status,
                                 @Param("priority") Task.Priority priority,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < :now AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("userId") Long userId,
                                @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId,
                                @Param("status") Task.Status status);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.user.id = :userId GROUP BY t.status")
    List<Object[]> countTasksByStatus(@Param("userId") Long userId);

    List<Task> findByStatusAndCompletedAtBefore(Task.Status status, LocalDateTime cutoffDate);

    List<Task> findByStatusAndDueDateBefore(Task.Status status, LocalDateTime now);
}