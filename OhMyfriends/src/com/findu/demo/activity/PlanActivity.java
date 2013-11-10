package com.findu.demo.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.db.Plan;
import com.findu.demo.db.XMLPlanManager;
import com.findu.demo.manager.MapManager;
import com.findu.demo.service.FindUService;
import com.findu.demo.user.User;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class PlanActivity extends Activity {
	public static String TAG = PlanActivity.class.getName();
	private Plan mCurPlan = null;
	private EditText mEditName = null;
	private Button mButtonLocation = null;
	private Button mButtonFriends = null;
	private EditText mEditTime = null;
	private CheckBox mCheckDayly = null;
	private Button mButtonFinish = null;
	private Button mButtonCancel = null;
	private Button mButtonUpdate = null;
	private FindUService mService = null;
	private final int REQ_SET_LOCATION = 0;
	private final int REQ_SET_FRIENDS = 1;
	private final int RESULT_OK = 0;
	private final int RESULT_FAIL = 1;
	private ServiceConnection mConnection = new ServiceConnection() {  
        public void onServiceConnected(ComponentName className,IBinder localBinder) {  
        	Log.d(TAG, className.getClassName());
        	mService = ((FindUService.FindUBinder)localBinder).getService();  
        }  
        public void onServiceDisconnected(ComponentName arg0) {  
        	mService = null;  
        }  
    }; 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_layout);
		initUI();

	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unbindService(mConnection);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQ_SET_LOCATION && resultCode == RESULT_OK)
		{
			mCurPlan.destLatitude = data.getIntExtra("latitude", 0);
			mCurPlan.destLongitude = data.getIntExtra("longtitude", 0);
		}
		else if(requestCode == REQ_SET_FRIENDS && resultCode == RESULT_OK)
		{
			mCurPlan.friends = (ArrayList<User>) data.getSerializableExtra("friends");
		}
	}
	
	
	private void initUI()
	{
		mEditName = (EditText)findViewById(R.id.plan_name);
		mButtonLocation = (Button)findViewById(R.id.set_location);
		mButtonLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 这里的逻辑要处理一下
				Intent intent = new Intent(PlanActivity.this, MyFriendsMain.class);
				startActivityForResult(intent, REQ_SET_LOCATION);
			}
		});
		mEditTime = (EditText)findViewById(R.id.editText_time);
		mButtonFinish = (Button)findViewById(R.id.button_finish);
		mButtonFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//updateData();
				//mService.addPlanToDo(mCurPlan);
				XMLPlanManager.getInstance().addPlan(mCurPlan);
				sendPlanRequest(mCurPlan);
				finish();
			}
		});
		
		mButtonCancel = (Button)findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mButtonFriends = (Button)findViewById(R.id.call_friends);
		mButtonFriends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlanActivity.this, ContactsActivity.class);
				startActivityForResult(intent, REQ_SET_FRIENDS);
			}
		});
	}
	
	private void upateView()
	{
		if(null!=mCurPlan)
		{
			mEditName.setText(mCurPlan.name);
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			mEditTime.setText(format.format(mCurPlan.startTime));
			mCheckDayly.setChecked(mCurPlan.isDaylyRemind);
		}
	}
	
	private void updateData()
	{
		mCurPlan.name = mEditName.getText().toString();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			mCurPlan.startTime = format.parse(mEditTime.getText().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCurPlan.destLatitude = 39914935;
		mCurPlan.destLongitude = 116403694;
		mCurPlan.isDaylyRemind = mCheckDayly.isChecked();
		
	}
	
	/**
	 * 
	 */
	private void sendPlanRequest(Plan plan)
	{
		
	}
	
	
}
