package com.hack.botinki.demo.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskTO {
    private Long Uid;
    private String name;
    private String deadline;
    private Double estimatedHours;

    public LocalDate getDeadline() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // TODO Auto-generated method stub
        return LocalDate.parse(deadline, formatter);
    }
}