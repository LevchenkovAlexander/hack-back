package com.hack.botinki.demo.service;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.model.PMMLUtil;
import org.xml.sax.SAXException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.hack.botinki.demo.entity.Task;
import com.hack.botinki.demo.entity.User;

import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.TargetField;

public class ModelService {
	
	private final TaskService taskService;
	private final UserService userService;
	private final ProxyService proxyService;

	private ModelEvaluator<?> evaluator;
	
	public ModelService(TaskService taskService, UserService userService, ProxyService proxyService) {
		 this.taskService = taskService;
		 this.proxyService = proxyService;
		 this.userService = userService;	

		 PMML pmml;
		 try {
			pmml = PMMLUtil.unmarshal(new FileInputStream(new File("resources/model.pmml")));
			Model model = pmml.getModels().get(0);
			ModelEvaluator<?> evaluator = ModelEvaluatorFactory.newInstance()
				    .newModelEvaluator(pmml, model);
			evaluator.verify();
			this.evaluator = evaluator;
			///логи прописать
		 } catch (FileNotFoundException | ParserConfigurationException | SAXException | JAXBException e) {
			///здесь тож
			e.printStackTrace();
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
		
		Map<String, Object>	data = new HashMap<>();
		data.put("days_until_deadline", dud);
		data.put("task_est_hours", task.getEstimatedHours());
		data.put("free_hours", freeHours);
		
	    
	    Map<String, ?> results = evaluator.evaluate(data);
	    
	    List<TargetField> targetFields = evaluator.getTargetFields();
	    if (targetFields.isEmpty()) {
	        throw new IllegalStateException("Модель не имеет целевых полей");
	    }
	    
	    Object prediction = results.get(targetFields.get(0).getName());
	    task.setPriority((Double) prediction);
	    return (Double) prediction;
    }
	
	
}
