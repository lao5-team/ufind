package com.findu.demo.activity;
import com.findu.demo.R;
import com.findu.demo.user.UserAction;

import android.app.Activity;
import android.os.Bundle;
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
				// TODO Auto-generated method stub

				UserAction ua = new UserAction();
		        if(ua.sendUserAddRequest(1, username, password, nickName, null))
		        {
		        	Toast.makeText(RegisterActivity.this, "ע��ɹ�", Toast.LENGTH_SHORT).show();
		        }
			}
		})
		{
			
		};
		t.start();

	}
}
