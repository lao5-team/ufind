package com.findu.demo.overlay;

import android.R.integer;
import android.app.Activity;
import android.content.Context;

import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.FriendsApplication;

public class CustomRouteOverlay{

	public static final int ROUTE_MODE_WALK = 1;
	public static final int ROUTE_MODE_TRANSIT = 2;
	public static final int ROUTE_MODE_DRIVE = 3;
	
	private Context mContext;
	private MapView mMapView;
	private RouteOverlay mRouteOverlay;
	public TransitOverlay mTransitOverlay;
	
	//起点
	private MKPlanNode mStartPlanNode;
	//终点
	private MKPlanNode mEndPlanNode;
	
	//搜索相关
	MKSearch mSearch = null;
	RouteSearchListener mSearchResultListener;
	
	public CustomRouteOverlay(Activity context, MapView mapview){
		mContext = context;
		mMapView = mapview;
		mRouteOverlay = new RouteOverlay(context, mapview);
		mTransitOverlay = new TransitOverlay(context, mMapView);
		mSearch = new MKSearch();
		mStartPlanNode = new MKPlanNode();
		mEndPlanNode = new MKPlanNode();
		
		FriendsApplication application = (FriendsApplication) context.getApplication();
		//初始化搜索模块
		mSearch.init(application.mBMapManager, new MKSearchListener(){

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetAddrResult(arg0, arg1);
				}
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetBusDetailResult(arg0, arg1);
				}
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetDrivingRouteResult(arg0, arg1);
				}
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetPoiDetailSearchResult(arg0, arg1);
				}
			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetPoiResult(arg0, arg1, arg2);
				}
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetSuggestionResult(arg0, arg1);
				}
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetTransitRouteResult(arg0, arg1);
				}
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				if(mSearchResultListener != null){
					mSearchResultListener.onGetWalkingRouteResult(arg0, arg1);
				}
			}
			
		});
	}

	public void setRouteOverlayData(MKRoute route){
		mRouteOverlay.setData(route);
	}
	
	public void addRouteOverlay(){
		
		if(!mMapView.getOverlays().contains(mRouteOverlay)){
			mMapView.getOverlays().add(mRouteOverlay);			
		}
		
	}
	
	public void addBusRouteOverlay(){
		if(!mMapView.getOverlays().contains(mTransitOverlay)){
			mMapView.getOverlays().add(mTransitOverlay);			
		}
		
	}
	
	public void removeBusRouteOverlay(){
		if(mMapView.getOverlays().contains(mTransitOverlay)){
			mMapView.getOverlays().add(mTransitOverlay);			
		}
		
	}
	
	public void removeRouteOverlay(){
		if(mMapView.getOverlays().contains(mRouteOverlay)){
			mMapView.getOverlays().remove(mRouteOverlay);			
		}
	}
	
	public void setRouteSearchListener(RouteSearchListener listener){
		mSearchResultListener = listener;
	}
	
	public void setRouteStartPt(GeoPoint pt){
		mStartPlanNode.pt = pt;
		//mStartPlanNode.name = "新宫";
	}
	
	public void setRouteStartName(String name){
		mStartPlanNode.name = name;
	}
	
	public void setRouteEndPt(GeoPoint pt){
		mEndPlanNode.pt = pt;
		//mEndPlanNode.name = "公益西桥";
	}
	
	public void setRouteEndName(String name){
		mEndPlanNode.name = name;
	}
	
	public void startSearch(String city,int mode){
		switch (mode) {
		case ROUTE_MODE_WALK:
			//暂定在北京地区，后期需要用户选择或者自动选择城市
			mSearch.walkingSearch(city, mStartPlanNode, city, mEndPlanNode);
			break;
			
		case ROUTE_MODE_TRANSIT:
			mSearch.transitSearch(city, mStartPlanNode, mEndPlanNode);
			break;
			
		case ROUTE_MODE_DRIVE:
			mSearch.drivingSearch(city, mStartPlanNode, city, mEndPlanNode);
			break;

		default:
			mSearch.walkingSearch(city, mStartPlanNode, city, mEndPlanNode);
			break;
		}
	}
	
	public void reverseGeocode(GeoPoint pt)
	{
		mSearch.reverseGeocode(pt);
	}
}
