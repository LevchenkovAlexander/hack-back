package com.hack.botinki.demo.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.service.ProxyService;
import com.hack.botinki.demo.service.TaskService;

@RestController
@RequestMapping("/users")
public class UserTaskController {

    private final TaskService taskService;
    private final ProxyService proxyService;

    public UserTaskController(TaskService taskService, ProxyService proxyService) {
        this.taskService = taskService;
        this.proxyService = proxyService;
    }

    @GetMapping("/{id}/tasks")
    public List<Task> getTasksByUserId(@PathVariable Long id) {
        return proxyService.getTasksByUserId(id);
    }
}