package com.example.demo.service;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.entity.Task;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

	private final TaskRepository taskRepository;

	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public List<TaskResponse> findAll() {
		return taskRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	public TaskResponse findById(Long id) {
		return toResponse(findTask(id));
	}

	public TaskResponse create(CreateTaskRequest request) {
		Task task = new Task();
		task.setTitle(request.title());
		task.setDescription(request.description());
		task.setDueDate(request.dueDate());
		task.setEmail(request.email());
		task.setPhoneNumber(request.phoneNumber());
		return toResponse(taskRepository.save(task));
	}

	public TaskResponse update(Long id, UpdateTaskRequest request) {
		Task task = findTask(id);
		task.setTitle(request.title());
		task.setDescription(request.description());
		task.setCompleted(request.completed());
		task.setDueDate(request.dueDate());
		task.setEmail(request.email());
		task.setPhoneNumber(request.phoneNumber());
		task.setNotificationSent(false);
		return toResponse(taskRepository.save(task));
	}

	public TaskResponse complete(Long id) {
		Task task = findTask(id);
		task.setCompleted(true);
		return toResponse(taskRepository.save(task));
	}

	public void delete(Long id) {
		Task task = findTask(id);
		taskRepository.delete(task);
	}

	private Task findTask(Long id) {
		return taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));
	}

	private TaskResponse toResponse(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.isCompleted(),
				task.getDueDate(),
				task.getEmail(),
				task.getPhoneNumber(),
				task.isNotificationSent(),
				task.getCreatedAt(),
				task.getUpdatedAt()
		);
	}
}
