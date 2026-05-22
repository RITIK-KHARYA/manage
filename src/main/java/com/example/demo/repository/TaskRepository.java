package com.example.demo.repository;

import com.example.demo.entity.Task;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByCompletedFalseAndNotificationSentFalseAndDueDateLessThanEqual(LocalDateTime dueDate);
}
