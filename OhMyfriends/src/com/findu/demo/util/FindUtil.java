package com.findu.demo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.route.FGeoPoint;

public class FindUtil {
	public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator 
			+ "FindU";
	
	public static final String AVATAR_PATH = ROOT_PATH + File.separator + "Avatar";
	
	public static void savePointsToFile(int mode, String fileName, ArrayList<FGeoPoint> points)
	{
		//switch
	}
	
	public static ArrayList<FGeoPoint> loadPointsFromFile(String fileName)
	{
		return null;
	}
	
	public static boolean isGeoPointsIdentical(GeoPoint pt1, GeoPoint pt2)
	{
		if(pt1.getLatitudeE6() == pt2.getLatitudeE6() && pt1.getLongitudeE6() == pt2.getLongitudeE6())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static void saveAvatar(byte[] content, String username)
	{
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(AVATAR_PATH + File.separator + username + ".jpg");
			fos.write(content);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Bitmap getAvatar(String username)
	{
		Bitmap bmp = BitmapFactory.decodeFile(AVATAR_PATH + File.separator + username + ".jpg");
		return bmp;
	}
	
	
	
}
