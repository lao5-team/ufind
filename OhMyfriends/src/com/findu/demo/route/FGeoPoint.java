package com.findu.demo.route;

import java.io.IOException;
import java.io.Serializable;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class FGeoPoint implements Serializable {
	GeoPoint mPt = null;
	int mRouteID = 0;
	public FGeoPoint(GeoPoint pt)
	{
		mPt = pt;
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
		// populate the fields of 'this' from the data in 'in'...
		// int length = in.readInt();
		// mPoints = new GeoPoint[length];
		// for(int i=0; i<length; i++)
		// {
		// GeoPoint pt = new GeoPoint(in.readInt(), in.readInt());
		// mPoints[i] = pt;
		// }
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException,
			ClassNotFoundException {
		out.writeInt(mPt.getLatitudeE6());
		out.writeInt(mPt.getLongitudeE6());
		out.writeInt(mRouteID);
		// populate the fields of 'this' from the data in 'in'...
		// int length = in.readInt();
		// mPoints = new GeoPoint[length];
		// for(int i=0; i<length; i++)
		// {
		// GeoPoint pt = new GeoPoint(in.readInt(), in.readInt());
		// mPoints[i] = pt;
		// }
	}
}
