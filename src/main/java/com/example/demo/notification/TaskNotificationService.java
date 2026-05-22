package com.example.demo.notification;

import com.example.demo.entity.Task;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaskNotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskNotificationService.class);

	private final Optional<EmailNotificationService> emailNotificationService;
	private final Optional<SmsNotificationService> smsNotificationService;

	public TaskNotificationService(Optional<EmailNotificationService> emailNotificationService,
			Optional<SmsNotificationService> smsNotificationService) {
		this.emailNotificationService = emailNotificationService;
		this.smsNotificationService = smsNotificationService;
	}

	public void sendReminder(Task task) {
		emailNotificationService.ifPresent(service -> service.sendReminder(task));
		smsNotificationService.ifPresent(service -> service.sendReminder(task));

		if (emailNotificationService.isEmpty() && smsNotificationService.isEmpty()) {
			LOGGER.info("Notifications disabled. Reminder due for task {}", task.getId());
		}
	}
}
