package com.example.demo.dto;

import java.time.LocalDateTime;

public record TaskResponse(
		Long id,
		String title,
		String description,
		boolean completed,
		LocalDateTime dueDate,
		String email,
		String phoneNumber,
		boolean notificationSent,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
