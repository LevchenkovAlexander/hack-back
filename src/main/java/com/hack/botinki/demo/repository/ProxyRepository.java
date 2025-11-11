package com.hack.botinki.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hack.botinki.demo.entity.Proxy;
import com.hack.botinki.demo.entity.Task;

public interface ProxyRepository extends JpaRepository<Proxy, Long>{
        List<Task> findByUserId(Long userId);
}
