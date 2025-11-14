package com.hack.botinki.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.entity.User;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;




@Service
public class ModelService {
  

    private final UserService userService;
    private final ProxyService proxyService;
    private OrtEnvironment env;
    private OrtSession session;
  
    @Autowired
    public ModelService(UserService userService, ProxyService proxyService) {
        this.userService = userService;
        this.proxyService = proxyService;
        try {
            env = OrtEnvironment.getEnvironment();

            // Загружаем файл из ресурсов
            InputStream modelStream = getClass().getClassLoader().getResourceAsStream("model.onnx");
            if (modelStream == null) {
                throw new RuntimeException("model.onnx не найден в resources");
            }

            // Сохраняем временно в файл, т.к. ONNXRuntime требует путь на диске
            File tempFile = File.createTempFile("model", ".onnx");
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = modelStream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }

            session = env.createSession(tempFile.getAbsolutePath(), new OrtSession.SessionOptions());
            System.out.println("ONNX модель успешно загружена");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить ONNX модель", e);
        }
    }

    

    public double predict(Task task, Integer freeHours) {
        try {
            // Рассчитываем days_until_deadline
            LocalDate now = LocalDate.now();
            long dud = ChronoUnit.DAYS.between(now, task.getDeadline());

            // Подготавливаем входной массив
            float[] inputData = new float[3];
            inputData[0] = (float) dud;                     // days_until_deadline
            inputData[1] = task.getEstimatedHours().floatValue(); // task_complexity
            inputData[2] = freeHours.floatValue();         // free_hours

            // Создаём ONNX тензор
            OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputData), new long[]{1, 3});

            // Выполняем предсказание
            OrtSession.Result result = session.run(Collections.singletonMap(session.getInputNames().iterator().next(), tensor));
            float[][] output = (float[][]) result.get(0).getValue();

            double priority = output[0][0];
            task.setPriority(priority);

            return priority;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при выполнении предсказания", e);
        }
    }
    
    public long[] execute(Long id) {
        List<Task> tasks = proxyService.getTasksByUserId(id);
        
        User user = userService.getUser(id);
        Integer freeHours = user.getFreeTime();
        
        long[] taskIds = tasks.stream()
            .map(task -> Map.entry(task.getId(), predict(task, freeHours)))
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .mapToLong(Map.Entry::getKey)
            .toArray();
        return taskIds;
    }
    
    
  
  
}
