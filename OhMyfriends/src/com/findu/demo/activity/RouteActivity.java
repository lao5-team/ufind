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
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.adapter.RouteAdapter;
import com.findu.demo.constvalue.ConstValue;
import com.findu.demo.manager.MapManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
	MKSearch mSearch;
	RouteAdapter mRouteAdapter;
	int mMode = ConstValue.WALK;
	
	public static final String TAG = RouteActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initMapSearch();
		//handleShortCut();
	}
	
	public void searchRoute(MKPlanNode nodeBegin, MKPlanNode nodeEnd, int mode)
	{
		switch(mode)
		{
		case ConstValue.WALK:
			mSearch.walkingSearch("北京", nodeBegin, "北京", nodeEnd);
			break;
		case ConstValue.TRANSIT:
			mSearch.transitSearch("北京", nodeBegin, nodeEnd);
			break;
		case ConstValue.DRIVE:
			break;
			
		}
	}
	
	private void initMapSearch()
	{
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
				//mRouteAdapter.setTransitPlan(arg0);
				//mRouteAdapter.notifyDataSetChanged();
				FriendsApplication.getInstance().mMapManager.setTransitRoute(arg0.getPlan(0));
				finish();
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				//Log.v(TAG, "onGetWalkingRouteResult " + arg0.getPlan(0).getDistance());
				mRouteAdapter.setWalkingPlan(arg0);
				mRouteAdapter.notifyDataSetChanged();
			}
			
		});
	}
	
	private void initView()
	{
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
				mMode = ConstValue.TRANSIT;
				mRouteAdapter.setMode(mMode);
			}
		});
		mDriveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMode = ConstValue.DRIVE;
				mRouteAdapter.setMode(mMode);
			}
		});
		mWalkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMode = ConstValue.WALK;
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
				Log.v(TAG, "EditName" + srcName);
				MKPlanNode nodeBegin = new MKPlanNode();
				if(srcName.length() == 0)
				{
					nodeBegin.pt = FriendsApplication.getInstance().mMapManager.getCurrentLocation();
					Log.v(TAG, nodeBegin.pt.getLatitudeE6() + "");
					Log.v(TAG, nodeBegin.pt.getLongitudeE6() + "");
					nodeBegin.name = srcName;
				}
				else
				{
					nodeBegin.name = srcName;
				}
				String destName = mEndEdit.getText().toString();
				MKPlanNode nodeEnd = new MKPlanNode();
				nodeEnd.name = destName;	
				searchRoute(nodeBegin, nodeEnd, mMode);
			}
		});
		
		mRouteAdapter = new RouteAdapter(this);
		mRoutesList.setAdapter(mRouteAdapter);
	}
	
	private void handleShortCut()
	{
		Intent intent = getIntent();
		int longit = intent.getIntExtra("long", 0);
		int latit = intent.getIntExtra("lat", 0);
		final int mode = intent.getIntExtra("mode", 0);
		GeoPoint destPt = new GeoPoint(latit, longit);
		final MKPlanNode nodeBegin = new MKPlanNode();
		nodeBegin.pt = FriendsApplication.getInstance().mMapManager.getCurrentLocation();
				
		final MKPlanNode nodeEnd = new MKPlanNode();
		nodeEnd.pt = destPt;
		
		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>()
		{

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
	        @Override  
	        protected void onPostExecute(String result) {  
	            // 返回HTML页面的内容  
	        	searchRoute(nodeBegin, nodeEnd, mode);
	        }  
			
		};
		task.execute("");
	}
}
