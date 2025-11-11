package com.hack.botinki.demo.max;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hack.botinki.demo.max.MaxModels.MaxUpdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/max")
@Slf4j
@RequiredArgsConstructor
public class MaxBotController {

    private MaxBotService maxBotService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody MaxUpdate update) {
        log.info("Received webhook update: {}", update.getUpdateType());
        
        maxBotService.handleUpdate(update);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("verify") String verifyToken,
            @RequestParam(value = "challenge", required = false) String challenge) {
        log.info("Webhook verification request: verify={}, challenge={}", verifyToken, challenge);
        
        // Здесь должна быть логика верификации, если требуется
        if (challenge != null) {
            return ResponseEntity.ok(challenge);
        }
        
        return ResponseEntity.ok("OK");
    }
}
