package com.hack.botinki.demo.max;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public class MaxModels {

	@Data
	public static class MaxUser {
	    @JsonProperty("user_id")
	    private Long userId;
	    
	    private String username;
	    
	    @JsonProperty("first_name")
	    private String firstName;
	    
	    @JsonProperty("last_name")
	    private String lastName;
	    
	    @JsonProperty("is_bot")
	    private Boolean isBot;
	}
	
	@Data
	public static class MaxUpdate {
	    @JsonProperty("update_type")
	    private String updateType;
	    
	    private Long timestamp;
	    
	    @JsonProperty("chat_id")
	    private Long chatId;
	    
	    private MaxUser user;
	    
	    private String payload;
	    
	    private MaxMessage message;
	}
	
	@Data
	public static class MaxMessage {
	    private String text;
	    private MaxUser sender;
	    private MaxRecipient recipient;
	}
	
	@Data
	public static class MaxRecipient {
	    @JsonProperty("chat_id")
	    private Long chatId;
	    
	    @JsonProperty("user_id")
	    private Long userId;
	}
	
	@Data
	public static class NewMessageBody {
	    private String text;
	    private List<Attachment> attachments;
	    
	}

	@Data
	public static class Attachment {
		private String type;
		private Object payload;
	}
	
	@Data
	public static class InlineKeyboard {
	    private List<List<Button>> buttons;
	    
	}

	@Data
	public static class Button {
		private String type;
		private String text;
		
		@com.fasterxml.jackson.annotation.JsonProperty("web_app")
		private WebApp webApp;
		
		
	}

	@Data
	public static class WebApp {
		private String url;
	}

}
