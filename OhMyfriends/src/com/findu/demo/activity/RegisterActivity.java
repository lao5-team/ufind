package com.findu.demo.activity;
import com.findu.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class RegisterActivity extends Activity {
	private TextView mUserName;
	private TextView mNickName;
	private TextView mPassword;
	private Button mCommit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initUI();

	}
	
	private void initUI()
	{
		mUserName = (TextView)this.findViewById(R.id.username);
		mNickName = (TextView)this.findViewById(R.id.nickname);
		mPassword = (TextView)this.findViewById(R.id.password);
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
	
	private void register(String email, String nickName, String password)
	{
		
	}
}
