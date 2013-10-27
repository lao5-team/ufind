package com.findu.demo.activity;

import com.findu.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class ContactsActivity extends Activity {
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_layout);
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
}
