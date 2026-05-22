package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateTaskRequest(
		@NotBlank(message = "Title is required") String title,
		String description,
		LocalDateTime dueDate,
		@Email(message = "Email must be valid") String email,
		String phoneNumber
) {
}
