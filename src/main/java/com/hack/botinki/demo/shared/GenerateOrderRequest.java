package com.hack.botinki.demo.shared;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;

@Data
public class GenerateOrderRequest {
    private List<TaskTO> tasks;
    private Long Uid;

     @JsonSetter("Uid")
    public void setUid(String uid) {
        this.Uid = uid != null ? Long.valueOf(uid) : null;
    }

    private Integer freeHours; // опциональное поле для обновления свободных часов
}
