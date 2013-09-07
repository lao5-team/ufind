package com.findu.demo.route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class RouteManager {
	String filePath = "mnt/sdcard/findu/routes.db";
	String pointPath = "mnt/sdcard/findu/points.txt";
	String point1Path = "mnt/sdcard/findu/points1.txt";
	ArrayList<Route> mRoutes;
	ArrayList<FGeoPoint> mPoints;
	//Route mCurrentRoute = null;
	SQLiteDatabase mDB;
	public static String TAG = RouteManager.class.getName();
	public RouteManager()
	{

		mRoutes = new ArrayList<Route>();
		mPoints = new ArrayList<FGeoPoint>();
		
		//initFileAndDB();

	}
	
	private void initFileAndDB()
	{
		File fileDir = new File("mnt/sdcard/findu");
		if(!fileDir.exists())
		{
			try {
				fileDir.mkdir();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		File filePoints = new File(pointPath);
		if(!filePoints.exists())
		{
			try {
				fileDir.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			mRoutes = (ArrayList<Route>) ois.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		///////////
		File dbFile = new File(filePath);
		
		if(dbFile.exists())
		{
			mDB = SQLiteDatabase.openOrCreateDatabase("mnt/sdcard/findu/routes.db", null);
		}
		else
		{
			mDB = SQLiteDatabase.openOrCreateDatabase("mnt/sdcard/findu/routes.db", null);
			mDB.execSQL("CREATE TABLE routes (" + "_id INTEGER PRIMARY KEY,"
					+ "name TEXT," + "startTime TEXT,"
					+ "endTime TEXT," + "pointIndex LONG,"
					 + ");");
		}
	}
	
	
	public void saveRoutetoDB(Route route)
	{
		mRoutes.add(route);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(mRoutes);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//将point写入文本文件
	public void addPoint(FGeoPoint point)
	{
		mPoints.add(point);
		
		//
		FileOutputStream fos;
		try {
			FileWriter fWriter = new FileWriter(pointPath, true);
			SimpleDateFormat formatter;
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			formatter = new SimpleDateFormat("HH:mm:ss");
			String strTime = formatter.format(curDate);
			fWriter.append(strTime+"\n");
			fWriter.append(point.mPt.getLatitudeE6() + "\n");
			fWriter.append(point.mPt.getLongitudeE6() + "\n");
			fWriter.append("\n");
			fWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void savePoints(String fileName)
	{
		File file = new File(fileName);
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileOutputStream fos;
		ObjectOutputStream oos;
		try {
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(mPoints);
			oos.close();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
	}
	
	public void loadPoints(String fileName)
	{
		File file = new File(fileName);
		if(file.exists())
		{
			try {
				FileInputStream fis;
				ObjectInputStream ois;
				try {
					fis = new FileInputStream(point1Path);
					ois = new ObjectInputStream(fis);
					mPoints = (ArrayList<FGeoPoint>) ois.readObject();
					ois.close();
					fis.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<FGeoPoint> getPoints()
	{
		return (ArrayList<FGeoPoint>) mPoints.clone();
	}
	
	public void addRoute(Route route)
	{
		mRoutes.add(route);
	}
	
	public void saveRoutes(String fileName)
	{
		
	}
	
	public void loadRoutes(String fileName)
	{
		//return (ArrayList<Route>) mRoutes.clone();
	}
	
	public ArrayList<Route> getRoutes()
	{
		return (ArrayList<Route>) mRoutes.clone();
	}
	
	public Route loadRoutefromDB(String name)
	{
		return null;
	}
	
	public void savePoint(GeoPoint point)
	{
		Log.v("RouteManager", "savePoint");
		FGeoPoint fpoint = new FGeoPoint(point);
		mPoints.add(fpoint);
		FileOutputStream fos;
		try {
			FileWriter fWriter = new FileWriter(pointPath, true);
			
			//fos = new FileOutputStream(pointPath);
			//ObjectOutputStream oos = new ObjectOutputStream(fos);
			//oos.writeObject(mPoints);
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			String strTime = formatter.format(curDate);
//			oos.writeChars(strTime+"\n");
//			oos.writeChars(point.getLatitudeE6() + "\n");
//			oos.writeChars(point.getLongitudeE6() + "\n");
//			oos.writeChars("\n");
//			oos.close();
//			fos.close();
			fWriter.append(strTime+"\n");
			fWriter.append(point.getLatitudeE6() + "\n");
			fWriter.append(point.getLongitudeE6() + "\n");
			fWriter.append("\n");
			fWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GeoPoint[] loadPointtxt()
	{
		Log.v("RouteManager", "loadPoint");
		ArrayList<FGeoPoint> points = new ArrayList<FGeoPoint>();
		FileOutputStream fos;
		try {
			FileReader fReader = new FileReader(point1Path);
			BufferedReader bfReader = new BufferedReader(fReader);
			while(true)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				String str = bfReader.readLine();
				if(str == null)
				{
					break;
				}
				Date date = formatter.parse(str);
				str = bfReader.readLine();
				if(str == null)
				{
					break;
				}
				int lat = Integer.valueOf(str);
				str = bfReader.readLine();
				if(str == null)
				{
					break;
				}
				int longtitude = Integer.valueOf(str);
				GeoPoint point = new GeoPoint(lat, longtitude);
				FGeoPoint fpoint = new FGeoPoint(point, date);
				if(points.size()>=1&&(point.getLatitudeE6()!=points.get(points.size()-1).mPt.getLatitudeE6())&&point.getLatitudeE6()!=0)
				{
					points.add(fpoint);
				}
				else if(points.size() == 0)
				{
					points.add(fpoint);
				}
				str = bfReader.readLine();
				if(str == null)
				{
					break;
				}
			}
			Log.v(TAG, "points num " + points.size());
			fReader.close();
			GeoPoint[] gpoints = new GeoPoint[points.size()];
			for(int i=0; i<points.size(); i++)
			{
				gpoints[i] = points.get(i).mPt;
			}
			return gpoints;
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			return null;
		}
	}
	
	public int getPointNum()
	{
		return mPoints.size();
	}
	
	public int getRouteNum()
	{
		return mRoutes.size();
	}
	
//	public Route load()
//	{
//		return null;
//	}
	
//	public ArrayList<Route> loadRoutes()
//	{
//		return mRoutes;
//	}

//	public Route createRoute()
//	{
//		Route route = new Route();
//		return route;
//	}
	
//	public void endRoute()
//	{
//		mCurrentRoute.mIsFinished = true;
//	}
}
