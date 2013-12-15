package com.findu.demo.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import com.findu.demo.R;
import com.findu.demo.adapter.ContactsAdapter;
import com.findu.demo.constvalue.ConstValue;
import com.findu.demo.manager.UserManager;
import com.findu.demo.manager.UserManager.ContactsChangerListener;
import com.findu.demo.user.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	private Button mAddFriendButton;
	/**
	 * 打开类型 
	 * TYPE_NORMAL 普通模式，可以添加好友，可以发起聚会
	 * TYPE_SET_FRIENDS 聚会中添加好友模式
	 */
	private int mType = 0;
	
	private Handler mUIHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			mAdapter.notifyDataSetChanged();
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mType = this.getIntent().getIntExtra(ConstValue.INTENT_TYPE, 0);
		mAdapter = new ContactsAdapter(this);
		
		setContentView(R.layout.contacts_layout);
		initUI();
		UserManager.getInstance().addContactsChangeListener(new ContactsChangerListener() {
			
			@Override
			public void onContactsChange(List<User> users) {
				// TODO Auto-generated method stub
				mUIHandler.sendMessage(new Message());
			}
		});
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
				//添加好友
				
				
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
		mFriendsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				User user = UserManager.getInstance().getFriends().get(arg2);
				Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
				
				intent.putExtra("username", user.mFindUID);
				ContactsActivity.this.startActivity(intent);
			}
		});
		
		mAddFriendButton = (Button)findViewById(R.id.addFriend);
		mAddFriendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAddFriendDialog();
			}
		});
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
	 * 1.拉起好友查找界面
	 * 2.输入好友ID查找好友
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
	
	private void showAddFriendDialog()
	{	
		View view = this.getLayoutInflater().inflate(R.layout.add_friend_layout, null);
		final EditText etxUserName = (EditText)view.findViewById(R.id.etx_friends_id);
		final EditText etxNickName = (EditText)view.findViewById(R.id.etx_friends_nickname);
		new AlertDialog.Builder(this).setView(view).setPositiveButton(getString(R.string.finish), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				User user = new User();
				user.mFindUID = etxUserName.getText().toString();
				user.mNickname = etxNickName.getText().toString();
				UserManager.getInstance().addFriend(user);
			}

		}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).create().show();
	}
	
}
