package com.findu.demo.route;

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
	Date mBegin;
	Date mEnd;
	ArrayList<GeoPoint> mPoints;
}
