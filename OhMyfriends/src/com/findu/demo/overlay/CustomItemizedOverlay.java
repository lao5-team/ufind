package com.findu.demo.overlay;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;

/*
 * 地图上的定位点管理类，通过add、remove、update实现点的管理
 */
public class CustomItemizedOverlay {

	private MapView mMapView;
	private Context mContext;
	public LocalItemizedOverlay mItemizedOverlay;
	private Drawable mItemDrawable;
	private ArrayList<OverlayItem> mItems = new ArrayList<OverlayItem>();
	private ItemOverlayOnTapListener mTapListener;
	
	
	public CustomItemizedOverlay(Context context, MapView mapview){
		mMapView = mapview;
		mContext = context;
		mItemDrawable = mContext.getResources().getDrawable(R.drawable.redhat);
		mItemizedOverlay = new LocalItemizedOverlay(mItemDrawable, mMapView);
	}

	public void addItemizedOverlay() {
		if (!mMapView.getOverlays().contains(mItemizedOverlay))
			mMapView.getOverlays().add(mItemizedOverlay);
	}

	public void removeItemizedOverlay(){
		if (mMapView.getOverlays().contains(mItemizedOverlay))
			mMapView.getOverlays().remove(mItemizedOverlay);
	}
	
	public void addOverItem(OverlayItem item){
		if(!mItems.contains(item)){
			mItems.add(item);
			mItemizedOverlay.addItem(item);
		}
	}
	
	
	public void removeOverItem(OverlayItem item){
		if(mItems.contains(item)){
			mItems.remove(item);
			mItemizedOverlay.removeItem(item);
		}
	}
	
	public void updateOverItem(OverlayItem item){
		if(mItems.contains(item)){
			mItems.remove(item);
			mItems.add(item);
			mItemizedOverlay.updateItem(item);
		}
	}
	
	public void setItemOverlayTapListener(ItemOverlayOnTapListener listener){
		mTapListener = listener;
	}
	public class LocalItemizedOverlay extends ItemizedOverlay {

		public LocalItemizedOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);
			mTapListener.onTap(index);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mapview) {
			mTapListener.onTap(pt, mapview);
			return false;
		}

	}

}
