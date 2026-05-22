package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:5173}")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping
	public ApiResponse<List<TaskResponse>> findAll() {
		return ApiResponse.success(taskService.findAll());
	}

	@GetMapping("/{id}")
	public ApiResponse<TaskResponse> findById(@PathVariable Long id) {
		return ApiResponse.success(taskService.findById(id));
	}

	@PostMapping
	public ApiResponse<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
		return ApiResponse.success(taskService.create(request));
	}

	@PutMapping("/{id}")
	public ApiResponse<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
		return ApiResponse.success(taskService.update(id, request));
	}

	@PatchMapping("/{id}/complete")
	public ApiResponse<TaskResponse> complete(@PathVariable Long id) {
		return ApiResponse.success(taskService.complete(id));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		taskService.delete(id);
		return ApiResponse.success(null);
	}
}
