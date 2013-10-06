package com.findu.demo.manager;

import android.app.Activity;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;

public class MiniMapManager extends MapManager{

	
	public MiniMapManager(Activity activity, MapView mapView) {
		super(activity, mapView);
	}
}
