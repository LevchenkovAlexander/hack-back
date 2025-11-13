package com.hack.botinki.demo.shared;

import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;

@Data
public class FreeHoursRequest {
    private Integer freeHours;
    private Long Uid;

    @JsonSetter("Uid")
    public void setUid(String uid) {
        this.Uid = uid != null ? Long.valueOf(uid) : null;
    }
}