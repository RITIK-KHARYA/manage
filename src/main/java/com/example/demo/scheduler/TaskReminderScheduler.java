package com.example.demo.scheduler;

import com.example.demo.entity.Task;
import com.example.demo.notification.TaskNotificationService;
import com.example.demo.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskReminderScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskReminderScheduler.class);

	private final TaskRepository taskRepository;
	private final TaskNotificationService notificationService;

	public TaskReminderScheduler(TaskRepository taskRepository, TaskNotificationService notificationService) {
		this.taskRepository = taskRepository;
		this.notificationService = notificationService;
	}

	@Scheduled(fixedRate = 60000)
	public void sendDueTaskReminders() {
		List<Task> dueTasks = taskRepository
				.findByCompletedFalseAndNotificationSentFalseAndDueDateLessThanEqual(LocalDateTime.now());

		for (Task task : dueTasks) {
			try {
				notificationService.sendReminder(task);
				task.setNotificationSent(true);
				taskRepository.save(task);
			} catch (Exception exception) {
				LOGGER.error("Failed to send reminder for task {}", task.getId(), exception);
			}
		}
	}
}
