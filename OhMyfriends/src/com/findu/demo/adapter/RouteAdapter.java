package com.findu.demo.adapter;

import junit.framework.Assert;

import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.findu.demo.activity.FriendsApplication;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RouteAdapter extends BaseAdapter {

	int mRouteNum = 0;
	Activity mActivity = null;
	int mMode = -1; //0 walk, 1 transit, 2 car
	MKTransitRouteResult mTransitResult;
	MKWalkingRouteResult mWalkingResult;
	static final String TAG = RouteAdapter.class.getName();
	public RouteAdapter(Activity activity)
	{
		Assert.assertNotNull(activity);
		mActivity = activity;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		switch(mMode)
		{
		case 0:
			if(null!=mWalkingResult)
			{
				return mWalkingResult.getNumPlan();
			}
			else
			{
				return 0;
			}
			
		case 1:
			if(null!=mTransitResult)
			{
				return mTransitResult.getNumPlan();
			}
			else
			{
				return 0;
			}
		default:
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv = new TextView(mActivity);
		tv.setHeight(64);
		final int pos = position;
		switch(mMode)
		{
		case 0:
			tv.setText(mWalkingResult.getPlan(position).getDistance() + "รื");
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					FriendsApplication.getInstance().mMapManager.setWalkingRoute(mWalkingResult.getPlan(pos));
					mActivity.finish();
				}
			});
			break;
		case 1:
			tv.setText(mTransitResult.getPlan(position).getContent());
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			break;
		}
		
		return tv;
	}
	
	
	public void setTransitPlan(MKTransitRouteResult result)
	{
		//mRouteNum = result.getNumPlan();
		mTransitResult = result;
	}
	
	public void setWalkingPlan(MKWalkingRouteResult result)
	{
		//mRouteNum = result.getNumPlan();
		mWalkingResult = result;
	}
	
	public void setMode(int mode)
	{
		mMode = mode;
	}

}
