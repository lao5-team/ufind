package com.findu.demo.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.baidu.mapapi.map.MapView;
import com.findu.demo.MyFriendsMain;
import com.findu.demo.overlay.CustomGraphicsOverlay;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class LocusDbManager {

	private ContentResolver mLocusResolver;
	private MyFriendsMain mContextFriendsMain;
	private MapView mMapView;
	private CustomGraphicsOverlay mGraphicsOverlay;
	
	private long mStartTime;
	private String mUser = "renlikun";
	private long mToday;
	private static final String COLUME_USER = "user";
	private static final String COLUME_DATE = "date";
	private static final String COLUME_STARTTIME = "starttime";
	private static final String COLUME_CURRENTTIME = "currenttime";
	private static final String COLUME_LATITUDE = "latitude";
	private static final String COLUME_LONGITUDE = "longitude";

	public LocusDbManager(MyFriendsMain main, MapView mapview, CustomGraphicsOverlay graphicsOverlay) {
		mContextFriendsMain = main;
		mLocusResolver = main.getContentResolver();
		mMapView = mapview;
		mGraphicsOverlay = graphicsOverlay;

		// 到底系统时间该用何种方式写呢？
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		formatter = new SimpleDateFormat("HH:mm:ss");
		str = formatter.format(curDate);

		mToday = System.currentTimeMillis();
		mStartTime = mToday;
	}

	public void setStartTime(long start) {
		mStartTime = start;
	}

	public void setUser(String user) {
		mUser = user;
	}

	public void insertLocusDb(int latitude, int longitude) {
		ContentValues values = new ContentValues();
		values.put(COLUME_USER, mUser);
		values.put(COLUME_DATE, mToday);
		values.put(COLUME_STARTTIME, mStartTime);
		values.put(COLUME_CURRENTTIME, System.currentTimeMillis());
		values.put(COLUME_LATITUDE, latitude);
		values.put(COLUME_LONGITUDE, longitude);

//		Cursor cursor = mLocusResolver.query(LocusProvider.FIND_URI,
//				LocusProvider.SUGGEST_PROJECTION, null, null, null);

		mLocusResolver.insert(LocusProvider.FIND_URI, values);
	}
	
	public void readLocusDb(){
		
	}
}
