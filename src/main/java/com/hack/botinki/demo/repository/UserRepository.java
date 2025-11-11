package com.hack.botinki.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hack.botinki.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
