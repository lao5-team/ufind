package com.findu.demo.ui;

import java.util.ArrayList;

import android.util.Log;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.overlay.CustomGraphicsOverlay;

public class RoutingUI {
	// �ж�·�ߣ��ȳ�ʼ��1024����
	GeoPoint[] mRoutingPoints = new GeoPoint[1024];
	int mCurrentRountingPointIndex = 0;
	int mMaxRoutingLength = 1024;
	MapView mMapView;
	Geometry mRoutingGeometry = new Geometry();
	Symbol mRoutingSymbol = new Symbol();
	Symbol.Color mRoutingColor = mRoutingSymbol.new Color();
	CustomGraphicsOverlay mCustomGraphicsOverlay;
	int mPassIndex = 0;

	public RoutingUI(CustomGraphicsOverlay custom) {
		mRoutingColor.red = 0;
		mRoutingColor.blue = 0;
		mRoutingColor.green = 255;
		mRoutingColor.alpha = 126;

		mCustomGraphicsOverlay = custom;
	}

	public void setRouteLineColor(Symbol.Color color) {
		mRoutingColor = color;
	}

	public void onRouting(long latitude, long longitude) {

		Log.i("rlk", "------------:" + mCurrentRountingPointIndex
				+ " latitude:" + latitude + " longitude:" + longitude);

		mPassIndex++;
		if (mPassIndex < 3) {// ��̫��׼�ˣ�ȥ��ǰ2����
			return;
		}
		
		// ���ǰһ�����λ�ú͵�ǰλ����ͬ����ô����
		if (mCurrentRountingPointIndex > 0
				&& mRoutingPoints[mCurrentRountingPointIndex - 1]
						.getLatitudeE6() == latitude
				&& mRoutingPoints[mCurrentRountingPointIndex - 1]
						.getLongitudeE6() == longitude) {
			Log.i("rlk", "============");

			return;
		}

		/*if (mCurrentRountingPointIndex > 0) {// �������ƫ������Ҳ��Ҫ�����
			double dis = DistanceUtil.getDistance(
					mRoutingPoints[mCurrentRountingPointIndex - 1],
					new GeoPoint((int) latitude, (int) longitude));
			if (dis > 100000) {
				Log.i("rlk", "++++++++++++++++:" + mCurrentRountingPointIndex+" dis:"+dis);
				return;
			}
		}*/

		// �������鳤��ʱ��������
		if (mCurrentRountingPointIndex >= mMaxRoutingLength) {
			GeoPoint[] tmpGeoPoints = mRoutingPoints.clone();
			mMaxRoutingLength *= 2;
			mRoutingPoints = new GeoPoint[mMaxRoutingLength];
			System.arraycopy(mRoutingPoints, 0, tmpGeoPoints, 0,
					mMaxRoutingLength >> 1);
		}

		mRoutingPoints[mCurrentRountingPointIndex] = new GeoPoint(
				(int) latitude, (int) longitude);

		GeoPoint[] tmpPoints = new GeoPoint[mCurrentRountingPointIndex + 1];
		// ����mCurrentRountingPointIndex
		mCurrentRountingPointIndex++;
	
		System.arraycopy(mRoutingPoints, 0, tmpPoints, 0, mCurrentRountingPointIndex);
		mRoutingGeometry.setPolyLine(tmpPoints);

		mRoutingColor.red = 0;
		mRoutingColor.green = 255;
		mRoutingColor.blue = 0;
		mRoutingColor.alpha = 126;
		mRoutingSymbol.setLineSymbol(mRoutingColor, 5);

		// mRoutingGeometry.setPolyLine(showPoints);
		mCustomGraphicsOverlay.removeAllData();
		mCustomGraphicsOverlay.addGraphicOverlay();
		Graphic cGraphic = new Graphic(mRoutingGeometry, mRoutingSymbol);
		mCustomGraphicsOverlay.setCustomGraphicData(cGraphic, true);

	}
}
