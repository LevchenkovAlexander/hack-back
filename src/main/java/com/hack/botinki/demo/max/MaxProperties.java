package com.hack.botinki.demo.max;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

public class MaxProperties {
	
	@Data
	@Component
	@ConfigurationProperties(prefix = "max.bot")
	public class MaxBotProperties {
	    private String token;
	}
	
	@Data
	@Component
	@ConfigurationProperties(prefix = "max")
	public class MaxApiProperties {
	    private String apiUrl;
	    private App app;
	    
	    @Data
	    public static class App {
	        private String url;
	    }
	}
}
