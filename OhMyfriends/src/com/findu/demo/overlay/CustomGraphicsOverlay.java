package com.findu.demo.overlay;

import java.util.ArrayList;

import android.R.integer;

import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;

public class CustomGraphicsOverlay {

	private GraphicsOverlay mGraphicOverLay;
	private ArrayList<Graphic> mOverlayItemArrayList = new ArrayList<Graphic>();
	private MapView mMapView;
	
	public CustomGraphicsOverlay(MapView mapview){
		mMapView = mapview;
		mGraphicOverLay = new GraphicsOverlay(mMapView);
	}
	
	public void setCustomGraphicData(Graphic graphic, boolean invalidate){
		if(graphic == null){
			return;
		}
		mOverlayItemArrayList.add(graphic);
		mGraphicOverLay.setData(graphic);
		if(invalidate){
			mMapView.refresh();
		}
	}
	
	public void removeGraphicData(long item){
		
		mOverlayItemArrayList.remove(item);
		mGraphicOverLay.removeGraphic(item);
	}
	
	public void removeGraphicData(Graphic graphic){
		if(graphic == null){
			return;
		}
		int item = mOverlayItemArrayList.indexOf(graphic);
		mOverlayItemArrayList.remove(item);
	}
	
	public void removeAllData(){
		
		mOverlayItemArrayList.removeAll(mOverlayItemArrayList);
		mGraphicOverLay.removeAll();
		
	}
	
	public void addGraphicOverlay() {
		if (!mMapView.getOverlays().contains(mGraphicOverLay))
			mMapView.getOverlays().add(mGraphicOverLay);
	}
}
