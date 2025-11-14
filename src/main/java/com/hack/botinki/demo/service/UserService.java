package com.hack.botinki.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack.botinki.demo.entity.User;
import com.hack.botinki.demo.exception.UserNotFoundException;
import com.hack.botinki.demo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // ← ДОБАВЬТЕ ЭТОТ ИМПОРТ

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class); // ← ЛОГГЕР

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("UserService инициализирован. Репозиторий: {}", userRepository.getClass().getName());
    }

    /**
     * Сохраняет пользователя в БД.
     * Если пользователь уже существует — обновляет.
     */
    public void addUser(User user) {
        log.info("addUser() вызван: ID = {}, freeTime = {}", user.getId(), user.getFreeTime());
        try {
            User saved = userRepository.save(user);
            log.info("Пользователь успешно сохранён: ID = {}", saved.getId());
        } catch (Exception e) {
            log.error("ОШИБКА при сохранении пользователя ID = {}", user.getId(), e);
            throw e; // ← чтобы ошибка не потерялась
        }
    }

    /**
     * Находит пользователя по ID.
     * Если нет — бросает UserNotFoundException.
     */
    public User getUser(Long id) {
        log.info("getOrCreateUser() вызван: ID = {}", id);
        return userRepository.findById(id).orElseGet(() -> {
            log.info("Пользователь не найден, создаём: ID = {}", id);
            User newUser = new User();
            newUser.setId(id);
            newUser.setFreeTime(0);
            addUser(newUser);
            return newUser;
        });
    }

    /**
     * Возвращает список всех ID пользователей из БД.
     */
    public List<Long> getAllIds() {
        log.info("getAllIds() вызван");
        List<User> allUsers = userRepository.findAll();
        log.info("Найдено пользователей в БД: {}", allUsers.size());

        List<Long> ids = new ArrayList<>();
        for (User user : allUsers) {
            Long id = user.getId();
            ids.add(id);
            log.info("  → ID из БД: {}", id);
        }

        log.info("getAllIds() возвращает: {}", ids);
        return ids;
    }
}