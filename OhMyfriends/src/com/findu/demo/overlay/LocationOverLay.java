package com.findu.demo.overlay;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/*
 * ���ض�λ������
 */
public class LocationOverLay {

	locationOverlay mMyLocationOverlay;
	LocationData mLocationData;
	MapView mMapView;

	public LocationOverLay(MapView mapView) {
		// ��λͼ���ʼ��
		mMyLocationOverlay = new locationOverlay(mapView);
		mMapView = mapView;

	}

	// ���ö�λ����
	public void setlocationOverlayData(LocationData locdata) {
		mLocationData = locdata;
		mMyLocationOverlay.setData(mLocationData);
	}

	public void addLocationOverlay() {
		if (!mMapView.getOverlays().contains(mMyLocationOverlay))
			mMapView.getOverlays().add(mMyLocationOverlay);
	}

	public void setEnableCompass(boolean enable) {
		if (enable) {
			mMyLocationOverlay.enableCompass();
		} else {
			mMyLocationOverlay.disableCompass();
		}
	}

	public void setLocationMarker(Drawable drawable) {
		mMyLocationOverlay.setMarker(drawable);
	}

	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// �������¼�,��������
			// popupText.setBackgroundResource(R.drawable.popup);
			// popupText.setText("�ҵ�λ��");
			// pop.showPopup(BMapUtil.getBitmapFromView(popupText),
			// new GeoPoint((int)(locData.latitude*1e6),
			// (int)(locData.longitude*1e6)),
			// 8);
			return true;
		}

	}

}
