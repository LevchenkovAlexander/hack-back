package com.hack.botinki.demo.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskTO {
    private Long Uid;
    private String name;
    private String deadline;
    private Double estimatedHours;

    @JsonSetter("Uid")
    public void setUid(String uid) {
        this.Uid = uid != null ? Long.valueOf(uid) : null;
    }

    public LocalDate getDeadline() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // TODO Auto-generated method stub
        return LocalDate.parse(deadline, formatter);
    }
}