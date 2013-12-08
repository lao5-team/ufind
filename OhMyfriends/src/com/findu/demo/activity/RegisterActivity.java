package com.findu.demo.activity;
import com.findu.demo.R;
import com.findu.demo.manager.LoginManager;
import com.findu.demo.user.User;
import com.findu.demo.user.UserAction;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterActivity extends Activity {
	private EditText mUserName;
	private EditText mNickName;
	private EditText mPassword;
	private Button mCommit;
	private static final int FALSE = -1;
	private static final int SUCCESSED = 0;
	private Handler mUIHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == SUCCESSED)
			{
				Toast.makeText(RegisterActivity.this, "×¢²á³É¹¦", Toast.LENGTH_SHORT).show();
				finish();
			}
			else
			{
				Toast.makeText(RegisterActivity.this, "×¢²áÊ§°Ü", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initUI();

	}
	
	private void initUI()
	{
		mUserName = (EditText)this.findViewById(R.id.inputUserName);
		mNickName = (EditText)this.findViewById(R.id.inputNickname);
		mPassword = (EditText)this.findViewById(R.id.inputPassword);
		mCommit = (Button)this.findViewById(R.id.commit);
		mCommit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userName = mUserName.getEditableText().toString();
				String nickNameStr = mNickName.getEditableText().toString();
				String passwordStr = mPassword.getEditableText().toString();
				register(userName, nickNameStr, passwordStr);
			}
		});
	}
	
	private void register(final String username, final String nickName, final String password)
	{

		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				User user = new User();
				user.mUsername = username;
				user.mPassword = password;
				boolean result = LoginManager.getInstance().register(user);
				Message msg = mUIHandler.obtainMessage();
				if(result == true)
				{
					msg.what = SUCCESSED;
				}
				else
				{
					msg.what = FALSE;
				}
				mUIHandler.sendMessage(msg);
			}
		});
		t.start();
//		Thread t = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				UserAction ua = new UserAction();
//		        if(ua.sendUserAddRequest(1, username, password, nickName, null))
//		        {
//		        	Toast.makeText(RegisterActivity.this, "×¢²á³É¹¦", Toast.LENGTH_SHORT).show();
//		        }
//			}
//		})
//		{
//			
//		};
//		t.start();
		

	}
}
