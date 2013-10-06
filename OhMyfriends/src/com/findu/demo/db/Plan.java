/**
 * 
 */
package com.findu.demo.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.ls.LSInput;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.activity.MyFriendsMain;
import com.findu.demo.activity.RouteActivity;
import com.findu.demo.manager.MapManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Administrator
 * ����������Ϣ:
 * 0.id
 * 1.����
 * 2.Ŀ�ĵ�
 * 3.���п�ʼʱ��
 * 4.�Ƿ�ÿ������
 * 5.�����б� �����ʱ���
 * 6.���ʱ��
 */
public class Plan extends BroadcastReceiver{
	public static int IDLE = 0;
	public static int READY = 1;
	public static int DOING = 2;
	public static int CAN_FINISH = 3;
	public static int FINISHED = 4;
	public static int TIME_OUT = 5;
	public int id;
	public String name;
	public int destLatitude;
	public int destLongitude;
	public Date startTime;
	public boolean isDaylyRemind;
	public ArrayList friends;
	public int duration;
	public int status; 
	public Timer timer = null;
	public ArrayList<PlanStateChangeListener> listeners;
	public static final int EPSILON = 1000;
	
	public static interface PlanStateChangeListener
	{
		public void onStateChanged(Plan plan);
	}
	
	public Plan()
	{
		name = "";
		destLatitude = 0;
		destLongitude = 0;
		status = IDLE;
		listeners = new ArrayList<Plan.PlanStateChangeListener>();
		isDaylyRemind = false;
		duration = 0;
		
	}
	
	public void addStateChangeListener(PlanStateChangeListener listener)
	{
		if(null != listener)
		{
			listeners.add(listener);
		}
	}
	
	public void removeStateChangeListener(PlanStateChangeListener listener)
	{
		if(null != listener)
		{
			listeners.remove(listener);
		}
	}
	
	public void countDown()
	{
		if(status == IDLE && timer==null)
		{
			Date curDate = new Date(System.currentTimeMillis());
			int currentTime = curDate.getHours() * 60 + curDate.getMinutes();
			Date beginDate = startTime;
			int beginTime = beginDate.getHours() * 60 + beginDate.getMinutes();
			if(beginTime > currentTime)
			{
				
				timer = new Timer();       
				timer.schedule(new TimerTask() {           
				            @Override
				            public void run() {
				            	if(status == IDLE)
				            	{
				            		status = Plan.READY;
				            		notifyStateChanged();
				            	}
				            	else if(status == Plan.READY)
				            	{
				            		status = Plan.TIME_OUT;
				            		notifyStateChanged();
				            	}
				            }
				        }, (beginTime - currentTime)*1*1000, 6*1000*5); 
			}
		}
		
	}
	
	public void begin()
	{
		if(status == READY)
		{
			status = DOING;
			notifyStateChanged();
		}
	}
	
	public void finish()
	{
		if(status == CAN_FINISH)
		{
			status = FINISHED;
			notifyStateChanged();
		}
	}
	
	private void notifyStateChanged()
	{
		Log.d("Plan", "status " + status);
		for(PlanStateChangeListener listener : listeners)
		{
			listener.onStateChanged(this);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(MapManager.ACTION_RECEIVE_LOCATION))
		{
			GeoPoint point = new GeoPoint(intent.getIntExtra("Lat", 0), intent.getIntExtra("Long", 0));
			if(status == DOING && Math.abs(destLatitude - point.getLatitudeE6())<1000 && Math.abs(destLongitude - point.getLongitudeE6())<1000)
			{
				status = CAN_FINISH;
				notifyStateChanged();
			}
		}
	}
	
}
