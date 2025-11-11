package com.hack.botinki.demo.shared;

import lombok.Data;

@Data
public class SubmitTaskRequest {
    private TaskTO task;
    private Long Uid;
}