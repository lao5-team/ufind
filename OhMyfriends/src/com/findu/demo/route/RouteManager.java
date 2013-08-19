package com.findu.demo.route;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RouteManager {
	String filePath = "data/data/com.findu.demo/routes.dat";
	ArrayList<Route> mRoutes;
	
	public RouteManager()
	{
		mRoutes = new ArrayList<Route>();
		try {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			mRoutes = (ArrayList<Route>) ois.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void save(Route route)
	{
		mRoutes.add(route);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(mRoutes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
//	public Route load()
//	{
//		return null;
//	}
	
	public ArrayList<Route> loadRoutes()
	{
		return mRoutes;
	}

}
