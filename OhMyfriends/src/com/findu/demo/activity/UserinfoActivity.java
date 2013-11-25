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
	   // �Ƿ񴥷�����Ϊback��  
	   Log.v("SettingActivity", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	       // �����˳�ȷ�Ͽ�  
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


