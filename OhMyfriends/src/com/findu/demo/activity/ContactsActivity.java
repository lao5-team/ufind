package com.findu.demo.activity;

import com.findu.demo.R;
import com.findu.demo.adapter.ContactsAdapter;
import com.findu.demo.manager.UserManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ContactsActivity extends Activity {
		
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_SET_FRIENDS = 1;
	private Button mSearchFriendButton;
	private Button mStartMeetingButton;
	private Button mFinishButton;
	private Button mCancelButton;
	private ListView mFriendsListView;
	private ContactsAdapter mAdapter;
	/**
	 * 打开类型 
	 * TYPE_NORMAL 普通模式，可以添加好友，可以发起聚会
	 * TYPE_SET_FRIENDS 聚会中添加好友模式
	 */
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
	
	public int getType()
	{
		return mType;
	}
	
	/**
	 * 初始化UI
	 * 
	 */
	private void initUI()
	{
		mSearchFriendButton = (Button)findViewById(R.id.addFriend);
		mSearchFriendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加好友
				
				
			}
		});
		mStartMeetingButton = (Button)findViewById(R.id.startMeeting);
		mStartMeetingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		mFinishButton = (Button)findViewById(R.id.finish_set_friends);
		mFinishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("selected friends", mAdapter.getSelectedUsers());
				ContactsActivity.this.setResult(RESULT_OK, intent);
			}
		});
		mCancelButton = (Button)findViewById(R.id.cancel_set_friends);
		mCancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ContactsActivity.this.finish();
			}
		});
		
		mFriendsListView = (ListView)findViewById(R.id.friends);
		mFriendsListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onResume()
	{
		if(mType == TYPE_NORMAL)
		{
			mSearchFriendButton.setVisibility(View.VISIBLE);
			mStartMeetingButton.setVisibility(View.VISIBLE);
			mFinishButton.setVisibility(View.GONE);
			mCancelButton.setVisibility(View.GONE);
		}
		else if(mType == TYPE_SET_FRIENDS)
		{
			mSearchFriendButton.setVisibility(View.GONE);
			mStartMeetingButton.setVisibility(View.GONE);
			mFinishButton.setVisibility(View.VISIBLE);
			mCancelButton.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 1.拉起好友查找界面
	 * 2.输入好友ID查找好友
	 */
	public void searchFriends()
	{
		
	}
	
}
