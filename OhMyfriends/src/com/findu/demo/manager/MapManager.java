package com.findu.demo.manager;

import java.util.ArrayList;

import junit.framework.Assert;
import android.app.Activity;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;
import com.findu.demo.overlay.CustomGraphicsOverlay;
import com.findu.demo.overlay.CustomItemizedOverlay;
import com.findu.demo.overlay.CustomRouteOverlay;
import com.findu.demo.overlay.ItemOverlayOnTapListener;
import com.findu.demo.overlay.LocationOverLay;
import com.findu.demo.overlay.RouteSearchListener;
import com.findu.demo.route.Route;
import com.findu.demo.route.RouteManager;

public class MapManager extends BaseMapManager implements LocationChangedListener,
ItemOverlayOnTapListener, RouteSearchListener{
	public static String TAG = MapManager.class.getName();
	Activity mActivity;
	MapView mMapView;
	MapController mMapController;
	LocationAbout mLocationAbout;
	LocationOverLay mLocationOverLay;

	// �����ڵ�ͼ�����ͼ��ͼ��
	CustomGraphicsOverlay mGraphicsOverlay;

	// ��������ͼ������λ�õ�
	CustomItemizedOverlay mItemizedOverlay;

	// ·�߻���ר��
	CustomRouteOverlay mRouteOverlay;

	// ��Щ��ͼ��Ԫ��Ҫ��Ҫ������ȥ�أ�
	// ����ֱ��������
	Geometry mLineGeometry = new Geometry();

	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	boolean isLocationClientStop = false;
	private int mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;

	// �ۻ�ص�λ��
	GeoPoint mJuhuiGoalPt;
	// ��ǰ�ҵ�λ��
	GeoPoint mCurrentPt;
	OverlayItem mJuDianItem = new OverlayItem(mJuhuiGoalPt, "", "");
	String mCity = "";
	RouteManager mRouteManager;
	MKRoute mCurrentRoute;
	public class RouteGraphic 
	{
		Graphic mGraphic;
		Geometry mGeometry;
		Symbol mSymbol;
		Color mColor;
		int mWidth;
		public RouteGraphic()
		{
			//mGraphic = new 
			mGeometry = new Geometry();
			mSymbol = new Symbol();
			mColor = mSymbol.new Color();
			mColor.red = 255;
			mColor.green = 0;
			mColor.blue = 0;
			mColor.alpha = 128;
			mWidth = 5;
		}
		
		public RouteGraphic setRoutePoints(GeoPoint[] points)
		{
			mGeometry.setPolyLine(points);
			return this;
		}
		
		public RouteGraphic setColor(int r, int g, int b, int a)
		{
			mColor.red = r;
			mColor.green = g;
			mColor.blue = b;
			mColor.alpha = a;
			return this;
		}
		
		public RouteGraphic setWidth(int width)
		{
			mWidth = width;
			return this;
		}
		
		public Graphic getGraphic()
		{
			mSymbol.setLineSymbol(mColor, mWidth);
			return new Graphic(mGeometry, mSymbol);
			
		}
		
	}
	public MapManager(Activity activity, MapView mapView)
	{
		Assert.assertNotNull(activity);
		Assert.assertNotNull(mapView);
		mActivity = activity;
		mMapView = mapView;
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		
		mCurrentPt = new GeoPoint(0, 0);

		mLocationAbout = new LocationAbout(mActivity, mMapView);
		mLocationAbout.setLocationChangeListener(this);

		mLocationOverLay = new LocationOverLay(mMapView);
		mLocationOverLay.setlocationOverlayData(mLocationAbout
				.getLocationData());

		mLocationOverLay.setLocationMarker(mActivity.getResources().getDrawable(
				R.drawable.wa));

		// ��Ӷ�λͼ��
		mLocationOverLay.addLocationOverlay();
		mLocationOverLay.setEnableCompass(true);

		mItemizedOverlay = new CustomItemizedOverlay(mActivity, mMapView);
		mItemizedOverlay.setItemOverlayTapListener(this);
		mItemizedOverlay.addItemizedOverlay();

		mGraphicsOverlay = new CustomGraphicsOverlay(mMapView);
		mGraphicsOverlay.addGraphicOverlay();

		// �趨����������ɫ
//		mLineColor.red = 255;
//		mLineColor.green = 0;
//		mLineColor.blue = 0;
//		mLineColor.alpha = 255;

		mRouteOverlay = new CustomRouteOverlay(mActivity, mMapView);
		mRouteOverlay.setRouteSearchListener(this);
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();
		
		mRouteManager = new RouteManager();
	}
	
	//can be deleted
	@Override
	public void setMapView(MapView mapview) {
		// TODO Auto-generated method stub

	}

	//can be deleted
	@Override
	public MapView getMapView() {
		// TODO Auto-generated method stub
		return null;
	}

	//can be deleted
	@Override
	public void setLocationChangedListener(LocationChangedListener listener) {
		// TODO Auto-generated method stub

	}

	//can be deleted
	@Override
	public void setMapController(MapController controller) {
		// TODO Auto-generated method stub

	}

	//can be deleted
	@Override
	public MapController getMapController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MKMapViewListener getMapViewListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mapListenerInit(MapListenerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.v(TAG, arg0.addressComponents.city);
		mCity = arg0.addressComponents.city;
		
	}

	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult routeResult, int arg1) {
		MKRoute route = routeResult.getPlan(0).getRoute(0);
		RouteGraphic routeGraphic = new RouteGraphic();
		Graphic graphic = routeGraphic.setRoutePoints(getPointsfromRoutePlan(route)).
		setColor(255, 0, 0, 126).setWidth(5).getGraphic();
//		mDrivingGeometry.setPolyLine(getPointsfromRoutePlan(route));
//
//		mDrivingColor.red = 255;
//		mDrivingColor.green = 0;
//		mDrivingColor.blue = 0;
//		mDrivingColor.alpha = 126;
//		mDrivingSymbol.setLineSymbol(mDrivingColor, 5);
//
//		if (mDrivingGraphic == null) {
//			mDrivingGraphic = new Graphic(mDrivingGeometry, mDrivingSymbol);
//		}

		mGraphicsOverlay.removeAllData();
		//mGraphicsOverlay.setCustomGraphicData(mLineGraphic);
		mGraphicsOverlay.setCustomGraphicData(graphic);
		mMapView.refresh();
		
	}

	@Override
	public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult routeResult, int arg1) {
		// TODO Auto-generated method stub
		MKTransitRoutePlan plan = routeResult.getPlan(0);
		MKRoute route = plan.getRoute(0);
		
		
		mRouteOverlay.mTransitOverlay.setData(routeResult.getPlan(0));
		Log.v(TAG, plan.getContent());
		for(int i=0; i<plan.getNumLines(); i++)
		{
			Log.v(TAG, plan.getLine(i).getTitle());
		}


		mGraphicsOverlay.removeAllData();
		mRouteOverlay.addBusRouteOverlay();
		mMapView.refresh();
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult routeResult, int arg1) {
		Log.v(TAG, "numPlan " + routeResult.getNumPlan());
		MKRoute route = routeResult.getPlan(0).getRoute(0);
		mCurrentRoute = route;
		RouteGraphic routeGraphic = new RouteGraphic();
		GeoPoint[] points = getPointsfromRoutePlan(route);
		
		Graphic graphic = routeGraphic.setRoutePoints(points).
		setColor(0, 0, 255, 126).setWidth(10).getGraphic();

		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(graphic);
		mMapView.refresh();
		

		
		
	}

	@Override
	public boolean onTap(int index) {

		return false;
	}

	@Override
	public boolean onTap(GeoPoint pt, MapView mMapView) {
		// TODO Auto-generated method stub
		mJuhuiGoalPt = pt;

		// ��������
		connectJuDian(mCurrentPt, mJuhuiGoalPt);

		mItemizedOverlay.removeOverItem(mJuDianItem);
		mJuDianItem.setGeoPoint(mJuhuiGoalPt);
		mJuDianItem.setMarker(mActivity.getResources().getDrawable(R.drawable.redhat));

		// ���ֱ��item
		mItemizedOverlay.addOverItem(mJuDianItem);

		// �����г������С�����·��
		//mRouteOverlay.setRouteEndPt(mJuhuiGoalPt);// ����·���յ�

		MKPlanNode start = new MKPlanNode();
		start.pt = mCurrentPt;
		MKPlanNode end = new MKPlanNode();
		end.pt = mJuhuiGoalPt;
//		mRouteOverlay.startSearch(mCity, start, end, CustomRouteOverlay.ROUTE_MODE_WALK);
//		mRouteOverlay.startSearch(CustomRouteOverlay.ROUTE_MODE_TRANSIT);
//		mRouteOverlay.startSearch(CustomRouteOverlay.ROUTE_MODE_DRIVE);
		mMapView.refresh();
		return false;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// ���¶�λ����
		mLocationOverLay.setlocationOverlayData(mLocationAbout
				.getLocationData());
		// ����ͼ������ִ��ˢ�º���Ч
		// mMapView.refresh();

		mCurrentPt.setLatitudeE6((int) (location.getLatitude() * 1e6));
		mCurrentPt.setLongitudeE6((int) (location.getLongitude() * 1e6));

		// ���ֶ�����������״ζ�λʱ���ƶ�����λ��
		if (isRequest || isFirstLoc) {
			// �ƶ���ͼ����λ��
			Log.v(TAG, "onReceiveLocation: "+"Lat " + location.getLatitude() * 1e6
					+ " Long " + location.getLongitude() * 1e6);
			mMapController.animateTo(mCurrentPt);
			isRequest = false;

			mJuhuiGoalPt = mCurrentPt;
		}
		// �״ζ�λ���
		isFirstLoc = false;

		//connectJuDian(mCurrentPt, mJuhuiGoalPt);
		mRouteOverlay.reverseGeocode(mCurrentPt);
		mRouteManager.savePoint(mCurrentPt);
		// ����·�����
		//mRouteOverlay.setRouteStartPt(mCurrentPt);
		
	}
	
	public void saveRoute()
	{
		if(null != mCurrentRoute)
		{
//			Route myroute = new Route("");
//			GeoPoint[] points = getPointsfromRoutePlan(mCurrentRoute);
//			myroute.mPoints = points.clone();
//			mRouteManager.save(myroute);
		}

	}
	
	public void loadRoute()
	{
		Log.v(TAG, "loadRoute");
		ArrayList<Route> routes = null;//mRouteManager.loadRoutes("");
		Route myroute = routes.get(0);
		RouteGraphic routeGraphic = new RouteGraphic();
		Graphic graphic = routeGraphic.setRoutePoints(null).
		setColor(0, 0, 255, 126).setWidth(10).getGraphic();
		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(graphic);
		mMapView.refresh();
		
	}
		
	private void connectJuDian(GeoPoint ptStart, GeoPoint ptEnd) {
		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(drawLine(ptStart, ptEnd));
//		if(mCurrentRunMode == CustomRouteOverlay.ROUTE_MODE_WALK){
//			mGraphicsOverlay.setCustomGraphicData(mWalkingGraphic);
//			
//		}else if(mCurrentRunMode == CustomRouteOverlay.ROUTE_MODE_TRANSIT){
//			mGraphicsOverlay.setCustomGraphicData(mBusingGraphic);
//			
//		}else{
//			mGraphicsOverlay.setCustomGraphicData(mDrivingGraphic);			
//		}
		
		mMapView.refresh();
	}
	
	private Graphic drawLine(GeoPoint ptStart, GeoPoint ptEnd) {

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
		return null;
	}
	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		// TODO Auto-generated method stub
		
	}
	
	public void destroy()
	{
		mLocationAbout.setLocationStop(true);
	}
	
	public void searchRoute(String destName, int mode)
	{
		Log.v(TAG, "searchRoute");
		MKPlanNode start = new MKPlanNode();
		start.pt = mCurrentPt;
		MKPlanNode end = new MKPlanNode();
		if(null != destName)
		{
			end.name = destName;
		}
		else
		{
			end.pt = mJuhuiGoalPt;
		}

		
		mRouteOverlay.startSearch(mCity, start, end, CustomRouteOverlay.ROUTE_MODE_WALK);

	}
	
	private GeoPoint[] getPointsfromRoutePlan(MKRoute route)
	{
		ArrayList<ArrayList<GeoPoint>> pointList = route.getArrayPoints();

		int totlePt = 0;

		// ���㹲�ж��ٸ����������
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				Log.v(TAG, route.getStep(i).getContent());
				totlePt++;
			}
		}

		GeoPoint[] routePoints = new GeoPoint[totlePt];

		// ��ʼ���������������
		int index = 0;
		for (int i = 0; i < pointList.size(); i++) {
			for (int j = 0; j < pointList.get(i).size(); j++) {
				routePoints[index] = pointList.get(i).get(j);
				Log.v(TAG, routePoints[index].toString());
				index++;
			}
		}
		return routePoints;
	}

}
