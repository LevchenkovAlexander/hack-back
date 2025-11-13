package com.hack.botinki.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hack.botinki.demo.entity.Proxy;

public interface ProxyRepository extends JpaRepository<Proxy, Long>{
        List<Proxy> findByUserId(Long userId);
}
