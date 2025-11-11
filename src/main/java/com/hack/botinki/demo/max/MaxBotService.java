package com.hack.botinki.demo.max;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hack.botinki.demo.entity.User;
import com.hack.botinki.demo.max.MaxModels.*;
import com.hack.botinki.demo.max.MaxProperties.MaxApiProperties;
import com.hack.botinki.demo.max.MaxProperties.MaxBotProperties;
import com.hack.botinki.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaxBotService {
	
    private RestTemplate restTemplate;
    private MaxBotProperties botProperties;
    private MaxApiProperties apiProperties;
    private UserService userService;

    public void handleUpdate(MaxUpdate update) {
        try {
            switch (update.getUpdateType()) {
                case "message_created":
                    handleMessage(update.getMessage());
                    break;
                case "bot_started":
                    handleBotStarted(update);
                    break;
                default:
                	log.debug("Unhandled update type: {}", update.getUpdateType());
            }
        } catch (Exception e) {
        	log.error("Error handling update", e);
        }
    }

    private void handleMessage(MaxMessage message) {
        if (message != null && "/start".equals(message.getText())) {
            Long userId = message.getSender().getUserId();
            String username = message.getSender().getUsername();
            handleStartCommand(userId, username, message.getRecipient().getChatId());
        }
    }

    private void handleBotStarted(MaxUpdate update) {
        Long userId = update.getUser().getUserId();
        String username = update.getUser().getUsername();
        Long chatId = update.getChatId();
        handleStartCommand(userId, username, chatId);
    }

    private void handleStartCommand(Long userId, String username, Long chatId) {
        // Сохраняем пользователя в БД при первом запуске
        saveUserIfNotExists(userId, username);
        
        // Отправляем приветственное сообщение с кнопкой
        sendWelcomeMessage(chatId, userId);
    }

    private void saveUserIfNotExists(Long userId, String username) {
        try {
            userService.getUser(userId);
            log.debug("User already exists: {}", userId);
        } catch (Exception e) {
            User newUser = new User();
            newUser.setId(userId);
            newUser.setUsername(username != null ? username : "user_" + userId);
            newUser.setFreeTime(0);
            userService.addUser(newUser);
            log.info("Created new user: {} with username: {}", userId, username);
        }
    }

    private void sendWelcomeMessage(Long chatId, Long userId) {
        String welcomeText = "Добро пожаловать! Нажмите кнопку ниже чтобы открыть мини-приложение для управления задачами.";

        // Создаем кнопку для открытия мини-приложения
        Button openAppButton = new Button();
        openAppButton.setType("open_app");
        openAppButton.setText("открыть мини приложение");
        
        WebApp webApp = new WebApp();
        webApp.setUrl(apiProperties.getApp().getUrl() + userId);
        openAppButton.setWebApp(webApp);

        // Создаем клавиатуру
        InlineKeyboard keyboard = new InlineKeyboard();
        keyboard.setButtons(Collections.singletonList(
            Collections.singletonList(openAppButton)
        ));

        // Создаем вложение с клавиатурой
        Attachment keyboardAttachment = new Attachment();
        keyboardAttachment.setType("inline_keyboard");
        keyboardAttachment.setPayload(keyboard);

        // Создаем тело сообщения
        NewMessageBody messageBody = new NewMessageBody();
        messageBody.setText(welcomeText);
        messageBody.setAttachments(Collections.singletonList(keyboardAttachment));

        // Отправляем сообщение
        sendMessage(chatId, null, messageBody);
    }

    public void sendMessage(Long chatId, Long userId, NewMessageBody messageBody) {
        String url = apiProperties.getApiUrl() + "/messages?access_token=" + botProperties.getToken();
        
        if (chatId != null) {
            url += "&chat_id=" + chatId;
        }
        if (userId != null) {
            url += "&user_id=" + userId;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NewMessageBody> request = new HttpEntity<>(messageBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Message sent successfully");
            } else {
                log.error("Failed to send message: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error sending message to Max API", e);
        }
    }
}
