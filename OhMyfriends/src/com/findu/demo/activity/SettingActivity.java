package com.findu.demo.activity;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;

public class SettingActivity extends Activity {

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
}


