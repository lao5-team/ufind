package com.findu.demo.controller;

import junit.framework.Assert;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.findu.demo.R;
import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.adapter.ChatAdapter;

public class ChatController {
	private Activity mActivity = null;
	private View mView = null;
	private EditText mEtxMessage;
	private Button mBtnSend;
	private ListView mLvMessages;
	private ChatAdapter mChatAdapter;
	private ChatManager mChatManager = null;
	private Chat mChat = null;
	private Handler mUIHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			mEtxMessage.setText("");
			mChatAdapter.notifyDataSetChanged();
		}
	};
	
	public ChatController(Activity activity, View view)
	{
		Assert.assertNotNull(activity);
		Assert.assertNotNull(view);
		mActivity = activity;
		mView = view;
		mChatManager = FriendsApplication.getInstance().getConnection().getChatManager();
		mChatManager.addChatListener(new MyMessageListeners());
		mChat = mChatManager.createChat(activity.getIntent().getStringExtra("username"), null);
		mChatAdapter = new ChatAdapter(activity); 
		initUI();
	}
	
	
	private void initUI()
	{
		mEtxMessage = (EditText)mView.findViewById(R.id.etx_input);
		mBtnSend = (Button)mView.findViewById(R.id.btn_send_msg);
		mLvMessages = (ListView)mView.findViewById(R.id.lv_messages);
		mLvMessages.setAdapter(mChatAdapter);
		mBtnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage(mEtxMessage.getEditableText().toString());	
			}
		});
	}
	
	private void sendMessage(String value)
	{
		/** 发送消息 */
		/** 用message对象发送消息 */
		Message message = new Message();
		message.setBody(value);
		message.setProperty("color", "red");
		try {
			mChat.sendMessage(message);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mChatAdapter.addMessage(0, value);
		android.os.Message message1 = mUIHandler.obtainMessage();
		mUIHandler.sendMessage(message1);

	}
	
	
	class MyMessageListeners implements ChatManagerListener {
		public void chatCreated(Chat chat, boolean value)
		{
			chat.addMessageListener(new MessageListener() {
				
				@Override
				public void processMessage(Chat chat, Message message) 
				{
					/** 发送消息 */
					// chat.sendMessage("dingding……" + message.getBody());
					mChatAdapter.addMessage(1, message.getBody());
					android.os.Message msg = mUIHandler.obtainMessage();
					mUIHandler.sendMessage(msg);
				}
			});
		}
		
    }
}
