package com.findu.demo;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;
import com.findu.demo.manager.MapManager;
import com.findu.demo.overlay.CustomGraphicsOverlay;
import com.findu.demo.overlay.CustomItemizedOverlay;
import com.findu.demo.overlay.CustomRouteOverlay;
import com.findu.demo.overlay.ItemOverlayOnTapListener;
import com.findu.demo.overlay.LocationOverLay;
import com.findu.demo.overlay.RouteSearchListener;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyFriendsMain extends Activity {

	private final String TAG = "MyFriendsMain";
	private final int DIANMEN_LATITUDE = (int) (39.97923 * 1E6);
	private final int DIANMEN_LONGITUDE = (int) (39.97923 * 1E6);
	MapView mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;

	private int mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;
	private MapManager mMapManager;
	private EditText mEditTextDest = null;
	private Button mButtonSearch = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CharSequence titleLable = "MyFriend";
		setTitle(titleLable);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mEditTextDest = (EditText)findViewById(R.id.editText_dest);
		mButtonSearch = (Button)findViewById(R.id.button_search);
		mButtonSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMapManager.searchRoute(null, CustomRouteOverlay.ROUTE_MODE_WALK);
			}
		});
		mMapManager = new MapManager(this, mMapView);
		
	}

//	/**
//	 * 手动触发一次定位请求
//	 */
//	public void requestLocClick() {
//		isRequest = true;
//		// mLocClient.requestLocation();
//		// Toast.makeText(LocationOverlayDemo.this, "正在定位…",
//		// Toast.LENGTH_SHORT).show();
//	}
//
//	/**
//	 * 修改位置图标
//	 * 
//	 * @param marker
//	 */
//	public void modifyLocationOverlayIcon(Drawable marker) {
//		// 当传入marker为null时，使用默认图标绘制
//		// mMyLocationOverlay.setMarker(marker);
//		// 修改图层，需要刷新MapView生效
//		mMapView.refresh();
//	}
//
//	private void connectJuDian(GeoPoint ptStart, GeoPoint ptEnd) {
//		mGraphicsOverlay.removeAllData();
//		mGraphicsOverlay.setCustomGraphicData(drawLine(ptStart, ptEnd));
//		if(mCurrentRunMode == CustomRouteOverlay.ROUTE_MODE_WALK){
//			mGraphicsOverlay.setCustomGraphicData(mWalkingGraphic);
//			
//		}else if(mCurrentRunMode == CustomRouteOverlay.ROUTE_MODE_TRANSIT){
//			mGraphicsOverlay.setCustomGraphicData(mBusingGraphic);
//			
//		}else{
//			mGraphicsOverlay.setCustomGraphicData(mDrivingGraphic);			
//		}
//		
//		mMapView.refresh();
//	}
//
//	private Graphic drawLine(GeoPoint ptStart, GeoPoint ptEnd) {
//
//		mLinePoints[0] = ptStart;
//		mLinePoints[1] = ptEnd;
//
//		mLineGeometry.setPolyLine(mLinePoints);
//		mLineSymbol.setLineSymbol(mLineColor, 3);
//
//		if (mLineGraphic == null) {
//			mLineGraphic = new Graphic(mLineGeometry, mLineSymbol);
//		}
//
//		return mLineGraphic;
//	}

	@Override
	protected void onPause() {
		isLocationClientStop = true;
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		isLocationClientStop = false;
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		//mLocationAbout.setLocationStop(true);
		//isLocationClientStop = true;
		mMapManager.destroy();
		mMapView.destroy();
		if(FriendsApplication.getInstance().mBMapManager!=null)
		{
			Log.v(FriendsApplication.TAG, "destroy BMapManager");
			FriendsApplication.getInstance().mBMapManager.destroy();
			FriendsApplication.getInstance().mBMapManager = null;
		}
		super.onDestroy();
		System.exit(0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case R.id.route_walk:
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;
			//mRouteOverlay.startSearch(mCurrentRunMode);
			break;
			
		case R.id.route_bus:
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_TRANSIT;
			//mRouteOverlay.startSearch(mCurrentRunMode);
			break;
			
		case R.id.route_drive:
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_DRIVE;
			//mRouteOverlay.startSearch(mCurrentRunMode);
			break;
			
		default:
			break;
		}
		
		mMapView.refresh();
		return super.onOptionsItemSelected(item);
	}

}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapView extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}
}
