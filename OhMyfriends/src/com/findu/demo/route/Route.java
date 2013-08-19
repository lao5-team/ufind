package com.findu.demo.route;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.text.format.Time;

public class Route implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7596959192754391445L;
	public Date mBegin;
	public Date mEnd;
	public GeoPoint[] mPoints;
	
	private void writeObject(java.io.ObjectOutputStream out)
		       throws IOException 
	{
		     // write 'this' to 'out'...
		//out.writeObject(mBegin);
		if(null != mPoints)
		{
			out.writeInt(mPoints.length);
			for(int i=0; i<mPoints.length;i ++)
			{
				out.writeInt(mPoints[i].getLatitudeE6());
				out.writeInt(mPoints[i].getLongitudeE6());
			}
		}
		   
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException 
	{
		// populate the fields of 'this' from the data in 'in'...
		int length = in.readInt();
		mPoints = new GeoPoint[length];
		for(int i=0; i<length; i++)
		{
			GeoPoint pt = new GeoPoint(in.readInt(), in.readInt());
			mPoints[i] = pt;
		}
	}

}
