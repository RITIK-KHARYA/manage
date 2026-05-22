package com.example.demo.notification;

import com.example.demo.entity.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@ConditionalOnProperty(name = "notifications.email.enabled", havingValue = "true")
public class EmailNotificationService {

	private final JavaMailSender mailSender;
	private final String fromAddress;

	public EmailNotificationService(JavaMailSender mailSender,
			@Value("${spring.mail.username:}") String fromAddress) {
		this.mailSender = mailSender;
		this.fromAddress = fromAddress;
	}

	public void sendReminder(Task task) {
		if (!StringUtils.hasText(task.getEmail())) {
			return;
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);
		message.setTo(task.getEmail());
		message.setSubject("Todo reminder: " + task.getTitle());
		message.setText("Your task is due: " + task.getTitle() + "\n\n" + nullToBlank(task.getDescription()));
		mailSender.send(message);
	}

	private String nullToBlank(String value) {
		return value == null ? "" : value;
	}
}
