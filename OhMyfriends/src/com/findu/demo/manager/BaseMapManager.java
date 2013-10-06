package com.findu.demo.manager;

import junit.framework.Assert;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;
import com.findu.demo.overlay.LocationOverLay;

public abstract class BaseMapManager {
	protected Activity mActivity;
	protected MapView mMapView;
	protected MapController mMapController;
	protected LocationAbout mLocationAbout;
	protected LocationOverLay mLocationOverLay;
	
	public MapView getMapView(){
		return mMapView;
	}
	
	//public abstract void mapInit(MainActivity context, MapView mapView);
	public abstract void setLocationChangedListener(LocationChangedListener listener);
	public abstract void setMapController(MapController controller);
	public abstract MapController getMapController();
	public abstract MKMapViewListener getMapViewListener();
	public abstract void mapListenerInit(MapListenerCallback callback);
	
	public BaseMapManager(Activity activity, MapView mapView)
	{
		Assert.assertNotNull(activity);
		Assert.assertNotNull(mapView);
		mActivity = activity;
		mMapView = mapView;
	}
	
}
