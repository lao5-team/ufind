package com.findu.demo.activity;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;

public class SettingActivity extends Activity {

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
}


