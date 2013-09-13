package com.findu.demo.activity;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.findu.demo.R;
import com.findu.demo.adapter.RouteAdapter;
import com.findu.demo.manager.MapManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class RouteActivity extends Activity {

	Button mTransitButton;
	Button mDriveButton;
	Button mWalkButton;
	Button mBackButton;
	Button mSearchButton;
	EditText mRouteNameEdit;
	EditText mBeginEdit;
	EditText mEndEdit;
	ListView mRoutesList;
	
	MapManager mMapManager;
	MKSearch mSearch;
	RouteAdapter mRouteAdapter;
	int mMode = WALK;
	
	static final String TAG = RouteActivity.class.getName();
	static final int WALK = 0;
	static final int TRANSIT = 1;
	static final int DRIVE = 2;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View mainView = getLayoutInflater().inflate(R.layout.route_layout, null);
		this.setContentView(mainView);
		mTransitButton = (Button)mainView.findViewById(R.id.transit);
		mDriveButton = (Button)mainView.findViewById(R.id.drive);
		mWalkButton = (Button)mainView.findViewById(R.id.walk);
		mBackButton = (Button)mainView.findViewById(R.id.back);
		mSearchButton = (Button)mainView.findViewById(R.id.search);
		mRouteNameEdit = (EditText)mainView.findViewById(R.id.route_name);
		mBeginEdit = (EditText)mainView.findViewById(R.id.begin);
		mEndEdit = (EditText)mainView.findViewById(R.id.end);
		mRoutesList = (ListView)mainView.findViewById(R.id.route_plans);
		
		
		mTransitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMode = TRANSIT;
				mRouteAdapter.setMode(mMode);
			}
		});
		mDriveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMode = DRIVE;
				mRouteAdapter.setMode(mMode);
			}
		});
		mWalkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMode = WALK;
				mRouteAdapter.setMode(mMode);
			}
		});
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RouteActivity.this.finish();
			}
		});
		
		mSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String srcName = mBeginEdit.getText().toString();
				String destName = mEndEdit.getText().toString();
				MKPlanNode nodeBegin = new MKPlanNode();
				nodeBegin.name = srcName;
				
				MKPlanNode nodeEnd = new MKPlanNode();
				nodeEnd.name = destName;				
				//mMapManager.searchRoute(srcName, destName, 0);
				switch(mMode)
				{
				case WALK:
					mSearch.walkingSearch("北京", nodeBegin, "北京", nodeEnd);
					break;
				case TRANSIT:
					mSearch.transitSearch("北京", nodeBegin, nodeEnd);
					break;
				case DRIVE:
					break;
					
				}
			}
		});
		
		mRouteAdapter = new RouteAdapter(this);
		mRouteAdapter.setMode(mMode);
		mRoutesList.setAdapter(mRouteAdapter);
		
		mSearch = new MKSearch();

		
		FriendsApplication application = (FriendsApplication) this.getApplication();
		//初始化搜索模块
		mSearch.init(application.mBMapManager, new MKSearchListener(){

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				mRouteAdapter.setTransitPlan(arg0);
				mRouteAdapter.notifyDataSetChanged();
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				mRouteAdapter.setWalkingPlan(arg0);
				mRouteAdapter.notifyDataSetChanged();
			}
			
		});
		
		
		
	}
}
