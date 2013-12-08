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
import com.findu.demo.constvalue.ConstValue;
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
import android.widget.TextView;

public class PlanActivity extends Activity {
	public static String TAG = PlanActivity.class.getName();
	private Plan mCurPlan = null;
	private EditText mEtxName = null;
	private Button mBtnLocation = null;
	private Button mBtnSelectFriends = null;
	private EditText mEditTime = null;
	private CheckBox mCheckDayly = null;
	private Button mButtonFinish = null;
	private Button mBtnCancel = null;
	private Button mBtnUpdate = null;
	private TextView mTvSelectedFriends = null;
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
		setContentView(R.layout.plan_layout3);
		initUI();
		mCurPlan = new Plan();

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
		
		if(requestCode == ConstValue.INTENT_SET_LOCATION && resultCode == RESULT_OK)
		{
			mCurPlan.destLatitude = data.getIntExtra("latitude", 0);
			mCurPlan.destLongitude = data.getIntExtra("longtitude", 0);
		}
		else if(requestCode == ConstValue.INTENT_SET_FRIENDS && resultCode == RESULT_OK)
		{
			
			mCurPlan.friends = (ArrayList<User>) data.getSerializableExtra(ConstValue.SELECTED_FRIENDS);
			String names = "";
			for(int i=0; i<mCurPlan.friends.size(); i++)
			{
				names += mCurPlan.friends.get(i).mNickname + ",";
			}
			Log.v(TAG, "set Friends " + names);
			mTvSelectedFriends.setText(names);
		}
	}
	
	
	private void initUI()
	{
		mEtxName = (EditText)findViewById(R.id.etx_plan_name);
		mBtnLocation = (Button)findViewById(R.id.btn_set_location);
		mBtnLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 这里的逻辑要处理一下
				Intent intent = new Intent(PlanActivity.this, MyFriendsMain.class);
				startActivityForResult(intent, ConstValue.INTENT_SET_LOCATION);
			}
		});
		mButtonFinish = (Button)findViewById(R.id.btn_finish);
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
		
		mBtnCancel = (Button)findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mBtnSelectFriends = (Button)findViewById(R.id.btn_select_friends);
		mBtnSelectFriends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectFriends();
			}
		});
		
		mTvSelectedFriends = (TextView)findViewById(R.id.tv_selected_friends);
	}
	
	private void upateView()
	{
		if(null!=mCurPlan)
		{
			mEtxName.setText(mCurPlan.name);
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			mEditTime.setText(format.format(mCurPlan.startTime));
			mCheckDayly.setChecked(mCurPlan.isDaylyRemind);
		}
	}
	
	private void updateData()
	{
		mCurPlan.name = mEtxName.getText().toString();
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
	
	private void selectFriends()
	{
		Intent intent = new Intent(PlanActivity.this, ContactsActivity.class);
		intent.putExtra(ConstValue.INTENT_TYPE, ConstValue.INTENT_SET_FRIENDS);
		startActivityForResult(intent, ConstValue.INTENT_SET_FRIENDS);

	}
	

	
	
}
