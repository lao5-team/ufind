/**
 * 
 */
package com.findu.demo.activity;

import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.db.Plan;
import com.findu.demo.db.XMLPlanManager;
import com.findu.demo.manager.MapManager;
import com.findu.demo.service.FindUService;
import com.findu.demo.service.MockFindUService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * @author Administrator
 *
 * 1.该Activity为主Activity PASS
 * 2.点击要去哪，拉起HistoryActivity 
 * 3.点击准备去，在去XX的路上，拉起PlanActivity，显示XX的计划 
 * 4.左滑显示地图，右滑关闭地图  PASS
 * 5.出行计划到时，button显示出行名称，地图显示目的地 PASS
 * 6.MyFriendsMain复用本Activity的MapView PASS
 */
public class EntranceActivity extends Activity{
	public static String TAG = EntranceActivity.class.getName();
	private Button mNewPlanButton;
	private Button mReadyButton;
	private MapView mMapView;
	private GestureDetector mGestureDetector; 
	private MapManager mMapManager;
	private FindUService mService;
	private RelativeLayout mRelativeLayout;
	private ServiceConnection mConnection = new ServiceConnection() {  
        public void onServiceConnected(ComponentName className,IBinder localBinder) {  
        	Log.d(TAG, className.getClassName());
        	mService = ((FindUService.FindUBinder)localBinder).getService();  
    		Plan plan = mService.getOnTimePlan();
    		Log.d(TAG, plan.name);
    		mReadyButton.setVisibility(View.VISIBLE);
    		mReadyButton.setText(String.format(EntranceActivity.this.getString(R.string.readytogo), plan.name));
    		mMapManager.setDestPoint(new GeoPoint(plan.destLatitude, plan.destLongitude));
        }  
        public void onServiceDisconnected(ComponentName arg0) {  
        	mService = null;  
        }  
    }; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entrance_layout);
		mNewPlanButton = (Button)findViewById(R.id.btn_new_plan);
		mNewPlanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EntranceActivity.this, HistoryActivity.class);
				startActivity(intent);
			}
		});
		
		mReadyButton = (Button)findViewById(R.id.btn_plan_in_time);
		mReadyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EntranceActivity.this, PlanActivity.class);
				startActivity(intent);
			}
		});
		mReadyButton.setVisibility(View.GONE);
		mRelativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
		mMapView = (MapView)findViewById(R.id.bmapView);
		mMapManager = new MapManager(this, mMapView);
		FriendsApplication.getInstance().mMapManager = mMapManager;
		initGesture();
		bindService(new Intent(EntranceActivity.this, 
		            MockFindUService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		XMLPlanManager.getInstance();
	}
	
	private void initGesture()
	{
		mGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				Log.d(TAG, "velocityX " + velocityX);
				// TODO Auto-generated method stub
				if(velocityX < -1000)
				{
					mMapView.setVisibility(View.VISIBLE);
				}
				if(velocityX > 1000)
				{
					mMapView.setVisibility(View.INVISIBLE);
				}
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		//mGestureDetector.setIsLongpressEnabled(false);
		mRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onTouch");
				return mGestureDetector.onTouchEvent(event);
			}
		});
		
		mMapView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onTouch");
				return mGestureDetector.onTouchEvent(event);
			}
		});
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		Intent intent = new Intent(this, FindUService.class);  
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
	}
	
}
