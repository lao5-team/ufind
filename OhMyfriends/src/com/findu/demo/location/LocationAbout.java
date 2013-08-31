package com.findu.demo.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.findu.demo.overlay.LocationOverLay;

public class LocationAbout {

	Context mContext;
	MapView mMapView;
	// 定位相关
	LocationClient mLocClient;
	LocationData mLocData = null;
	MyLocationListenner mMyListener = new MyLocationListenner();
	
	private LocationChangedListener mLocationListener;
	
	public LocationAbout(Context context, MapView mapview) {
		mContext = context;
		mMapView = mapview;

		// 定位初始化
		mLocClient = new LocationClient(context.getApplicationContext());
		mLocData = new LocationData();
		mLocClient.registerLocationListener(mMyListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000);
		mLocClient.setLocOption(option);
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true)
				{
					if(!mLocClient.isStarted())
					{
						mLocClient.start();
					}
				}

				
			}
		});

	}

	public void setLocationChangeListener(LocationChangedListener listener){
		
		mLocationListener = listener;
	}
	public LocationData getLocationData() {
		return mLocData;
	}
	
	public LocationClient getLocationClient(){
		return mLocClient;
	}

	public void setLocationStop(boolean stop){
		if(stop){
			mLocClient.stop();
		}else{
			mLocClient.start();
		}
	}
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if(mLocationListener != null){
				mLocationListener.onReceiveLocation(location);
			}
			mLocData.latitude = location.getLatitude();
			mLocData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			mLocData.accuracy = location.getRadius();
			mLocData.direction = location.getDerect();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
}
