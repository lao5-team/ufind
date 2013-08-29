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
import android.util.Log;

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
	public static final String COLUME_LATITUDE = "latitude";
	public static final String COLUME_LONGITUDE = "longitude";

	public LocusDbManager(MyFriendsMain main, MapView mapview,
			CustomGraphicsOverlay graphicsOverlay) {
		mContextFriendsMain = main;
		mLocusResolver = main.getContentResolver();
		mMapView = mapview;
		mGraphicsOverlay = graphicsOverlay;

		// 到底系统时间该用何种方式写呢？
		long time = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(time);// 获取当前时间
		String str = formatter.format(curDate);
		mToday = dateStringToLong(str);
		formatter = new SimpleDateFormat(" HH:mm:ss");
		str = formatter.format(curDate);
		mStartTime = time;
	}

	private long dateStringToLong(String date) {
		String[] spString = date.split("-");
		long day = Integer.valueOf(spString[0]) * 10000 // 年份
				+ Integer.valueOf(spString[1]) * 100 // 月份
				+ Integer.valueOf(spString[2]); // 日
		return day;
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
	
	public Cursor getHistoryLocus() {
		
		long time = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(time);// 获取当前时间
		String str = formatter.format(curDate);
		long today = dateStringToLong(str);
		String whereClause = COLUME_USER + " > 0 " + " AND " + COLUME_DATE
				+ " < " + today;
		final String orderBy = COLUME_DATE + " DESC";
		Cursor cursor = mLocusResolver.query(LocusProvider.FIND_URI,
				LocusProvider.SUGGEST_PROJECTION, whereClause, null, orderBy);
		return cursor;
	}
	public Cursor getTodayLocus(long today) {
		String whereClause = COLUME_USER + " > 0 " + " AND " + COLUME_DATE
				+ " > 0" + " AND " + COLUME_DATE + " = " + today;
		final String orderBy = COLUME_DATE + " DESC";
		Cursor cursor = mLocusResolver.query(LocusProvider.FIND_URI,
				LocusProvider.SUGGEST_PROJECTION, whereClause, null, orderBy);
		return cursor;
	}
}
