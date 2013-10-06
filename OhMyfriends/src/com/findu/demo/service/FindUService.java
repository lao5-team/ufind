package com.findu.demo.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.findu.demo.db.Plan;
import com.findu.demo.db.Plan.PlanStateChangeListener;

import android.app.Service;
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
	private Object mSyncObj = new Object();
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
				}
				else if(plan.status == Plan.FINISHED || plan.status == Plan.TIME_OUT)
				{
					if(mPlanToDo.id == plan.id)
					{
						mPlanToDo = null;
					}
				}
			}
		});
	}

}
