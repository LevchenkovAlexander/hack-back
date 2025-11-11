package com.hack.botinki.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.exception.TaskNotFoundException;
import com.hack.botinki.demo.repository.TaskRepository;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(Task task) {
        taskRepository.save(task);    
    }

    
    public void removeTask(Long id) {
        taskRepository.deleteById(id);
    }
    

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id)); 
    }



}
