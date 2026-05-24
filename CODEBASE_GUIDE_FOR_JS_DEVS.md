# Codebase Guide for JavaScript, Node, and React Developers

This project is a full-stack todo/task app.

- Frontend: React + Vite in `frontend/`
- Backend: Java 21 + Spring Boot in `src/main/java/`
- Database access: Spring Data JPA
- Default backend port: `8080`
- Default frontend dev port: `5173`

If you know React and Node, think of this backend as an Express API, but with more structure, types, annotations, and framework conventions.

## Big Picture

The app lets users create, edit, complete, delete, and list tasks. A task can also have a due date, email, and phone number. A background scheduler checks every minute for due tasks and can send reminders by email or SMS.

Request flow:

```text
React component
  -> frontend/src/services/api.js
  -> HTTP request to Spring Boot
  -> TaskController
  -> TaskService
  -> TaskRepository
  -> Database
```

Response flow comes back in reverse:

```text
Database
  -> Repository
  -> Service
  -> Controller
  -> JSON response
  -> React state update
```

## Folder Structure

```text
frontend/
  src/
    App.jsx
    components/
    services/api.js

src/main/java/com/example/demo/
  DemoApplication.java
  controller/
  service/
  repository/
  entity/
  dto/
  exception/
  notification/
  scheduler/

src/main/resources/
  application.properties
  application-local.properties

pom.xml
```

Important idea: Java/Spring projects usually split backend code by responsibility. In Node, you might have `routes/`, `controllers/`, `models/`, and `services/`. This codebase follows the same idea, but with Java classes.

## Java and Spring Boot Compared to Node

| Node/Express concept | Spring Boot equivalent in this project |
| --- | --- |
| `package.json` | `pom.xml` |
| `npm install` dependencies | Maven dependencies |
| `npm run dev` | `./mvnw spring-boot:run` or `mvnw.cmd spring-boot:run` |
| Express app entry file | `DemoApplication.java` |
| Express router | `TaskController.java` |
| Controller/service functions | Java classes with methods |
| Mongoose/Prisma model | JPA `Task` entity |
| Database query layer | `TaskRepository.java` |
| Middleware error handler | `GlobalExceptionHandler.java` |
| `.env` config | `application.properties` plus environment variables |
| Cron job | `@Scheduled` method |

## Backend Entry Point

File: `src/main/java/com/example/demo/DemoApplication.java`

This starts the Spring Boot app:

```java
SpringApplication.run(DemoApplication.class, args);
```

Comparable Node idea:

```js
app.listen(8080);
```

`@SpringBootApplication` tells Spring to auto-configure the app and scan the package for controllers, services, repositories, and components.

`@EnableScheduling` enables background scheduled jobs, similar to enabling cron-like tasks.

## API Layer: Controller

File: `src/main/java/com/example/demo/controller/TaskController.java`

This is similar to an Express router.

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController
```

That means all routes inside this class start with:

```text
/api/tasks
```

Endpoints:

| HTTP method | URL | Purpose |
| --- | --- | --- |
| `GET` | `/api/tasks` | Get all tasks |
| `GET` | `/api/tasks/{id}` | Get one task |
| `POST` | `/api/tasks` | Create task |
| `PUT` | `/api/tasks/{id}` | Update task |
| `PATCH` | `/api/tasks/{id}/complete` | Mark task complete |
| `DELETE` | `/api/tasks/{id}` | Delete task |

Example comparison:

```js
// Express style
app.get('/api/tasks', async (req, res) => {
  res.json(await taskService.findAll());
});
```

```java
// Spring style
@GetMapping
public ApiResponse<List<TaskResponse>> findAll() {
    return ApiResponse.success(taskService.findAll());
}
```

Spring uses annotations like `@GetMapping`, `@PostMapping`, and `@RequestBody` instead of manually reading `req` and `res`.

## Business Logic: Service

File: `src/main/java/com/example/demo/service/TaskService.java`

This is where the main task logic lives. The controller stays thin and delegates to the service.

Responsibilities:

- Find all tasks
- Find one task by ID
- Create a task
- Update a task
- Complete a task
- Delete a task
- Convert database entities into API response objects

This is similar to a Node service file:

```js
export async function createTask(payload) {
  return prisma.task.create({ data: payload });
}
```

In this Java app:

```java
Task task = new Task();
task.setTitle(request.title());
return toResponse(taskRepository.save(task));
```

The service does not talk to HTTP directly. It talks to the repository.

## Database Model: Entity

File: `src/main/java/com/example/demo/entity/Task.java`

`Task` is a JPA entity. Think of it like a Prisma model, Sequelize model, or Mongoose schema.

```java
@Entity
@Table(name = "tasks")
public class Task
```

This tells Spring/JPA:

- This Java class maps to a database table.
- The table name is `tasks`.
- Each field maps to a column.

Fields:

```text
id
title
description
completed
dueDate
email
phoneNumber
notificationSent
createdAt
updatedAt
```

`@Id` marks the primary key.

`@GeneratedValue` means the database generates the ID.

`@PrePersist` runs before creating a row and sets `createdAt` and `updatedAt`.

`@PreUpdate` runs before updating a row and refreshes `updatedAt`.

## Repository: Database Access

File: `src/main/java/com/example/demo/repository/TaskRepository.java`

```java
public interface TaskRepository extends JpaRepository<Task, Long>
```

This gives you database methods automatically:

- `findAll()`
- `findById(id)`
- `save(task)`
- `delete(task)`

That is similar to getting CRUD methods from Prisma or Mongoose.

This project also defines one custom query method:

```java
findByCompletedFalseAndNotificationSentFalseAndDueDateLessThanEqual(LocalDateTime dueDate)
```

Spring Data JPA reads the method name and builds the SQL query automatically.

In plain English:

```text
Find tasks where:
- completed is false
- notificationSent is false
- dueDate is less than or equal to now
```

## DTOs: Request and Response Shapes

Folder: `src/main/java/com/example/demo/dto/`

DTO means Data Transfer Object. These are the shapes of data entering or leaving the API.

Files:

- `CreateTaskRequest.java`
- `UpdateTaskRequest.java`
- `TaskResponse.java`
- `ApiResponse.java`

In TypeScript terms, DTOs are close to interfaces or types:

```ts
type CreateTaskRequest = {
  title: string;
  description?: string;
  dueDate?: string;
  email?: string;
  phoneNumber?: string;
};
```

In this Java project, records are used:

```java
public record CreateTaskRequest(
    @NotBlank(message = "Title is required") String title,
    String description,
    LocalDateTime dueDate,
    @Email(message = "Email must be valid") String email,
    String phoneNumber
) {}
```

Java records are concise immutable data classes. They are useful for API payloads.

`@NotBlank` and `@Email` are validation rules. They are similar to using Zod, Yup, or Joi in a Node API.

## API Response Wrapper

File: `src/main/java/com/example/demo/dto/ApiResponse.java`

All successful controller responses are wrapped like this:

```json
{
  "success": true,
  "data": "...",
  "message": null
}
```

The frontend knows this and unwraps `response.data.data` in `frontend/src/services/api.js`.

## Error Handling

Folder: `src/main/java/com/example/demo/exception/`

`GlobalExceptionHandler.java` is similar to Express error middleware.

It handles:

- `ResourceNotFoundException` as `404`
- validation errors as `400`
- unknown errors as `500`

Express comparison:

```js
app.use((err, req, res, next) => {
  res.status(500).json({ success: false, message: err.message });
});
```

Spring version:

```java
@RestControllerAdvice
public class GlobalExceptionHandler
```

`@RestControllerAdvice` means this class can catch exceptions thrown by controllers and return JSON responses.

## Frontend API Client

File: `frontend/src/services/api.js`

This uses Axios:

```js
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});
```

So frontend calls:

```js
api.get('/tasks')
```

Which becomes:

```text
http://localhost:8080/api/tasks
```

Important detail: backend returns:

```json
{
  "success": true,
  "data": [...]
}
```

So frontend returns:

```js
return response.data.data;
```

## React App Flow

File: `frontend/src/App.jsx`

The React app:

- Loads tasks on mount with `useEffect`
- Stores tasks in `useState`
- Computes counts with `useMemo`
- Calls backend functions from `services/api.js`
- Re-fetches tasks after create, update, complete, or delete

Simple flow:

```text
User submits form
  -> handleSubmit()
  -> createTask() or updateTask()
  -> Axios request
  -> Spring Boot API
  -> Database update
  -> loadTasks()
  -> React state refresh
```

## Notifications

Folder: `src/main/java/com/example/demo/notification/`

There are three notification-related services:

- `TaskNotificationService`
- `EmailNotificationService`
- `SmsNotificationService`

`TaskNotificationService` is the coordinator. It tries to send reminders using email and SMS services if they are enabled.

Email is enabled only when:

```properties
notifications.email.enabled=true
```

SMS is enabled only when:

```properties
notifications.sms.enabled=true
```

This is controlled by:

```java
@ConditionalOnProperty(...)
```

That annotation means: only create this service if a config value says it should exist.

For email, the project uses Spring Mail.

For SMS, the project uses Twilio.

## Scheduler

File: `src/main/java/com/example/demo/scheduler/TaskReminderScheduler.java`

This runs every 60 seconds:

```java
@Scheduled(fixedRate = 60000)
```

It finds tasks that are:

- not completed
- not already notified
- due now or overdue

Then it sends a reminder and marks `notificationSent` as `true`.

Node equivalent would be something like:

```js
setInterval(async () => {
  const dueTasks = await findDueTasks();
  for (const task of dueTasks) {
    await sendReminder(task);
  }
}, 60000);
```

## Configuration

File: `src/main/resources/application.properties`

This file is like a mix of `.env`, app config, and framework config.

Examples:

```properties
server.port=8080
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/todo_app}
spring.datasource.username=${DB_USERNAME:root}
```

Syntax:

```text
${ENV_VAR_NAME:defaultValue}
```

That means:

- Use the environment variable if it exists.
- Otherwise use the default value.

Example:

```properties
DB_URL=jdbc:mysql://localhost:3306/todo_app
```

Local profile file:

```text
src/main/resources/application-local.properties
```

This uses an in-memory H2 database instead of MySQL. That is useful for quick local testing.

## Maven and `pom.xml`

File: `pom.xml`

This is Java's equivalent of `package.json`.

It defines:

- Java version: `21`
- Spring Boot version: `3.5.14`
- Dependencies
- Build plugins

Important dependencies:

| Dependency | Purpose |
| --- | --- |
| `spring-boot-starter-web` | Build REST APIs |
| `spring-boot-starter-data-jpa` | Database ORM |
| `spring-boot-starter-validation` | Validate request DTOs |
| `spring-boot-starter-mail` | Send email |
| `mysql-connector-j` | Connect to MySQL |
| `h2` | In-memory/local database |
| `twilio` | Send SMS |
| `lombok` | Reduces Java boilerplate, though this app mostly uses normal getters/setters |

## CORS

The controller has:

```java
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:5173}")
```

This allows the React dev server to call the backend.

Without CORS config, browser requests from:

```text
http://localhost:5173
```

to:

```text
http://localhost:8080
```

would be blocked by the browser.

## How to Run

Backend with default MySQL config:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend with local H2 database:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Then open:

```text
http://localhost:5173
```

## Mental Model for Frontend Developers

If you only know frontend, start with this:

1. `App.jsx` is the UI brain.
2. `services/api.js` is the frontend's bridge to the backend.
3. `TaskController.java` receives HTTP requests.
4. `TaskService.java` decides what to do.
5. `TaskRepository.java` talks to the database.
6. `Task.java` defines what a task looks like in the database.
7. DTO files define what JSON is allowed in and sent out.
8. `GlobalExceptionHandler.java` converts errors into clean JSON.
9. `TaskReminderScheduler.java` runs in the background every minute.

## Most Important Differences from Node

Java is compiled. Many mistakes are caught before the app starts. Node usually catches more mistakes at runtime unless you use TypeScript carefully.

Spring uses dependency injection heavily. Classes ask for what they need in the constructor:

```java
public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
}
```

Spring creates and passes the dependency automatically. In Node, you often import dependencies manually.

Spring uses annotations to declare behavior:

```java
@RestController
@Service
@Entity
@Scheduled
```

These are like framework instructions attached to classes and methods.

Java is more explicit with types:

```java
public ApiResponse<List<TaskResponse>> findAll()
```

This tells you exactly what the method returns.

Spring Data JPA generates many database operations for you. You do not see SQL in most of this codebase because the repository layer handles it.

## One Request Example

When the user creates a task:

1. React form calls `handleSubmit()` in `App.jsx`.
2. `handleSubmit()` calls `createTask(payload)` from `api.js`.
3. Axios sends `POST http://localhost:8080/api/tasks`.
4. `TaskController.create()` receives the request.
5. `@Valid` checks the request rules.
6. `TaskService.create()` creates a `Task` entity.
7. `taskRepository.save(task)` inserts it into the database.
8. The saved task is converted to `TaskResponse`.
9. Controller wraps it in `ApiResponse.success(...)`.
10. React receives the response and reloads the task list.

## What to Learn First

For this codebase, learn in this order:

1. Java classes, methods, fields, constructors, and records
2. Spring annotations: `@RestController`, `@Service`, `@Entity`, `@Repository`, `@Scheduled`
3. Request/response DTOs
4. JPA entities and repositories
5. `application.properties`
6. Validation and exception handling
7. Maven basics

Once those make sense, the backend will feel similar to a typed and structured Node API.
