package com.hack.botinki.demo.shared;

import java.util.List;

import lombok.Data;

@Data
public class GenerateOrderResponse {
    private List<TaskTO> orderedTasks;

}
