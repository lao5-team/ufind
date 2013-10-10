/**
 * 
 */
package com.findu.demo.activity;

import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.db.Plan;
import com.findu.demo.db.Plan.PlanStateChangeListener;
import com.findu.demo.db.XMLPlanManager;
import com.findu.demo.manager.MapManager;
import com.findu.demo.manager.MiniMapManager;
import com.findu.demo.service.FindUService;
import com.findu.demo.service.MockFindUService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Administrator
 *
 * 1.��ActivityΪ��Activity PASS
 * 2.���Ҫȥ�ģ�����HistoryActivity 
 * 3.���׼��ȥ����ȥXX��·�ϣ�����PlanActivity����ʾXX�ļƻ� 
 * 4.����ʾ��ͼ���һ��رյ�ͼ  PASS
 * 5.���мƻ���ʱ��button��ʾ�������ƣ���ͼ��ʾĿ�ĵ� PASS
 * 6.MyFriendsMain���ñ�Activity��MapView PASS
 */
public class EntranceActivity extends Activity{
	public static String TAG = EntranceActivity.class.getName();
	private Button mNewPlanButton;
	private Button mReadyButton;
	private TextView mReadyTextView;
	private MapView mMapView;
	private GestureDetector mGestureDetector; 
	private MiniMapManager mMapManager;
	private FindUService mService;
	private RelativeLayout mRelativeLayout;
	private Plan mCurReadyPlan = null;
	private Button mSimLocationButton = null;
	private final int PLAN_STATE_CHANGED = 0;
	
	/**
	 * ����mCurReadyPlan��״̬������
	 */
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == PLAN_STATE_CHANGED)
			{
				Plan plan = (Plan)msg.obj;
				switch(msg.arg1)
				{
					case Plan.READY:
			    			mReadyTextView.setText(String.format(EntranceActivity.this.getString(R.string.readytogo), plan.name));
			    			mReadyButton.setText(EntranceActivity.this.getString(R.string.begin));
			    			break;
					case Plan.DOING:
			    			mReadyTextView.setText(String.format(EntranceActivity.this.getString(R.string.ontheway), plan.name));
			    			mReadyButton.setVisibility(View.INVISIBLE);
			    			break;
			    	case Plan.CAN_FINISH:
			    			mReadyButton.setVisibility(View.VISIBLE);
			    			mReadyButton.setText(EntranceActivity.this.getString(R.string.finish));
			    			break;
			    	case Plan.IDLE:
			    	case Plan.TIME_OUT:
			    	case Plan.FINISHED:
			    			mReadyTextView.setVisibility(View.INVISIBLE);
			    			mReadyButton.setVisibility(View.INVISIBLE);
			    			break;
				}
				
			}
		}
	};
	
	private ServiceConnection mConnection = new ServiceConnection() {  
        public void onServiceConnected(ComponentName className,IBinder localBinder) {  
        	Log.d(TAG, className.getClassName());
        	mService = ((FindUService.FindUBinder)localBinder).getService();  
        	//���ӷ���ʱ����ȡ��Ҫ���е�plan
        	mCurReadyPlan = mService.getOnTimePlan();
        	if(null != mCurReadyPlan)
        	{
        		onGetReadyPlan();
        	}
        }  
        public void onServiceDisconnected(ComponentName arg0) {  
        	mService = null;  
        }  
    }; 
    
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("HasPlanReady"))
			{
				mCurReadyPlan = mService.getOnTimePlan();
				if(null != mCurReadyPlan)
				{
					onGetReadyPlan();
				}
			}
		}
    	
    };
    
    /**
     * ���յ���ready plan�Ĺ㲥ʱ��UI�ϵĴ���
     */
    private void onGetReadyPlan()
    {
    	Log.d(TAG, mCurReadyPlan.name);
		
		mReadyTextView.setVisibility(View.VISIBLE);
		mReadyButton.setVisibility(View.VISIBLE);
		mCurReadyPlan.addStateChangeListener(mStateChangedListener);
		mReadyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurReadyPlan.status == Plan.READY)
				{
					IntentFilter filter = new IntentFilter();
					filter.addAction(MapManager.ACTION_RECEIVE_LOCATION);
					EntranceActivity.this.registerReceiver(mCurReadyPlan, filter);
					mCurReadyPlan.begin();
				}
				else if(mCurReadyPlan.status == Plan.CAN_FINISH)
				{
					mCurReadyPlan.finish();
				}
			}
		});
		mMapManager.setDestPoint(new GeoPoint(mCurReadyPlan.destLatitude, mCurReadyPlan.destLongitude));
		mStateChangedListener.onStateChanged(mCurReadyPlan);
    }
	
    private Plan.PlanStateChangeListener mStateChangedListener = new PlanStateChangeListener() {
		
		@Override
		public void onStateChanged(Plan plan) {
			// TODO Auto-generated method stub
    		Message msg = mHandler.obtainMessage();
    		msg.what = PLAN_STATE_CHANGED;
    		msg.arg1 = plan.status;
    		msg.obj = plan;
    		mHandler.sendMessage(msg);
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
		mReadyButton.setVisibility(View.GONE);
		
		mReadyTextView = (TextView)findViewById(R.id.textView_plan_in_time);
		mReadyTextView.setClickable(true);
		mReadyTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(EntranceActivity.this, PlanActivity.class);
				intent.putExtra("type", "Load");
				intent.putExtra("id", mCurReadyPlan.id);
				startActivity(intent);
			}
		});
		mReadyTextView.setVisibility(View.GONE);
		mRelativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
		mMapView = (MapView)findViewById(R.id.bmapView);
		mSimLocationButton = (Button)findViewById(R.id.button_sim_location);
		mSimLocationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction(MapManager.ACTION_RECEIVE_LOCATION);
				intent.putExtra("Lat", 39914035);
				intent.putExtra("Long", 116403094);
				sendBroadcast(intent);
			}
		});
		mMapManager = new MiniMapManager(this, mMapView);
		FriendsApplication.getInstance().mMapManager = mMapManager;
		initGesture();
		bindService(new Intent(EntranceActivity.this, 
		            FindUService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		XMLPlanManager.getInstance();
		IntentFilter filter = new IntentFilter("HasPlanReady");
		registerReceiver(mBroadcastReceiver, filter);
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
		startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if(null != mCurReadyPlan)
		{
			mCurReadyPlan.removeStateChangeListener(mStateChangedListener);
		}
		
	}
	@Override
	protected void onDestroy()
	{
		unbindService(mConnection);
		super.onDestroy();
		//System.exit(0);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(null != mCurReadyPlan)
		{
			mCurReadyPlan.addStateChangeListener(mStateChangedListener);
		}
	}
}
