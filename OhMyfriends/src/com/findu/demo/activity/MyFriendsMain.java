package com.findu.demo.activity;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.R;
import com.findu.demo.R.id;
import com.findu.demo.R.layout;
import com.findu.demo.R.menu;
import com.findu.demo.R.string;
import com.findu.demo.location.LocationAbout;
import com.findu.demo.location.LocationChangedListener;
import com.findu.demo.manager.MapManager;
import com.findu.demo.overlay.CustomGraphicsOverlay;
import com.findu.demo.overlay.CustomItemizedOverlay;
import com.findu.demo.overlay.CustomRouteOverlay;
import com.findu.demo.overlay.ItemOverlayOnTapListener;
import com.findu.demo.overlay.LocationOverLay;
import com.findu.demo.overlay.RouteSearchListener;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyFriendsMain extends Activity {

	private final String TAG = "MyFriendsMain";
	private final int DIANMEN_LATITUDE = (int) (39.97923 * 1E6);
	private final int DIANMEN_LONGITUDE = (int) (39.97923 * 1E6);
	MapView mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;

	private int mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;
	private MapManager mMapManager;
	private EditText mEditTextDest = null;
	private Button mButtonSearch = null;
	private Button mButtonSave = null;
	private Button mButtonLoad = null;
	private Button mButtonBegin = null;
	private Button mButtonReset = null;
	private boolean mIsBegin = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int lat = intent.getIntExtra("lat", 0);
		int longit = intent.getIntExtra("long", 0);
		String name = intent.getStringExtra("name");
		Log.v(TAG, "name " + name);
		
		
		setContentView(R.layout.activity_main);
		CharSequence titleLable = "MyFriend";
		setTitle(titleLable);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mEditTextDest = (EditText)findViewById(R.id.editText_dest);
		mButtonSearch = (Button)findViewById(R.id.button_search);
		mButtonSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mMapManager.searchRoute(null, CustomRouteOverlay.ROUTE_MODE_WALK);
				MyFriendsMain.this.startActivity(new Intent(MyFriendsMain.this, RouteActivity.class));
			}
		});
		
		mButtonSave = (Button)findViewById(R.id.button_save);
		mButtonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMapManager.saveRoute();
			}
		});
		
		mButtonLoad = (Button)findViewById(R.id.button_load);
		mButtonLoad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMapManager.loadRoute();
			}
		});
		
		mButtonBegin = (Button)findViewById(R.id.button_begin);
		mButtonBegin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsBegin = !mIsBegin;
				if(mIsBegin)
				{
					mButtonBegin.setText(R.string.begin);
				}
				else
				{
					mButtonBegin.setText(R.string.end);
				}
			}
		});
		
		mButtonReset = (Button)findViewById(R.id.reset);
		mButtonReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMapManager.resetMyLocation();
			}
		});
		
		mMapManager = new MapManager(this, mMapView);
		FriendsApplication.getInstance().mMapManager = mMapManager;
		
	}


	@Override
	protected void onPause() {
		isLocationClientStop = true;
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		isLocationClientStop = false;
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		//mLocationAbout.setLocationStop(true);
		//isLocationClientStop = true;
		mMapManager.destroy();
		mMapView.destroy();
		if(FriendsApplication.getInstance().mBMapManager!=null)
		{
			Log.v(FriendsApplication.TAG, "destroy BMapManager");
			FriendsApplication.getInstance().mBMapManager.destroy();
			FriendsApplication.getInstance().mBMapManager = null;
		}
		super.onDestroy();
		System.exit(0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case R.id.route_walk:
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_WALK;
			//mRouteOverlay.startSearch(mCurrentRunMode);
			break;
			
		case R.id.route_bus:
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_TRANSIT;
			//mRouteOverlay.startSearch(mCurrentRunMode);
			break;
			
		case R.id.route_drive:
			mCurrentRunMode = CustomRouteOverlay.ROUTE_MODE_DRIVE;
			//mRouteOverlay.startSearch(mCurrentRunMode);
			break;
			
		default:
			break;
		}
		
		mMapView.refresh();
		return super.onOptionsItemSelected(item);
	}
	
public void addShortCut(GeoPoint pt, String name){
        
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置属性
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this.getApplicationContext(), R.drawable.ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,iconRes);
 
        // 是否允许重复创建
        shortcut.putExtra("duplicate", false);
        
        //设置桌面快捷方式的图标
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this,R.drawable.ic_launcher);        
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,icon);
        
        //点击快捷方式的操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("lat", pt.getLatitudeE6());
        intent.putExtra("long", pt.getLongitudeE6());
        intent.putExtra("name", name);
        intent.setClass(MyFriendsMain.this, MyFriendsMain.class);
        
        // 设置启动程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        
        //广播通知桌面去创建
        this.sendBroadcast(shortcut);
    }

}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapView extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}
}
