package com.findu.demo.manager;

import com.findu.demo.model.ChatMessage;
import com.findu.demo.user.User;

public class ChatManager {
	private static ChatManager mInstance;
	
	public ChatManager getInstance()
	{
		return mInstance;
	}
	
	private ChatManager()
	{
		
	}
	
	public void sendMessagetoUser(User user, ChatMessage msg)
	{
		
	}
	
}
