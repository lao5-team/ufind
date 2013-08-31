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
	// ��λ���
	LocationClient mLocClient;
	LocationData mLocData = null;
	MyLocationListenner mMyListener = new MyLocationListenner();
	
	private LocationChangedListener mLocationListener;
	
	public LocationAbout(Context context, MapView mapview) {
		mContext = context;
		mMapView = mapview;

		// ��λ��ʼ��
		mLocClient = new LocationClient(context.getApplicationContext());
		mLocData = new LocationData();
		mLocClient.registerLocationListener(mMyListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
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
	 * ��λSDK��������
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
			// �������ʾ��λ����Ȧ����accuracy��ֵΪ0����
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
