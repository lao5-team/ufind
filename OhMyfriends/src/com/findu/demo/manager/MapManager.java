package com.findu.demo.manager;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKLine;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.activity.MyFriendsMain;
import com.findu.demo.activity.RouteActivity;
import com.findu.demo.constvalue.ConstValue;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;
import com.findu.demo.overlay.CustomGraphicsOverlay;
import com.findu.demo.overlay.CustomItemizedOverlay;
import com.findu.demo.overlay.CustomRouteOverlay;
import com.findu.demo.overlay.ItemOverlayOnTapListener;
import com.findu.demo.overlay.LocationOverLay;
import com.findu.demo.overlay.RouteSearchListener;
import com.findu.demo.route.FGeoPoint;
import com.findu.demo.route.Route;
import com.findu.demo.route.RouteManager;
import com.findu.demo.util.BMapUtil;
import com.findu.demo.util.FindUtil;

public class MapManager extends BaseMapManager implements LocationChangedListener,
ItemOverlayOnTapListener, RouteSearchListener{
	public static String TAG = MapManager.class.getName();
	public static String ACTION_RECEIVE_LOCATION = "receive_location";


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
	
	MKRoutePlan mWalkingPlan;
	MKTransitRoutePlan mTransitPlan;
	ArrayList<FGeoPoint> mLocationPoints;
	
	private TextView  mPopupText = null;
	private View mViewCache = null;
	private View mPopupInfo = null;
	private View mPopupLeft = null;
	private View mPopupRight = null;
	private PopupOverlay   mPop  = null;
	private GeoPoint mDestPt = null;
	private int mMode = 0;
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
		super(activity, mapView);
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
				R.drawable.wa_icon));

		// ��Ӷ�λͼ��
		mLocationOverLay.addLocationOverlay();
		mLocationOverLay.setEnableCompass(true);

		mItemizedOverlay = new CustomItemizedOverlay(mActivity, mMapView);
		mItemizedOverlay.setItemOverlayTapListener(this);
		mItemizedOverlay.addItemizedOverlay();

		mGraphicsOverlay = new CustomGraphicsOverlay(mMapView);
		mGraphicsOverlay.addGraphicOverlay();


		mRouteOverlay = new CustomRouteOverlay(mActivity, mMapView);
		mRouteOverlay.setRouteSearchListener(this);
		
		mRouteManager = new RouteManager();
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();
		
		mLocationPoints = new ArrayList<FGeoPoint>();
        createPaopao();
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
	
	public GeoPoint getCurrentLocation()
	{
		return mCurrentPt;
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
		Graphic graphic = routeGraphic.setRoutePoints(getPointsfromRoute(route)).
		setColor(255, 0, 0, 126).setWidth(5).getGraphic();

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
		GeoPoint[] points = getPointsfromRoute(route);
		
		Graphic graphic = routeGraphic.setRoutePoints(points).
		setColor(0, 0, 255, 126).setWidth(10).getGraphic();

		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(graphic);
		mMapView.refresh();
	}

	@Override
	public boolean onTap(int index) {
		   mPopupText.setText(mItemizedOverlay.mItemizedOverlay.getItem(index).getTitle());
		   Bitmap[] bitMaps={
			    BMapUtil.getBitmapFromView(mPopupLeft), 		
			    BMapUtil.getBitmapFromView(mPopupInfo), 		
			    BMapUtil.getBitmapFromView(mPopupRight) 		
		    };
		    mPop.showPopup(bitMaps,mItemizedOverlay.mItemizedOverlay.getItem(index).getPoint(),32);
		return false;
	}
	

	@Override
	public boolean onTap(GeoPoint pt, MapView mMapView) {
        mViewCache = mActivity.getLayoutInflater().inflate(R.layout.custom_text_view, null);
        mPopupInfo = (View) mViewCache.findViewById(R.id.popinfo);
        mPopupLeft = (View) mViewCache.findViewById(R.id.popleft);
        mPopupRight = (View) mViewCache.findViewById(R.id.popright);
        mPopupText =(TextView) mViewCache.findViewById(R.id.textcache);
		return false;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// ���¶�λ����
		mLocationOverLay.setlocationOverlayData(mLocationAbout
				.getLocationData());
		mCurrentPt.setLatitudeE6((int) (location.getLatitude() * 1e6));
		mCurrentPt.setLongitudeE6((int) (location.getLongitude() * 1e6));
			Log.v(TAG, "onReceiveLocation: "+"Lat " + location.getLatitude() * 1e6
					+ " Long " + location.getLongitude() * 1e6);
		isRequest = false;
		mJuhuiGoalPt = mCurrentPt;
		mRouteOverlay.reverseGeocode(mCurrentPt);
		mRouteManager.addPoint(new FGeoPoint(mCurrentPt, new Date()));
		Log.v(TAG, "Point Num: " + mRouteManager.getPoints().size());
		mMapView.refresh();
		GeoPoint pt = new GeoPoint(mCurrentPt.getLatitudeE6(), mCurrentPt.getLongitudeE6());
		mLocationPoints.add(new FGeoPoint(pt, null));
		Intent intent = new Intent();
		intent.setAction(ACTION_RECEIVE_LOCATION);
		intent.putExtra("Lat", mCurrentPt.getLatitudeE6());
		intent.putExtra("Long", mCurrentPt.getLongitudeE6());
		mActivity.sendBroadcast(intent);
		//updateRoute();
	}
	
	public void resetMyLocation()
	{
		mMapController.animateTo(mCurrentPt);
		mMapView.refresh();
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
		GeoPoint[] points = mRouteManager.loadPointtxt();
		
		RouteGraphic routeGraphic = new RouteGraphic();
		Graphic graphic = routeGraphic.setRoutePoints(points).
		setColor(0, 0, 255, 126).setWidth(10).getGraphic();

		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(graphic);
		mMapView.refresh();
	}
		
	private void connectJuDian(GeoPoint ptStart, GeoPoint ptEnd) {
		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(drawLine(ptStart, ptEnd));
		
		mMapView.refresh();
	}
	
	private Graphic drawLine(GeoPoint ptStart, GeoPoint ptEnd) {

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
	
	public void searchRoute(String srcName, String destName, int mode)
	{
		Log.v(TAG, "searchRoute");
		MKPlanNode start = new MKPlanNode();
		start.name = srcName;
		MKPlanNode end = new MKPlanNode();
		if(null != destName)
		{
			end.name = destName;
		}
		else
		{
			end.pt = mJuhuiGoalPt;
		}

		
		mRouteOverlay.startSearch(mCity, start, end, mode);

	}
	
	private GeoPoint[] getPointsfromTransitPlan(MKTransitRoutePlan plan)
	{
		ArrayList<Object> routeResult = new ArrayList<Object>();
		if(plan.getNumLines() == 0)
		{
			for(int i=0; i<plan.getNumRoute(); i++)
			{
				routeResult.add(plan.getRoute(i));
			}
		}
		else if(plan.getNumRoute() == 0)
		{
			for(int i=0; i<plan.getNumLines(); i++)
			{
				routeResult.add(plan.getLine(i));
			}
		}
		else 
		{
			GeoPoint ptBeginTrans = null; 
			MKLine line = plan.getLine(0);
			if(line != null)
			{
				ptBeginTrans = line.getGetOnStop().pt;
			}
			
			//�������ǳ�վ
			if(FindUtil.isGeoPointsIdentical(ptBeginTrans, plan.getStart()))
			{
				//���������������Ĺ����������������������Ĳ���
				routeResult.add(plan.getLine(0));
				GeoPoint tailPoint = plan.getLine(0).getGetOffStop().pt;
				int routeIndex = 0;
				for(int i=1; i<plan.getNumLines(); i++)
				{
					if(FindUtil.isGeoPointsIdentical(tailPoint, plan.getLine(i).getGetOnStop().pt))
					{
						routeResult.add(plan.getLine(i));
						tailPoint = plan.getLine(i).getGetOffStop().pt;
					}
					else
					{
						routeResult.add(routeIndex);
						tailPoint = plan.getRoute(routeIndex).getEnd();
						routeIndex ++;
					}
				}
			}
			else
			{
				routeResult.add(plan.getRoute(0));
				GeoPoint tailPoint = plan.getRoute(0).getEnd();
				int routeIndex = 1;
				for(int i=0; i<plan.getNumRoute(); i++)
				{
					if(FindUtil.isGeoPointsIdentical(tailPoint, plan.getLine(i).getGetOnStop().pt))
					{
						routeResult.add(plan.getLine(i));
						tailPoint = plan.getLine(i).getGetOffStop().pt;
					}
					else
					{
						routeResult.add(plan.getRoute(routeIndex));
						tailPoint = plan.getRoute(routeIndex).getEnd();
						routeIndex++;
					}
				}				
			}
		}
		
        //��ӡ���
		Log.v(TAG, "��ӡ���");
		ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
		for(int i=0; i<routeResult.size(); i++)
		{
			Object line = routeResult.get(i);
			if(line instanceof MKLine)
			{
				Log.v(TAG, ((MKLine)line).getTitle());
				points.addAll(((MKLine)line).getPoints());
			}
			else
			{
				Log.v(TAG, "���� " + ((MKRoute)line).getDistance());
				for(int j=0; j<((MKRoute)line).getArrayPoints().size(); j++)
				{
					points.addAll(((MKRoute)line).getArrayPoints().get(j));
				}
			}
		}
		GeoPoint[] pointList = new GeoPoint[points.size()];
		for(int i=0; i<pointList.length; i++)
		{
			pointList[i] = points.get(i);
		}
		return pointList;
	}
	
	private GeoPoint[] getPointsfromRoutePlan(MKRoutePlan plan)
	{
		ArrayList<GeoPoint[]> pointsList = new ArrayList<GeoPoint[]>();
		ArrayList<GeoPoint>pointArray = new ArrayList<GeoPoint>();
		for(int i=0; i<plan.getNumRoutes(); i++)
		{
			GeoPoint[] pointList = getPointsfromRoute(plan.getRoute(i));
			pointsList.add(pointList);
			for(int j=0; j<pointList.length; j++)
			{
				pointArray.add(pointList[j]);
			}
		}
		GeoPoint pointsResult[] = new GeoPoint[pointArray.size()];
		for(int i=0; i<pointsResult.length; i++)
		{
			pointsResult[i] = pointArray.get(i);
		}
		return pointsResult;
	}
	
	
	private GeoPoint[] getPointsfromRoute(MKRoute route)
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
	
	private GeoPoint[] getPointsfromLine(MKLine line)
	{
		ArrayList<GeoPoint> pointList = line.getPoints();
		int totlePt = 0;
		// ���㹲�ж��ٸ����������
		for (int i = 0; i < pointList.size(); i++) {
				totlePt++;
		}

		GeoPoint[] routePoints = new GeoPoint[totlePt];
		// ��ʼ���������������
		int index = 0;
		for (int i = 0; i < pointList.size(); i++) {
				routePoints[index] = pointList.get(i);
				Log.v(TAG, routePoints[index].toString());
				index++;
		}
		return routePoints;
	}
	
	public void setWalkingRoute(MKRoutePlan plan)
	{
		mWalkingPlan = plan;
		GeoPoint [] points = getPointsfromRoutePlan(mWalkingPlan);
		mDestPt = points[points.length-1];
		RouteGraphic routeGraphic = new RouteGraphic();
		Graphic graphic = routeGraphic.setRoutePoints(points).
		setColor(0, 0, 255, 126).setWidth(10).getGraphic();

		mGraphicsOverlay.removeAllData();
		mGraphicsOverlay.setCustomGraphicData(graphic);
		
        OverlayItem itemBegin = new OverlayItem(points[0],"���","");
        /**
         * ����overlayͼ�꣬�粻���ã���ʹ�ô���ItemizedOverlayʱ��Ĭ��ͼ��.
         */
        itemBegin.setMarker(mActivity.getResources().getDrawable(R.drawable.begin_icon));
		
		mItemizedOverlay.addOverItem(itemBegin);
		mMapView.refresh();
		mMode = ConstValue.WALK;
		
	}
	
	public void setTransitRoute(MKTransitRoutePlan plan)
	{
		mDestPt = plan.getEnd();
//		GeoPoint[] points = getPointsfromTransitPlan(plan);
//		RouteGraphic routeGraphic = new RouteGraphic();
//		Graphic graphic = routeGraphic.setRoutePoints(points).
//		setColor(0, 0, 255, 126).setWidth(10).getGraphic();
//
//		mGraphicsOverlay.removeAllData();
//		mGraphicsOverlay.setCustomGraphicData(graphic);
//		
//        OverlayItem itemBegin = new OverlayItem(points[0],"���","");
//        /**
//         * ����overlayͼ�꣬�粻���ã���ʹ�ô���ItemizedOverlayʱ��Ĭ��ͼ��.
//         */
//        itemBegin.setMarker(mActivity.getResources().getDrawable(R.drawable.begin_icon));
//		
//		mItemizedOverlay.addOverItem(itemBegin);
		//mGraphicsOverlay.setCustomGraphicData(graphic);
		TransitOverlay  routeOverlay = new TransitOverlay (mActivity, mMapView);
	    // �˴���չʾһ��������Ϊʾ��
	    routeOverlay.setData(plan);
	  //�������ͼ��
	    mMapView.getOverlays().clear();
	  //���·��ͼ��
	    mMapView.getOverlays().add(routeOverlay);
	  //ִ��ˢ��ʹ��Ч
	    mMapView.refresh();
	    // ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
	    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
	  //�ƶ���ͼ�����
		mMapView.refresh();
		mMode = ConstValue.TRANSIT;
	}
	
	public void createPaopao(){
		
        //���ݵ����Ӧ�ص�
        PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				if(index == 0)
				{
					//���ɿ�ݷ�ʽ
					((MyFriendsMain)mActivity).addShortCut(mDestPt, "��ݷ�ʽ", mMode);
				}
				else
				{
					
				}
			}
        };
        mPop = new PopupOverlay(mMapView,popListener);
	}
	
	public void setDestPoint(GeoPoint pt)
	{
		mDestPt = pt;
	}
	
	public void updateRoute()
	{
		if(null == mDestPt)
			return;
		MKSearch search = new MKSearch();
		search.init(FriendsApplication.getInstance().mBMapManager, new MKSearchListener() {
			
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub
				setWalkingRoute(arg0.getPlan(0));
			}
			
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		MKPlanNode nodeSrc = new MKPlanNode();
		nodeSrc.pt = mCurrentPt;
		
		MKPlanNode nodeDest = new MKPlanNode();
		nodeDest.pt = mDestPt;		
		Log.d(TAG, nodeSrc.toString() + nodeDest.toString());
		search.walkingSearch("����", nodeSrc, "����", nodeDest);
	}

}
