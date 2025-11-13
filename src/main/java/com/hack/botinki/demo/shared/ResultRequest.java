package com.hack.botinki.demo.shared;

import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;

@Data
public class ResultRequest {
    private Long Uid;
    private Integer number;
    private Integer percent;

    @JsonSetter("Uid")
    public void setUid(String uid) {
        this.Uid = uid != null ? Long.valueOf(uid) : null;
    }
}
