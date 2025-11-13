package com.hack.botinki.demo.service;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.model.PMMLUtil;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.entity.User;

import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jpmml.evaluator.LoadingModelEvaluatorBuilder;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.TargetField;

@Service
public class ModelService {
  
  private final UserService userService;
  private final ProxyService proxyService;

  private ModelEvaluator<?> evaluator;
  
  public ModelService(UserService userService, ProxyService proxyService) {
    this.proxyService = proxyService;
    this.userService = userService;
    
    try {
            // Загружаем PMML модель
            InputStream is = getClass().getClassLoader().getResourceAsStream("model.pmml");
            if (is == null) {
                throw new RuntimeException("Файл model.pmml не найден в classpath");
            }
            
            PMML pmml = PMMLUtil.unmarshal(is);
            Model model = pmml.getModels().get(0); 
            this.evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml, model);
            this.evaluator.verify();
            
            this.evaluator.verify();
            System.out.println("Модель PMML успешно загружена и инициализирована");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Не удалось загрузить модель PMML", e);
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
  
  private Double predict(Task task, Integer freeHours) {
    ModelEvaluator<?> evaluator = this.evaluator;
    
    LocalDate deadline = task.getDeadline();
    LocalDate now = LocalDate.now();
    long dud = ChronoUnit.DAYS.between(now, deadline);
    
    Map<String, Object> inputData = new HashMap<>();
        inputData.put("days_until_deadline", (float) dud);
        inputData.put("task_complexity", task.getEstimatedHours()/20);
        inputData.put("free_hours", (float) freeHours);
        
    
        try {
            Map<String, ?> results = evaluator.evaluate(inputData);
            
            List<TargetField> targetFields = evaluator.getTargetFields();
            if (targetFields.isEmpty()) {
                throw new IllegalStateException("Модель не имеет целевых полей");
            }
            
            String targetFieldName = targetFields.get(0).getName();
            Object prediction = results.get(targetFieldName);
            
            Double priority;
            if (prediction instanceof Number) {
                priority = ((Number) prediction).doubleValue();
            } else {
                throw new IllegalStateException("Предсказание имеет нечисловой тип: " + prediction.getClass());
            }
            
            task.setPriority(priority);
            return priority;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при выполнении предсказания для задачи: " + task.getId(), e);
        }
    }
  
  
}
