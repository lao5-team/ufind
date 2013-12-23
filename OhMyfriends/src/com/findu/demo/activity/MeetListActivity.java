package com.findu.demo.activity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import com.findu.demo.R;
import com.findu.demo.adapter.PlansAdapter;
import com.findu.demo.constvalue.ConstValue;
import com.findu.demo.db.Plan;
import com.findu.demo.db.XMLPlanManager;
import com.findu.demo.manager.UserManager;
import com.findu.demo.user.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Administrator
 * 聚会列表Activity
 */
public class MeetListActivity extends Activity {
//	TextView tv = null;
//	EditText et = null;
//	Button bt = null;
//	ChatManager chatManager;
//	Chat chat;
//	String chatContent = "";
//	
//	Handler mUIHandler = new Handler()
//	{
//		@Override
//		public void handleMessage(android.os.Message msg)
//		{
//			tv.setText(chatContent);
//			tv.invalidate();
//		}
//		
//	};
	private ListView mlvwPlans = null;
	private PlansAdapter mPlanAdapter = null;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_list);
		mPlanAdapter = new PlansAdapter(this);
//		chatManager = FriendsApplication.getInstance().getConnection().getChatManager();
//		chatManager.addChatListener(new MyMessageListeners());
//		Presence presence = new Presence(Presence.Type.available);
//	    presence.setStatus("Q我吧");
//	    FriendsApplication.getInstance().getConnection().sendPacket(presence);
//	    chat = chatManager.createChat("byyf3@" + FriendsApplication.getInstance().getConnection().getServiceName(), null);
	    initUI();
	    test();
	}
	
	private void initUI()
	{
		mlvwPlans = (ListView)findViewById(R.id.lvw_plans);
		mlvwPlans.setAdapter(mPlanAdapter);
		mlvwPlans.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
					Intent intent = new Intent(MeetListActivity.this, MeetActivity.class);
					intent.putExtra(ConstValue.OPEN_PLAN, (Plan)mPlanAdapter.getItem(arg2));
					MeetListActivity.this.startActivity(intent);
			}
		});
//		tv = (TextView)this.findViewById(R.id.tv_selected_friends);
//		et = (EditText)this.findViewById(R.id.etx_meet_time);
//		bt = (Button)this.findViewById(R.id.btn_travel_plan);
//		bt.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				User user = new User();
//				user.mUsername = "byyf3";
//				
//				sendMessage(user, et.getEditableText().toString());
//				et.setText("");
//			}
//		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mPlanAdapter.notifyDataSetChanged();
	}
	
//	private void sendMessage(User user, String value)
//	{
//		  try {
//	            /** 发送消息 */
//	            /** 用message对象发送消息 */
//	            Message message = new Message();
//	            message.setBody(value);
//	            message.setProperty("color", "red");
//	            chat.sendMessage(message);
//	            chatContent+= value + "\n";
//	            Log.v("MeetListActivity", "test");
//	            android.os.Message message1 = mUIHandler.obtainMessage();
//	            mUIHandler.sendMessage(message1);
//	        } catch (XMPPException e) {
//	            e.printStackTrace();
//	        }
//
//	}
//	
//	
//	class MyMessageListeners implements ChatManagerListener {
//		public void chatCreated(Chat chat, boolean value)
//		{
//			chat.addMessageListener(new MessageListener() {
//				
//				@Override
//				public void processMessage(Chat chat, Message message) {
//			                /** 发送消息 */
//			                //chat.sendMessage("dingding……" + message.getBody());
//						 chatContent += message.getBody() + "\n";
//						 android.os.Message msg = mUIHandler.obtainMessage();
//						 mUIHandler.sendMessage(msg);
//				}
//			});
//		}
//		
//    }
	private void test()
	{
		Plan plan = new Plan();
		plan.name = "Test";
		XMLPlanManager.getInstance().addPlan(plan);
	}
	
}
