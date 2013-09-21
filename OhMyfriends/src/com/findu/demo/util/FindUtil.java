package com.findu.demo.util;

import java.util.ArrayList;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.route.FGeoPoint;

public class FindUtil {
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
	
	
	
}
