package com.findu.demo.route;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class FGeoPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2590357782365752482L;
	GeoPoint mPt = null;
	int mRouteID = 0;
	Date mDate = null;
	public FGeoPoint(GeoPoint pt, Date date)
	{
		mPt = pt;
		mDate = date;
	}
	
	public GeoPoint getGeoPoint()
	{
		return mPt;
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		GeoPoint pt = new GeoPoint(in.readInt(), in.readInt());
		mPt = pt;
		mRouteID = in.readInt();
		mDate = (Date)in.readObject();
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException,
			ClassNotFoundException {
		out.writeInt(mPt.getLatitudeE6());
		out.writeInt(mPt.getLongitudeE6());
		out.writeInt(mRouteID);
		out.writeObject(mDate);
	}
}
