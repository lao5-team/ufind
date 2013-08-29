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
import com.findu.demo.db.LocusDbManager;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;
import com.findu.demo.overlay.CustomGraphicsOverlay;
import com.findu.demo.overlay.CustomItemizedOverlay;
import com.findu.demo.overlay.CustomRouteOverlay;
import com.findu.demo.overlay.ItemOverlayOnTapListener;
import com.findu.demo.overlay.LocationOverLay;
import com.findu.demo.overlay.RouteSearchListener;
import com.findu.demo.ui.CommandMenu;
import com.findu.demo.ui.RoutingUI;

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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyFriendsMain extends Activity implements LocationChangedListener,
		ItemOverlayOnTapListener, RouteSearchListener {

	private final String TAG = "MyFriendsMain";
	private final int DIANMEN_LATITUDE = (int) (39.97923 * 1E6);
	private final int DIANMEN_LONGITUDE = (int) (39.97923 * 1E6);
	LocationAbout mLocationAbout;

	LocationOverLay mLocationOverLay;

	// 用于在地图上描绘图形图像
	CustomGraphicsOverlay mGraphicsOverlay;

	// 用于描绘地图中其他位置点
	CustomItemizedOverlay mItemizedOverlay;

	// 路线绘制专用
	CustomRouteOverlay mRouteOverlay;

	// 聚会地点位置
	GeoPoint mJuhuiGoalPt = new GeoPoint(DIANMEN_LATITUDE, DIANMEN_LONGITUDE);
	// 当前我的位置
	GeoPoint mCurrentPt = new GeoPoint(DIANMEN_LATITUDE, DIANMEN_LONGITUDE);
	//
	OverlayItem mJuDianItem = new OverlayItem(mJuhuiGoalPt, "", "");

	// 这些画图的元素要不要独立出去呢？
	// 构建直接连接线
	Geometry mLineGeometry = new Geometry();

	// 设定直接连接线样式
	Symbol mLineSymbol = new Symbol();
	Symbol.Color mLineColor = mLineSymbol.new Color();
	Graphic mLineGraphic;// 绘图专用
	GeoPoint[] mLinePoints = new GeoPoint[2];// 两点连接坐标

	// 构建步行路线连接线
	Geometry mWalkingGeometry = new Geometry();
	Symbol mWalkingSymbol = new Symbol();
	Symbol.Color mWalkingColor = mWalkingSymbol.new Color();
	Graphic mWalkingGraphic;// 步行连接线绘图专用

	// 构建步行路线连接线
	Geometry mBusingGeometry = new Geometry();
	Symbol mBusingSymbol = new Symbol();
	Symbol.Color mBusingColor = mBusingSymbol.new Color();
	Graphic mBusingGraphic;// 公交连接线绘图专用

	// 构建步行路线连接线
	Geometry mDrivingGeometry = new Geometry();
	Symbol mDrivingSymbol = new Symbol();
	Symbol.Color mDrivingColor = mDrivingSymbol.new Color();
	Graphic mDrivingGraphic;// 公交连接线绘图专用

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;

	private int mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;
	private CommandMenu mCommandMenu;
	private LocusDbManager mDbManager;
	private RoutingUI mRoutingLineUI;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CharSequence titleLable = "MyFriend";
		setTitle(titleLable);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);

		mLocationAbout = new LocationAbout(this, mMapView);
		mLocationAbout.setLocationChangeListener(this);

		mLocationOverLay = new LocationOverLay(mMapView);
		mLocationOverLay.setlocationOverlayData(mLocationAbout
				.getLocationData());

		mLocationOverLay.setLocationMarker(getResources().getDrawable(
				R.drawable.wa));

		// 添加定位图层
		mLocationOverLay.addLocationOverlay();
		mLocationOverLay.setEnableCompass(true);

		mItemizedOverlay = new CustomItemizedOverlay(this, mMapView);
		mItemizedOverlay.setItemOverlayTapListener(this);
		mItemizedOverlay.addItemizedOverlay();

		mGraphicsOverlay = new CustomGraphicsOverlay(mMapView);
		mGraphicsOverlay.addGraphicOverlay();

		// 设定连接线条颜色
		mLineColor.red = 255;
		mLineColor.green = 0;
		mLineColor.blue = 0;
		mLineColor.alpha = 255;

		mRouteOverlay = new CustomRouteOverlay(this, mMapView);
		mRouteOverlay.setRouteSearchListener(this);
		// 修改定位数据后刷新图层生效
		mMapView.refresh();
		// mCommandMenu = new CommandMenu(this, mMapView);
		mDbManager = new LocusDbManager(this, mMapView, mGraphicsOverlay);

	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		// mLocClient.requestLocation();
		// Toast.makeText(LocationOverlayDemo.this, "正在定位…",
		// Toast.LENGTH_SHORT).show();
	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		// mMyLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效
		mMapView.refresh();
	}

	private void connectJuDian(GeoPoint ptStart, GeoPoint ptEnd) {
		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(drawLine(ptStart, ptEnd), true);
		if(mCurrentRunMode == CustomRouteOverlay.ROUTE_MODE_WALK){
			mGraphicsOverlay.setCustomGraphicData(mWalkingGraphic, true);
			
		}else if(mCurrentRunMode == CustomRouteOverlay.ROUTE_MODE_TRANSIT){
			mGraphicsOverlay.setCustomGraphicData(mBusingGraphic, true);
			
		}else{
			mGraphicsOverlay.setCustomGraphicData(mDrivingGraphic, true);
		}
		
	}

	private Graphic drawLine(GeoPoint ptStart, GeoPoint ptEnd) {

		mLinePoints[0] = ptStart;
		mLinePoints[1] = ptEnd;

		mLineGeometry.setPolyLine(mLinePoints);
		mLineSymbol.setLineSymbol(mLineColor, 3);

		if (mLineGraphic == null) {
			mLineGraphic = new Graphic(mLineGeometry, mLineSymbol);
		}

		return mLineGraphic;
	}

	@Override
	protected void onPause() {
		isLocationClientStop = true;
		mMapView.onPause();
		// mCommandMenu.removeMenuView();
		super.onPause();
	}

	@Override
	protected void onResume() {
		isLocationClientStop = false;
		mMapView.onResume();
		// mCommandMenu.showMenuView();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocationAbout.setLocationStop(true);
		isLocationClientStop = true;
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
//			mGraphicsOverlay.removeAllData();
//			mGraphicsOverlay.setCustomGraphicData(mLineGraphic);
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;
			mRouteOverlay.startSearch(mCurrentRunMode);
//			mGraphicsOverlay.setCustomGraphicData(mWalkingGraphic);
			break;
			
		case R.id.route_bus:
//			mGraphicsOverlay.removeAllData();
//			mGraphicsOverlay.setCustomGraphicData(mLineGraphic);
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_TRANSIT;
			mRouteOverlay.startSearch(mCurrentRunMode);
//			mGraphicsOverlay.setCustomGraphicData(mBusingGraphic);
			break;
			
		case R.id.route_drive:
//			mGraphicsOverlay.removeAllData();
//			mGraphicsOverlay.setCustomGraphicData(mLineGraphic);
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_DRIVE;
			mRouteOverlay.startSearch(mCurrentRunMode);
//			mGraphicsOverlay.setCustomGraphicData(mDrivingGraphic);
			break;
			
		default:
			break;
		}
		
		mMapView.refresh();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {

		// 更新定位数据
		mLocationOverLay.setlocationOverlayData(mLocationAbout
				.getLocationData());
		// 更新图层数据执行刷新后生效
		// mMapView.refresh();

		mCurrentPt.setLatitudeE6((int) (location.getLatitude() * 1e6));
		mCurrentPt.setLongitudeE6((int) (location.getLongitude() * 1e6));

		// 是手动触发请求或首次定位时，移动到定位点
		if (isRequest || isFirstLoc) {
			// 移动地图到定位点
			mMapController.animateTo(new GeoPoint(
					(int) (location.getLatitude() * 1e6), (int) (location
							.getLongitude() * 1e6)));
			isRequest = false;

			mJuhuiGoalPt = mCurrentPt;
		}
		// 首次定位完成

		connectJuDian(mCurrentPt, mJuhuiGoalPt);

		// 设置路线起点
		mRouteOverlay.setRouteStartPt(mCurrentPt);
		mDbManager.insertLocusDb((int) (location.getLatitude() * 1e6),
				(int) (location.getLongitude()));
		if (mRoutingLineUI == null) {
			mRoutingLineUI = new RoutingUI(mGraphicsOverlay);
		}
		mGraphicsOverlay.addGraphicOverlay();
		mRoutingLineUI.onRouting((long) (location.getLatitude() * 1e6),
				(long) (location.getLongitude()* 1e6));
		isFirstLoc = false;
	}

	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTap(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTap(GeoPoint pt, MapView mMapView) {
		mJuhuiGoalPt = pt;

		// 画连接线
		connectJuDian(mCurrentPt, mJuhuiGoalPt);

		mItemizedOverlay.removeOverItem(mJuDianItem);
		mJuDianItem.setGeoPoint(mJuhuiGoalPt);
		mJuDianItem.setMarker(getResources().getDrawable(R.drawable.redhat));

		// 添加直线item
		mItemizedOverlay.addOverItem(mJuDianItem);

		// 设置行车、步行、公交路线
		mRouteOverlay.setRouteEndPt(mJuhuiGoalPt);// 设置路线终点

		mRouteOverlay.startSearch(CustomRouteOverlay.ROUTE_MODE_WALK);
//		mRouteOverlay.startSearch(CustomRouteOverlay.ROUTE_MODE_TRANSIT);
//		mRouteOverlay.startSearch(CustomRouteOverlay.ROUTE_MODE_DRIVE);
		mMapView.refresh();
		return false;
	}

	@Override
	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		Log.i(TAG, "onGetAddrResult");
	}

	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

		Log.i(TAG, "onGetBusDetailResult");
	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult routeResult, int arg1) {

		MKRoute route = routeResult.getPlan(0).getRoute(0);
		ArrayList<ArrayList<GeoPoint>> pointList = route.getArrayPoints();

		int totlePt = 0;

		// 计算共有多少个地理坐标点
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				totlePt++;
			}
		}

		GeoPoint[] routePoints = new GeoPoint[totlePt];

		// 初始化地理坐标点数组
		int index = 0;
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				routePoints[index] = pointList.get(i).get(j);
				index++;
			}
		}

		mDrivingGeometry.setPolyLine(routePoints);

		mDrivingColor.red = 255;
		mDrivingColor.green = 0;
		mDrivingColor.blue = 0;
		mDrivingColor.alpha = 126;
		mDrivingSymbol.setLineSymbol(mDrivingColor, 5);

		if (mDrivingGraphic == null) {
			mDrivingGraphic = new Graphic(mDrivingGeometry, mDrivingSymbol);
		}

		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(mLineGraphic, false);
		mGraphicsOverlay.setCustomGraphicData(mDrivingGraphic, true);
	}

	@Override
	public void onGetPoiDetailSearchResult(int arg0, int arg1) {

		Log.i(TAG, "onGetPoiDetailSearchResult");
	}

	@Override
	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {

		Log.i(TAG, "onGetPoiResult");
	}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult routeResult,
			int arg1) {
		MKTransitRoutePlan plan = routeResult.getPlan(0);
		MKRoute route = plan.getRoute(0);
		
		
		mRouteOverlay.mTransitOverlay.setData(routeResult.getPlan(0));
		Log.v(TAG, plan.getContent());
		for(int i=0; i<plan.getNumLines(); i++)
		{
			Log.v(TAG, plan.getLine(i).getTitle());
		}
		ArrayList<ArrayList<GeoPoint>> pointList = route.getArrayPoints();

		int totlePt = 0;

		// 计算共有多少个地理坐标点
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				totlePt++;
			}
		}

		GeoPoint[] routePoints = new GeoPoint[totlePt];

		// 初始化地理坐标点数组
		int index = 0;
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				routePoints[index] = pointList.get(i).get(j);
				index++;
			}
		}

		mBusingGeometry.setPolyLine(routePoints);

		mBusingColor.red = 0;
		mBusingColor.green = 255;
		mBusingColor.blue = 0;
		mBusingColor.alpha = 126;
		mBusingSymbol.setLineSymbol(mBusingColor, 5);

		if (mBusingGraphic == null) {
			mBusingGraphic = new Graphic(mBusingGeometry, mBusingSymbol);
		}

		mGraphicsOverlay.removeAllData();
		mRouteOverlay.addBusRouteOverlay();
		mGraphicsOverlay.setCustomGraphicData(mLineGraphic, false);
		mGraphicsOverlay.setCustomGraphicData(mBusingGraphic, true);
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult routeResult,
			int arg1) {

		MKRoute route = routeResult.getPlan(0).getRoute(0);
		ArrayList<ArrayList<GeoPoint>> pointList = route.getArrayPoints();

		int totlePt = 0;

		// 计算共有多少个地理坐标点
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				totlePt++;
			}
		}

		GeoPoint[] routePoints = new GeoPoint[totlePt];

		// 初始化地理坐标点数组
		int index = 0;
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				routePoints[index] = pointList.get(i).get(j);
				index++;
			}
		}

		mWalkingGeometry.setPolyLine(routePoints);

		mWalkingColor.red = 0;
		mWalkingColor.green = 0;
		mWalkingColor.blue = 255;
		mWalkingColor.alpha = 126;
		mWalkingSymbol.setLineSymbol(mWalkingColor, 5);

		if (mWalkingGraphic == null) {
			mWalkingGraphic = new Graphic(mWalkingGeometry, mWalkingSymbol);
		}

		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(mLineGraphic);
		mGraphicsOverlay.setCustomGraphicData(mWalkingGraphic);
		mGraphicsOverlay.setCustomGraphicData(mLineGraphic, false);
		mGraphicsOverlay.setCustomGraphicData(mWalkingGraphic, true);
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
