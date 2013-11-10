package com.findu.demo.activity;

import com.findu.demo.R;
import com.findu.demo.adapter.ContactsAdapter;
import com.findu.demo.manager.UserManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;

public class ContactsActivity extends Activity {
		
	public final int TYPE_NORMAL = 0;
	public final int TYPE_SET_FRIENDS = 1;
	private Button mAddFriendButton;
	private Button mStartMeetingButton;
	private ListView mFriendsListView;
	private ContactsAdapter mAdapter;
	private int mType = 0;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mType = this.getIntent().getIntExtra("type", 0);
		mAdapter = new ContactsAdapter(this);
		mAdapter.setFriends(UserManager.getInstance().getFriends());
		setContentView(R.layout.contacts_layout);
		initUI();
		
	}
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event) {  
	   // 是否触发按键为back键  
	   Log.v("ContactsActivity", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	       // 弹出退出确认框  
	        //finish(); 
	        System.exit(10);
	        return true;  
	    }
	   return false;
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI()
	{
		if(mType == TYPE_NORMAL)
		{
			mAddFriendButton = (Button)findViewById(R.id.addFriend);
			mStartMeetingButton = (Button)findViewById(R.id.startMeeting);
			mFriendsListView = (ListView)findViewById(R.id.friends);
			mFriendsListView.setAdapter(mAdapter);
		}
		else if(mType == TYPE_SET_FRIENDS)
		{
			
		}

	}
}
