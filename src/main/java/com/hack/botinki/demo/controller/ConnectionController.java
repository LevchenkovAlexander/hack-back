package com.hack.botinki.demo.controller;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.entity.User;
import com.hack.botinki.demo.service.ModelService;
import com.hack.botinki.demo.service.ProxyService;
import com.hack.botinki.demo.service.TaskService;
import com.hack.botinki.demo.service.UserService;
import com.hack.botinki.demo.shared.FreeHoursRequest;
import com.hack.botinki.demo.shared.GenerateOrderRequest;
import com.hack.botinki.demo.shared.GenerateOrderResponse;
import com.hack.botinki.demo.shared.ResultRequest;
import com.hack.botinki.demo.shared.TaskTO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class ConnectionController {

    private final ModelService modelService;
    private final TaskService taskService;
    private final UserService userService;
    private final ProxyService proxyService;

    @Autowired
    ConnectionController(ModelService modelService, TaskService taskService, UserService userService, ProxyService proxyService) {
        this.modelService = modelService;
        this.taskService = taskService;
        this.userService = userService;
        this.proxyService = proxyService;
        log.info("ConnectionController инициализирован");
    }

    // === /start/{userId} — Инициализация пользователя ===
    @GetMapping("/start/{userId}")
    public ResponseEntity<String> start(@PathVariable Long userId) {
        log.info("Вызов /start/{}", userId);

        try {
            userService.getUser(userId);
            log.info("Пользователь уже существует: ID = {}", userId);
            return ResponseEntity.ok("User " + userId + " already initialized");
        } catch (Exception e) {
            log.info("Создаём нового пользователя: ID = {}", userId);
            User newUser = new User();
            newUser.setId(userId);
            newUser.setFreeTime(0);
            userService.addUser(newUser);
            log.info("Пользователь создан: ID = {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("User " + userId + " created");
        }
    }

    // === Генерация порядка задач ===
    @PostMapping("/generate-order")
    public ResponseEntity<GenerateOrderResponse> generateOrder(@RequestBody GenerateOrderRequest request) {
        log.info("generate-order вызван: Uid = {}, freeHours = {}", request.getUid(), request.getFreeHours());

        try {
            Long Uid = request.getUid();

            // Обновляем свободные часы, если они переданы в запросе
            if (request.getFreeHours() != null) {
                try {
                    User user = userService.getUser(Uid);
                    user.setFreeTime(request.getFreeHours());
                    userService.addUser(user);
                    log.info("Свободные часы обновлены через generate-order: Uid = {}, freeHours = {}", Uid, request.getFreeHours());
                } catch (Exception e) {
                    log.warn("Не удалось обновить свободные часы, продолжаем с существующими значениями", e);
                }
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            long[] taskIds = modelService.execute(Uid);
            List<TaskTO> optimizedTasks = new ArrayList<>();

            for (long id : taskIds) {
                Task task = taskService.getTask(id);
                TaskTO taskToList = new TaskTO(Uid, task.getName(), formatter.format(task.getDeadline()), task.getEstimatedHours());
                optimizedTasks.add(taskToList);
            }

            GenerateOrderResponse response = new GenerateOrderResponse();
            response.setOrderedTasks(optimizedTasks.reversed());

            log.info("generate-order успешен: {} задач", optimizedTasks.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка в generate-order", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // === Добавление задачи ===
    @PostMapping("/task")
    public ResponseEntity<Void> submitTask(@RequestBody TaskTO taskRequest) {
        log.info("submitTask вызван: Uid = {}, name = {}", taskRequest.getUid(), taskRequest.getName());

        try {
            Long Uid = taskRequest.getUid();
            Task taskToDB = new Task();
            taskToDB.setName(taskRequest.getName());
            taskToDB.setDeadline(taskRequest.getDeadline());
            taskToDB.setEstimatedHours(taskRequest.getEstimatedHours());
            
            // Устанавливаем complexity равным estimatedHours (исходная сложность)
            // Priority будет вычислен позже в ModelService
            taskToDB.setComplexity(taskRequest.getEstimatedHours().intValue());
            taskToDB.setPriority(0.0); // Временное значение, будет обновлено в ModelService

            taskService.addTask(taskToDB);
            proxyService.addInstance(Uid, taskToDB.getId());

            log.info("Задача добавлена: ID = {}, complexity = {}", taskToDB.getId(), taskToDB.getComplexity());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Ошибка при добавлении задачи", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === Сохранение свободных часов ===
    @PostMapping("/free-hours")
    public ResponseEntity<Map<String, Boolean>> submitFreeHours(@RequestBody FreeHoursRequest freeHoursRequest) {
        log.info("submitFreeHours вызван: Uid = {}, freeHours = {}", freeHoursRequest.getUid(), freeHoursRequest.getFreeHours());

        try {
            Long Uid = freeHoursRequest.getUid();
            Integer freeHours = freeHoursRequest.getFreeHours();

            User user = userService.getUser(Uid);
            user.setFreeTime(freeHours);
            userService.addUser(user);

            log.info("Свободные часы обновлены: Uid = {}, freeHours = {}", Uid, freeHours);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("ok", true));
        } catch (Exception e) {
            log.error("Ошибка при сохранении свободных часов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === Обновление результата ===
    @PostMapping("/result")
    public ResponseEntity<Void> submitResult(@RequestBody ResultRequest request) {
        log.info("submitResult вызван: Uid = {}, number = {}", request.getUid(), request.getNumber());

        try {
            Long Uid = request.getUid();
            long[] taskIds = modelService.execute(Uid);
            Long idToChange = taskIds[request.getNumber() - 1];
            Task taskToChange = taskService.getTask(idToChange);
            Integer percent = request.getPercent();

            if (percent.equals(100)) {
                taskService.removeTask(idToChange);
                log.info("Задача удалена (100%): ID = {}", idToChange);
            } else {
                double percentd = Double.valueOf(percent);
                double complexity = Double.valueOf(taskToChange.getComplexity());
                taskToChange.setEstimatedHours(complexity * percentd / 100);
                taskService.addTask(taskToChange);
                log.info("Задача обновлена ({}%): ID = {}, newHours = {}", percent, idToChange, taskToChange.getEstimatedHours());
            }

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Ошибка при обновлении результата", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === ТЕСТОВЫЙ ECHO ===
    @RequestMapping(value = "/echo", method = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS
    })
    public ResponseEntity<Map<String, Object>> echoRequest(
            @RequestBody(required = false) String body,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        log.info("==== ECHO ENDPOINT ====");
        log.info("Method: {}", request.getMethod());
        log.info("Path: {}", request.getRequestURI());
        log.info("Headers: {}", headers);
        log.info("Body: {}", body);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("method", request.getMethod());
        response.put("path", request.getRequestURI());
        response.put("headers", headers);
        response.put("body", body);

        return ResponseEntity.ok(response);
    }

    // === Health check ===
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("localtonet", "connected");
        response.put("time", java.time.LocalDateTime.now().toString());
        log.info("health check: OK");
        return ResponseEntity.ok(response);
    }

    // === Получить всех пользователей ===
    @GetMapping("/users")
    public ResponseEntity<List<Long>> users() {
        log.info("/users вызван");
        List<Long> userIds = userService.getAllIds();
        log.info("Возвращаем пользователей: {}", userIds);
        return ResponseEntity.ok(userIds);
    }

    // === Получить информацию о пользователе ===
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long userId) {
        log.info("getUser вызван: userId = {}", userId);
        
        try {
            User user = userService.getUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("freeTime", user.getFreeTime());
            
            log.info("Информация о пользователе получена: ID = {}, freeTime = {}", userId, user.getFreeTime());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при получении информации о пользователе", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // === Получить задачи пользователя ===
    @GetMapping("/user/{userId}/tasks")
    public ResponseEntity<List<TaskTO>> getUserTasks(@PathVariable Long userId) {
        log.info("getUserTasks вызван: userId = {}", userId);
        
        try {
            List<Task> tasks = proxyService.getTasksByUserId(userId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            List<TaskTO> taskTOs = new ArrayList<>();
            
            for (Task task : tasks) {
                TaskTO taskTO = new TaskTO(
                    userId,
                    task.getName(),
                    formatter.format(task.getDeadline()),
                    task.getEstimatedHours()
                );
                taskTOs.add(taskTO);
            }
            
            log.info("Получено задач для пользователя {}: {}", userId, taskTOs.size());
            return ResponseEntity.ok(taskTOs);
        } catch (Exception e) {
            log.error("Ошибка при получении задач пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}