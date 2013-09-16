package com.findu.demo.route;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.manager.MapManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;

public class Route extends BroadcastReceiver implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7596959192754391445L;
	public Date mBegin;
	public Date mEnd;
	public ArrayList<Integer> mPointIndices;
	public ArrayList<FGeoPoint> mLocationPoints;
	public boolean mIsFinished = false;
	public static SimpleDateFormat dateFormat =new SimpleDateFormat("yy/MM/dd HH:mm"); 
	public String mName;
	public Route(String name)
	{
		mName = name;
		mBegin = new Date();
		mEnd = new Date();
		mLocationPoints = new ArrayList<FGeoPoint>();
	}
	
	public void addPoint(int index)
	{
		mPointIndices.add(index);
	}
	
	public void addGeoPoint(GeoPoint point)
	{
		mLocationPoints.add(new FGeoPoint(point, new Date(System.currentTimeMillis())));
	}
	
	public void start()
	{
		
		mBegin = new Date(System.currentTimeMillis());
	}
	
	public void end()
	{
		if(!mIsFinished)
		{
			mEnd = new Date(System.currentTimeMillis());
			mIsFinished = true;
		}

	}
	
//	public byte[] getPointsByteArray()
//	{
//		
//		int array[] = new int[mPointIndices.size()];
//		for(int i=0; i<array.length; i++)
//		{
//			array[i] = mPointIndices.get(i);
//		}
//		ObjectOutputStream oos = new 
//		ByteArrayOutputStream bos = new ByteArrayOutputStream(mPointIndices.size()*4);
//		bos.w
//		return array.
//	}
	
	@Override
	public String toString()
	{		
		return  "name: " + mName + "\n" 
				+"beginTime: " + dateFormat.format(mBegin) + "\n"
				+"endTime: " + dateFormat.format(mEnd) + "\n"
				+"isFinished: " + mIsFinished;
	}
		       
			   	
	
	private void writeObject(java.io.ObjectOutputStream out)
		       throws IOException 
	{
		out.writeObject(mBegin);
		out.writeObject(mEnd);
		out.writeObject(mPointIndices);
		out.writeBoolean(mIsFinished);
		out.writeUTF(mName);
		out.writeObject(mLocationPoints);
		//out.writeObject(mBegin);
//		if(null != mPointIndices)
//		{
//			out.writeInt(mPointIndices.length);
//			for(int i=0; i<mPointIndices.length;i ++)
//			{
//			}
//		}
		   
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException 
	{
		// populate the fields of 'this' from the data in 'in'...
//		int length = in.readInt();
//		mPoints = new GeoPoint[length];
//		for(int i=0; i<length; i++)
//		{
//			GeoPoint pt = new GeoPoint(in.readInt(), in.readInt());
//			mPoints[i] = pt;
//		}
		mBegin = (Date)in.readObject();
		mEnd = (Date)in.readObject();
		mPointIndices = (ArrayList<Integer>)in.readObject();
		mIsFinished = in.readBoolean();
		mName = in.readUTF();
		mLocationPoints = (ArrayList<FGeoPoint>)in.readObject();
//		out.writeObject(mBegin);
//		out.writeObject(mEnd);
//		out.writeObject(mPointIndices);
//		out.writeBoolean(mIsFinished);
//		out.writeChars(mName);
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		GeoPoint point = new GeoPoint(arg1.getIntExtra("Lat", 0), arg1.getIntExtra("Long", 0));
		Log.v(MapManager.TAG, "route receive location " + point.getLatitudeE6() + " " + point.getLongitudeE6());
	}

}
