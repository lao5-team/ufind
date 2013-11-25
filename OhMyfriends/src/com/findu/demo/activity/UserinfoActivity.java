package com.findu.demo.activity;

import com.findu.demo.R;
import com.findu.demo.manager.UserManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class UserinfoActivity extends Activity {

	//nickName
	//uFind_ID
	private TextView mNickName;
	private TextView mUFindID;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initUI();
	}
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event) {  
	   // 是否触发按键为back键  
	   Log.v("SettingActivity", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	       // 弹出退出确认框  
	        //finish(); 
	        System.exit(0);
	        return true;  
	    }
	   return false;
	}
	
	private void initUI()
	{
		this.setContentView(R.layout.setting_layout);
		mNickName = (TextView) this.findViewById(R.id.nickname);
		mUFindID = (TextView)this.findViewById(R.id.uFind_ID);
		mUFindID.setText(UserManager.getInstance().getCurrentUser().mFindUID);
	}
}


