package com.findu.demo.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionInfo;
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
import com.findu.demo.constvalue.ConstValue;
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
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MapActivity extends Activity {

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
	private Button mBtnSearch = null;
	private Button mButtonSave = null;
	private Button mBtnCancel = null;
	private Button mBtnStart = null;
	private Button mBtnFinish = null;
	private boolean mIsBegin = true;
	private GeoPoint mShortcutDest;
	private String mShortcutName;
	private int mShortcutMode;
	private boolean shortcutOpen = false;
	private RelativeLayout mRelativeLayout = null;
	private MKSearch mSearch = null;
	private MKPoiInfo mCurrPoi = null;
	private TextView mTvDestLocation = null;
	class MyPoiOverlay extends PoiOverlay {
	    
	    MKSearch mSearch;

	    public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
	        super(activity, mapView);
	        mSearch = search;
	    }

	    @Override
	    protected boolean onTap(int i) {
	        super.onTap(i);
	        MKPoiInfo info = getPoi(i);
	        if (info.hasCaterDetails) {
	            mSearch.poiDetailSearch(info.uid);
	        }
	        mCurrPoi = info;
	        Log.v(TAG, info.name);
	        mTvDestLocation.setText(info.name);
	        return true;
	    }
	}
	
	MKMapViewListener mMapListener = new MKMapViewListener() {
		@Override
		public void onMapMoveFinish() {
			/**
			 * 在此处理地图移动完成回调
			 * 缩放，平移等操作完成后，此回调被触发
			 */
		}
		
		@Override
		public void onClickMapPoi(MapPoi mapPoiInfo) {
			/**
			 * 在此处理底图poi点击事件
			 * 显示底图poi名称并移动至该点
			 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
			 * 
			 */
			 mCurrPoi = new MKPoiInfo();
			 mCurrPoi.pt = mapPoiInfo.geoPt;
			 mCurrPoi.name = mapPoiInfo.strText;
			 mTvDestLocation.setText(mCurrPoi.name);
			 mMapController.animateTo(mapPoiInfo.geoPt);
		}

		@Override
		public void onGetCurrentMap(Bitmap b) {
			/**
			 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
			 *  可在此保存截图至存储设备
			 */
		}

		@Override
		public void onMapAnimationFinish() {
			/**
			 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
			 */
		}
	};
	
	private BroadcastReceiver mLocationReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
//			if(intent.getAction().equals(MapManager.ACTION_RECEIVE_LOCATION)&&(false == shortcutOpen))
//			{
//				Intent intentShortCut = new Intent();
//				intentShortCut.putExtra("lat", mShortcutDest.getLatitudeE6());
//				intentShortCut.putExtra("long", mShortcutDest.getLongitudeE6());
//				intentShortCut.putExtra("mode", mShortcutMode);
//				intentShortCut.setClass(MyFriendsMain.this, RouteActivity.class);
//				MyFriendsMain.this.startActivity(intentShortCut);
//				shortcutOpen = true;
//			}
			
		}
		
	};
			
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shortcutOpen = false;
		Intent intent = getIntent();
		int lat = intent.getIntExtra("lat", 0);
		int longit = intent.getIntExtra("long", 0);
		mShortcutDest = new GeoPoint(lat, longit);
		mShortcutName = intent.getStringExtra("name");
		mShortcutMode = intent.getIntExtra("mode", 0);
		//Log.v(TAG, "name " + name);
		
		setContentView(R.layout.activity_main);
		mRelativeLayout = (RelativeLayout)findViewById(R.id.main);
		CharSequence titleLable = "MyFriend";
		setTitle(titleLable);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mEditTextDest = (EditText)findViewById(R.id.etx_dest);
		mBtnSearch = (Button)findViewById(R.id.btn_search);
		mBtnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mMapManager.searchRoute(null, CustomRouteOverlay.ROUTE_MODE_WALK);
				//MapActivity.this.startActivity(new Intent(MapActivity.this, RouteActivity.class));
		        mSearch.poiSearchInCity("北京", mEditTextDest.getText().toString());
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
		
		mBtnCancel = (Button)findViewById(R.id.btn_cancel_map);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MapActivity.this.finish();
			}
		});
		
		mBtnStart = (Button)findViewById(R.id.btn_start);
		mBtnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsBegin = !mIsBegin;
				if(mIsBegin)
				{
					mBtnStart.setText(R.string.begin);
				}
				else
				{
					mBtnStart.setText(R.string.end);
				}
			}
		});
		
		mBtnFinish = (Button)findViewById(R.id.btn_finish_location);
		mBtnFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishLocation();
			}
		});
		
		mTvDestLocation = (TextView)findViewById(R.id.tv_dest_location);
		mMapManager = new MapManager(this, mMapView);
		mMapView.regMapViewListener(FriendsApplication.getInstance().mBMapManager, mMapListener);
		//FriendsApplication.getInstance().mMapManager = mMapManager;
		//ViewGroup vp = (ViewGroup) FriendsApplication.getInstance().mMapManager.getMapView().getParent();
		//vp.removeView(FriendsApplication.getInstance().mMapManager.getMapView());
		//mRelativeLayout.addView(FriendsApplication.getInstance().mMapManager.getMapView());
		IntentFilter filter = new IntentFilter();
		filter.addAction(MapManager.ACTION_RECEIVE_LOCATION);
		this.registerReceiver(mLocationReceiver, filter);
		
		// 初始化搜索模块，注册搜索事件监听
        mSearch = new MKSearch();
        mSearch.init(FriendsApplication.getInstance().mBMapManager, new MKSearchListener(){
            //在此处理详情页结果
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
                if (error != 0) {
                    Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapActivity.this, "成功，查看详情页面", Toast.LENGTH_SHORT).show();
                }
            }
            /**
             * 在此处理poi搜索结果
             */
            public void onGetPoiResult(MKPoiResult res, int type, int error) {
                // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                // 将地图移动到第一个POI中心点
                if (res.getCurrentNumPois() > 0) {
                    // 将poi结果显示到地图上
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(MapActivity.this, mMapView, mSearch);
                    poiOverlay.setData(res.getAllPoi());
                    mMapView.getOverlays().clear();
                    mMapView.getOverlays().add(poiOverlay);
                    mMapView.refresh();
                    //当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		break;
                    	}
                    }
                } else if (res.getCityListNum() > 0) {
                	//当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (int i = 0; i < res.getCityListNum(); i++) {
                        strInfo += res.getCityListInfo(i).city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG).show();
                }
            }
            public void onGetDrivingRouteResult(MKDrivingRouteResult res,
                    int error) {
            }
            public void onGetTransitRouteResult(MKTransitRouteResult res,
                    int error) {
            }
            public void onGetWalkingRouteResult(MKWalkingRouteResult res,
                    int error) {
            }
            public void onGetAddrResult(MKAddrInfo res, int error) {
            }
            public void onGetBusDetailResult(MKBusLineResult result, int iError) {
            }
            /**
             * 更新建议列表
             */
            @Override
            public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
//            	if ( res == null || res.getAllSuggestions() == null){
//            		return ;
//            	}
//            	sugAdapter.clear();
//            	for ( MKSuggestionInfo info : res.getAllSuggestions()){
//            		if ( info.key != null)
//            		    sugAdapter.add(info.key);
//            	}
//            	sugAdapter.notifyDataSetChanged();
                
            }
        });
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
		this.unregisterReceiver(mLocationReceiver);
		super.onDestroy();
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
	
	
	public void addShortCut(GeoPoint pt, String name, int routeMode){
	        
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
	        intent.putExtra("mode", routeMode);
	        intent.setClass(MapActivity.this, MapActivity.class);
	        
	        // 设置启动程序
	        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
	        
	        //广播通知桌面去创建
	        this.sendBroadcast(shortcut);
	    }
	private void finishLocation()
	{
		Intent intent = new Intent();
		intent.putExtra(ConstValue.DEST_LOCATION_NAME, mCurrPoi.name);
		intent.putExtra(ConstValue.DEST_LOCATION_LATITUDE, mCurrPoi.pt.getLatitudeE6());
		intent.putExtra(ConstValue.DEST_LOCATION_LONGTITUDE, mCurrPoi.pt.getLongitudeE6());
		setResult(RESULT_OK, intent);
		finish();
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
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event) {  
	   // 是否触发按键为back键  
	   Log.v("MyFriendsMain", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	        return true;  
	    }
	   return false;
	}
	

}
