package com.findu.demo.activity;

import com.findu.demo.R;
import com.findu.demo.manager.UserManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Administrator
 * 查找好友Activity
 * 1.输入好友ID
 * 2.点击查找，向服务器发送消息 ，消息参数为被查找好友的ID
 *   如果失败，则返回该用户不存在，如果成功，就跳转到下一页面
 * 3.点击取消，退出当前Activity
 */
public class SearchFriendActivity extends Activity {
	
	private final String TAG = SearchFriendActivity.class.getName();
	private EditText mInputFriendID;
	private Button mSearch;
	private Button mCancel;
	private TextView mSearchResult;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_friends_layout);
		initUI();
	}
	
	private void initUI()
	{
		mInputFriendID = (EditText)findViewById(R.id.input_friendID);
		mSearch = (Button)findViewById(R.id.searchFriend);
		mSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String friendID = mInputFriendID.getText().toString();
				requestSearch(friendID);
			}
		});
		mCancel = (Button)findViewById(R.id.cancelSearchFriend);
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchFriendActivity.this.finish();
			}
		});
		
		mSearchResult = (TextView)findViewById(R.id.search_result);
		
	}
	
	private void requestSearch(String friendID)
	{
		Log.v(TAG, "requestSearch " + friendID);
	}
	
	
	
	
	
	
}
