package com.findu.demo.test;

import java.io.File;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.route.FGeoPoint;
import com.findu.demo.route.Route;
import com.findu.demo.route.RouteManager;

import dalvik.annotation.TestTargetClass;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

public class RouteTest extends AndroidTestCase   {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testAddPoint()
	{
		RouteManager manager = new RouteManager();
		FGeoPoint point = new FGeoPoint(new GeoPoint(0, 0));
		manager.addPoint(point);
		point = new FGeoPoint(new GeoPoint(0, 0));
		manager.addPoint(point);
		assertEquals(2, manager.getPoints().size());
		
	}
	

	
	public void testSavePoints() {
		RouteManager manager = new RouteManager();
		FGeoPoint point = new FGeoPoint(new GeoPoint(0, 0));
		manager.addPoint(point);
		point = new FGeoPoint(new GeoPoint(0, 0));
		manager.addPoint(point);
		
		String filePath = "mnt/sdcard/findu/testpoints.dat";
		manager.savePoints(filePath);
		File file = new File(filePath);
		assertTrue(file.exists());
		
	}

	public void testLoadPoints()
	{
		RouteManager manager = new RouteManager();
		manager.loadPoints("mnt/sdcard/findu/testpoints.dat");
		assertEquals(2, manager.getPoints().size());
	}

	public void testAddRoute()
	{
		RouteManager manager = new RouteManager();
		Route route = new Route("1");
		manager.addRoute(route);
		route = new Route("1");
		manager.addRoute(route);
		assertEquals(2, manager.getRoutes().size());
	}
	
	public void testSaveRoutes()
	{
		RouteManager manager = new RouteManager();
		Route route = new Route("1");
		manager.addRoute(route);
		route = new Route("1");
		manager.addRoute(route);
		assertEquals(2, manager.getRoutes().size());
		
		String filePath = "mnt/sdcard/findu/testroutes.dat";
		manager.saveRoutes(filePath);
		File file = new File(filePath);
		assertTrue(file.exists());
		
	}
	
	public void testLoadRoutes() {
		RouteManager manager = new RouteManager();
		manager.loadPoints("mnt/sdcard/findu/testroutes.dat");
		assertEquals(2, manager.getRoutes().size());
		
		manager.loadPoints("mnt/sdcard/findu/testroutes1.dat");
		assertEquals(0, manager.getRoutes().size());
		
		manager.loadPoints("mnt/sdcard/findu/testroutes.dat");
		assertEquals(2, manager.getRoutes().size());
		
		
		
		
	}
}
