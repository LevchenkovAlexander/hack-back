package com.hack.botinki.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack.botinki.demo.entity.User;
import com.hack.botinki.demo.exception.UserNotFoundException;
import com.hack.botinki.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public User getUser(Long id) {
    	 return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)); 
     }

}