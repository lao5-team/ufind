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
	private EditText mEditTime = null;
	private CheckBox mCheckDayly = null;
	private Button mButtonCreate = null;
	private Button mButtonCancel = null;
	private Button mButtonUpdate = null;
	private FindUService mService = null;
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
		initView();
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if(type.equals("Create"))
		{
			mCurPlan = new Plan();
		}
		else if(type.equals("Load"))
		{
			int id = intent.getIntExtra("id", -1);
			if(-1 != id)
			{
				ArrayList<Plan> plans = XMLPlanManager.getInstance().getPlans();
				for(Plan p:plans)
				{
					if(p.id == id)
					{
						mCurPlan = (Plan)p.clone();
						break;
					}
				}
			}
			upateView();
		}

		bindService(new Intent(PlanActivity.this, 
	            FindUService.class), mConnection, Context.BIND_AUTO_CREATE);

	}
	
	private void initView()
	{
		mEditName = (EditText)findViewById(R.id.editText_name);
		mButtonLocation = (Button)findViewById(R.id.button_set_location);
		mButtonLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PlanActivity.this, MyFriendsMain.class);
				startActivity(intent);
			}
		});
		mEditTime = (EditText)findViewById(R.id.editText_time);
		mCheckDayly = (CheckBox)findViewById(R.id.checkBox_dayly_remind);
		mButtonCreate = (Button)findViewById(R.id.button_finish);
		mButtonCreate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateData();
				mService.addPlanToDo(mCurPlan);
				XMLPlanManager.getInstance().addPlan(mCurPlan);
				finish();
			}
		});
		
		mButtonUpdate = (Button)findViewById(R.id.button_update);
		mButtonUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateData();
				mService.addPlanToDo(mCurPlan);
				XMLPlanManager.getInstance().update(mCurPlan);
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
	
	
}
