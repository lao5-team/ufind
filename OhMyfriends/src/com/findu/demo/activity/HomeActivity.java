package com.findu.demo.activity;

import com.findu.demo.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TabHost;

public class HomeActivity extends TabActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();
        
        tabHost.addTab(tabHost.newTabSpec("tab0")
                .setIndicator(this.getString(R.string.meet))
                .setContent(new Intent(this, MeetListActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(this.getString(R.string.contacts))
                .setContent(new Intent(this, ContactsActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(this.getString(R.string.map))
                .setContent(new Intent(this, MyFriendsMain.class)));
        
        // This tab sets the intent flag so that it is recreated each time
        // the tab is clicked.
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(this.getString(R.string.setting))
                .setContent(new Intent(this, UserinfoActivity.class)));
                        //.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event) {  
	   // 是否触发按键为back键  
	   Log.v("HomeActivity", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	       // 弹出退出确认框  
	        //finish(); 
	        System.exit(0);
	        return true;  
	    }
	   return false;
	}
}
