package com.hack.botinki.demo.controller;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.entity.User;
import com.hack.botinki.demo.service.ModelService;
import com.hack.botinki.demo.service.ProxyService;
import com.hack.botinki.demo.service.TaskService;
import com.hack.botinki.demo.service.UserService;
import com.hack.botinki.demo.shared.FreeHoursRequest;
import com.hack.botinki.demo.shared.GenerateOrderRequest;
import com.hack.botinki.demo.shared.GenerateOrderResponse;
import com.hack.botinki.demo.shared.ResultRequest;
import com.hack.botinki.demo.shared.TaskTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") 
public class ConnectionController {

	private ModelService modelService; 
	private TaskService taskService; 
	private UserService userService; 
    private ProxyService proxyService;
	
    @PostMapping("/generate-order")
    public ResponseEntity<GenerateOrderResponse> generateOrder(@RequestBody GenerateOrderRequest request) {
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            Long Uid = request.getUid();
            long[] taskIds = modelService.execute(Uid);
            List<TaskTO> optimizedTasks = new ArrayList<>();
            for (long id : taskIds) {
            	Task task = taskService.getTask(id);
            	TaskTO taskToList = new TaskTO(Uid, task.getName(), formatter.format(task.getDeadline()), task.getEstimatedHours());
            	optimizedTasks.add(taskToList);
            }
            GenerateOrderResponse response = new GenerateOrderResponse();
            response.setOrderedTasks(optimizedTasks.reversed());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/task")
    public ResponseEntity<Void> submitTask(@RequestBody TaskTO taskRequest) {
       
        try {
        	Long Uid = taskRequest.getUid();
        	Task taskToDB = new Task();
        	taskToDB.setName(taskRequest.getName());
            taskToDB.setDeadline(taskRequest.getDeadline());
            taskToDB.setEstimatedHours(taskRequest.getEstimatedHours());
            taskToDB.setEstimatedHours(taskRequest.getEstimatedHours());
            taskService.addTask(taskToDB);
            proxyService.addInstance(Uid, taskToDB.getId());
         
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/free-hours")
    public ResponseEntity<Void> submitFreeHours(@RequestBody FreeHoursRequest freeHoursRequest) {
        
        try {
        	Long Uid = freeHoursRequest.getUid();
        	Integer freeHours = freeHoursRequest.getFreeHours();
            User user = userService.getUser(Uid);
            user.setFreeTime(freeHours);
            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/result")
    public ResponseEntity<Void> submitResult(@RequestBody ResultRequest request) {
        
        try {
        	Long Uid = request.getUid();
            long[] taskIds = modelService.execute(Uid);
            Long idToChange = taskIds[request.getNumber()-1];
            Task taskToChange = taskService.getTask(idToChange);
        	Integer percent = request.getPercent();
        	if (percent.equals(100)) {
        		taskService.removeTask(idToChange);
        	}
        	else {
        		double percentd = Double.valueOf(percent);
        		double complexity = Double.valueOf(taskToChange.getComplexity());
        		taskToChange.setEstimatedHours(complexity*percentd/100);
        		taskService.addTask(taskToChange);
        	}    
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}