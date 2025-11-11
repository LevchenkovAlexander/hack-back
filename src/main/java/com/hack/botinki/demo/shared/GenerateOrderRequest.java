package com.hack.botinki.demo.shared;

import java.util.List;

import lombok.Data;

@Data
public class GenerateOrderRequest {
    private List<TaskTO> tasks;
    private Long Uid;
}
