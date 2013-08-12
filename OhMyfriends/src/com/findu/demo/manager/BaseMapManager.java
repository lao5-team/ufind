package com.findu.demo.manager;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.FriendsApplication;
import com.findu.demo.location.LocationChangedListener;

public abstract class BaseMapManager {
	
//	public abstract void setMapView(MapView mapview);
//	
//	public abstract MapView getMapView();
	
	//public abstract void mapInit(MainActivity context, MapView mapView);
	public abstract void setLocationChangedListener(LocationChangedListener listener);
//	public abstract void setMapController(MapController controller);
//	public abstract MapController getMapController();
	public abstract MKMapViewListener getMapViewListener();
	public abstract void mapListenerInit(MapListenerCallback callback);
	
}
