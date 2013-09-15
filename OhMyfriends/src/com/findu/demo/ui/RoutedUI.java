package com.findu.demo.ui;

import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.MyFriendsMain;
import com.findu.demo.db.LocusDbManager;
import com.findu.demo.overlay.CustomGraphicsOverlay;

import android.R.integer;
import android.database.Cursor;

public class RoutedUI {

	Cursor mCursor;
	LocusDbManager mLocusManager;
	GeoPoint[] mRoutePoints = new GeoPoint[1024];
	int mCurrentReadPoint = 0;
	int mDrawingIndex = 0;
	
	public RoutedUI(MyFriendsMain main, MapView mapview,
			CustomGraphicsOverlay graphicsOverlay){
		mLocusManager = new LocusDbManager(main, mapview, graphicsOverlay);
	}
	
	public void readRouteDb(){
		mCursor = mLocusManager.getHistoryLocus();
		boolean ret = mCursor.moveToFirst();
		if(!ret)
		{
			return;
		}
		int lattingdex = mCursor.getColumnIndex(LocusDbManager.COLUME_LATITUDE);
		long latitude = mCursor.getLong(lattingdex);
		int longindex = mCursor.getColumnIndex(LocusDbManager.COLUME_LONGITUDE);
		long longtitude = mCursor.getLong(longindex);
		
		mRoutePoints[mCurrentReadPoint] = new GeoPoint((int)latitude, (int)longtitude);
		
		while (mCursor.moveToNext()) {
			lattingdex = mCursor.getColumnIndex(LocusDbManager.COLUME_LATITUDE);
			latitude = mCursor.getLong(lattingdex);
			longindex = mCursor.getColumnIndex(LocusDbManager.COLUME_LONGITUDE);
			longtitude = mCursor.getLong(longindex);
			
			mCurrentReadPoint++;
			mRoutePoints[mCurrentReadPoint] = new GeoPoint((int)latitude, (int)longtitude);
		}
	}
}
