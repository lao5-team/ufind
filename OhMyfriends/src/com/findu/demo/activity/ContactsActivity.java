package com.findu.demo.activity;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import com.findu.demo.R;
import com.findu.demo.adapter.ContactsAdapter;
import com.findu.demo.constvalue.ConstValue;
import com.findu.demo.manager.UserManager;
import com.findu.demo.user.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends Activity {
	public static final String TAG = ContactsActivity.class.getName();

	private Button mSearchFriendButton;
	private Button mStartMeetingButton;
	private Button mFinishButton;
	private Button mCancelButton;
	private TextView mSelectFriends;
	private ListView mFriendsListView;
	private ContactsAdapter mAdapter;
	/**
	 * ������ 
	 * TYPE_NORMAL ��ͨģʽ��������Ӻ��ѣ����Է���ۻ�
	 * TYPE_SET_FRIENDS �ۻ�����Ӻ���ģʽ
	 */
	private int mType = 0;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mType = this.getIntent().getIntExtra(ConstValue.INTENT_TYPE, 0);
		mAdapter = new ContactsAdapter(this);
		
		setContentView(R.layout.contacts_layout);
		initUI();
	}
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event) {  
	   // �Ƿ񴥷�����Ϊback��  
	   Log.v("ContactsActivity", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	       // �����˳�ȷ�Ͽ�  
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
	 * ��ʼ��UI
	 * 
	 */
	private void initUI()
	{
		mSelectFriends = (TextView)findViewById(R.id.selectFriends);
		mSelectFriends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startMeeting();
				
			}
		});
		mSearchFriendButton = (Button)findViewById(R.id.addFriend);
		mSearchFriendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��Ӻ���
				
				
			}
		});
		mStartMeetingButton = (Button)findViewById(R.id.startMeeting);
		mStartMeetingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startMeeting();
			}
		});
		mFinishButton = (Button)findViewById(R.id.finish_set_friends);
		mFinishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishSelectFriends();
			}
		});
		mCancelButton = (Button)findViewById(R.id.cancel_set_friends);
		mCancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancelMeeting();
			}
		});
		
		mFriendsListView = (ListView)findViewById(R.id.friends);
		mFriendsListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(mType == ConstValue.INTENT_NORMAL)
		{
			mSearchFriendButton.setVisibility(View.VISIBLE);
			mStartMeetingButton.setVisibility(View.VISIBLE);
			mFinishButton.setVisibility(View.GONE);
			mCancelButton.setVisibility(View.GONE);
		}
		else if(mType == ConstValue.INTENT_SET_FRIENDS)
		{
			mSearchFriendButton.setVisibility(View.GONE);
			mStartMeetingButton.setVisibility(View.GONE);
			mFinishButton.setVisibility(View.VISIBLE);
			mCancelButton.setVisibility(View.VISIBLE);
		}
		UserManager.getInstance().loadFriends();
		mAdapter.setFriends(UserManager.getInstance().getFriends());
	}
	
	/**
	 * 1.������Ѳ��ҽ���
	 * 2.�������ID���Һ���
	 */
	public void searchFriends()
	{
		
	}
	
	private void startMeeting()
	{
		Intent intent = new Intent(this, PlanActivity.class);
		startActivity(intent);
	}
	
	private void finishSelectFriends()
	{
		Log.v(TAG, "finishSelectFriends");
		Intent intent = new Intent();
		intent.putExtra(ConstValue.SELECTED_FRIENDS, mAdapter.getSelectedUsers());
		setResult(RESULT_OK, intent);
		finish();

	}
	
	private void cancelMeeting()
	{
		mSelectFriends.setVisibility(View.GONE);
		mFinishButton.setVisibility(View.GONE);
		mCancelButton.setVisibility(View.GONE);
	}
	
}
