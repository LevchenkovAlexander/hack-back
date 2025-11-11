package com.hack.botinki.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack.botinki.demo.entity.Proxy;
import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.repository.ProxyRepository;

@Service
public class ProxyService {
    
    private final ProxyRepository proxyRepository;

    @Autowired
    ProxyService (ProxyRepository proxyRepository){
        this.proxyRepository=proxyRepository;
    }

    public void addInstance(Long taskId, Long userId){ 
        Proxy inst = new Proxy();
        inst.setTaskId(taskId);
        inst.setUserId(userId);
        proxyRepository.save(inst);
    }
    
    public List<Task> getTasksByUserId(Long userId){
        return proxyRepository.findByUserId(userId);
    }
}
