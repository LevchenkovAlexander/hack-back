package com.hack.botinki.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack.botinki.demo.entity.Proxy;
import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.repository.ProxyRepository;

@Service
public class ProxyService {
    
    private final ProxyRepository proxyRepository;

    private final TaskService taskService;

    @Autowired
    ProxyService (ProxyRepository proxyRepository, TaskService taskService){
        this.proxyRepository=proxyRepository;
        this.taskService = taskService;
    }

    public void addInstance(Long userId, Long taskId){ 
        Proxy inst = new Proxy();
        inst.setTaskId(taskId);
        inst.setUserId(userId);
        proxyRepository.save(inst);
    }
    
    public List<Task> getTasksByUserId(Long userId){
        List<Task> tasks= new ArrayList<>();
        for (Proxy proxy : proxyRepository.findByUserId(userId)) {
            tasks.add(taskService.getTask(proxy.getTaskId()));
        }
        return tasks;
    }
}
