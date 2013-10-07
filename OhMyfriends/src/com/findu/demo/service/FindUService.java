package com.findu.demo.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.findu.demo.R;
import com.findu.demo.activity.EntranceActivity;
import com.findu.demo.db.Plan;
import com.findu.demo.db.Plan.PlanStateChangeListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Administrator
 * 获取到时的出行plan
 */
public class FindUService extends Service {
	public static String TAG = FindUService.class.getName();
	public Plan mPlanToDo = null;
	public class FindUBinder extends Binder
	{
		public FindUService getService()
		{
			return FindUService.this;
		}
	}
	
	private FindUBinder mBinder = new FindUBinder();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}
	
	public Plan getOnTimePlan()
	{
		return mPlanToDo;
	}
	
	/**
	 * @param plan
	 */
	public void addPlanToDo(final Plan plan)
	{
		plan.countDown();
		plan.addStateChangeListener(new PlanStateChangeListener() {
			
			@Override
			public void onStateChanged(Plan plan) {
				// TODO Auto-generated method stub
				if(plan.status == Plan.READY)
				{
					Log.d(TAG, plan.name + " isReady");
					mPlanToDo = plan;
					Intent intent = new Intent("HasPlanReady");
					sendBroadcast(intent);
					showNotification(plan);
				}
				else if(plan.status == Plan.CAN_FINISH)
				{
					showNotification(plan);
				}
				else if(plan.status == Plan.FINISHED || plan.status == Plan.TIME_OUT)
				{
					if(mPlanToDo.id == plan.id)
					{
						mPlanToDo = null;
					}
					showNotification(plan);
				}
			}
		});
	}
	
	private void showNotification(Plan plan)
	{
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		String tickerText = "";
		if(plan.status == Plan.READY)
		{
			tickerText = "准备去" + plan.name + "了";
		}
		else if(plan.status == Plan.CAN_FINISH)		
		{
			tickerText = "已经到" + plan.name + "附近了";
		}
		else if(plan.status == Plan.TIME_OUT)
		{
			tickerText = plan.name + "超时了";
		}
		else
		{
			return ;
		}
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.ic_launcher, tickerText, when);
		Intent notificationIntent = new Intent(this, EntranceActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, tickerText, tickerText, contentIntent);
		mNotificationManager.notify(0, notification);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.v(TAG, "onDestroy");
	}

}
