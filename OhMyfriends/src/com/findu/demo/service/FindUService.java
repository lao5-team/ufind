package com.findu.demo.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.findu.demo.db.Plan;

import android.app.Service;
import android.content.Intent;
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
		Plan plan = new Plan();
		plan.name = "上班";
		plan.destLatitude = (int)(39.915267 * 1E6);
		plan.destLongitude = (int)(116.403909 * 1E6);
		return plan;
	}
	
	public void setPlanToDo(final Plan plan)
	{
		Date curDate = new Date(System.currentTimeMillis());
		int currentTime = curDate.getHours() * 60 + curDate.getMinutes();
		Date beginDate = plan.startTime;
		int beginTime = beginDate.getHours() * 60 + beginDate.getMinutes();
		if(beginTime > currentTime)
		{
			Timer timer = new Timer();       
			timer.schedule(new TimerTask() {           
			            @Override
			            public void run() {
			            	if(mPlanToDo != plan)
			            	{
			            		mPlanToDo = plan;
			            	}
			            	else
			            	{
			            		mPlanToDo = null;
			            	}
			            	
			            }
			        }, (beginTime - currentTime)*60*1000, 60*1000); 
		}
		
	}

}
