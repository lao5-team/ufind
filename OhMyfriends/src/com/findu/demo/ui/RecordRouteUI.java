package com.findu.demo.ui;

import java.math.MathContext;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKLine;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.findu.demo.FriendsApplication;
import com.findu.demo.MyFriendsMain;
import com.findu.demo.R;
import com.findu.demo.overlay.CustomRouteOverlay;


import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 用于记录路线的管理类，包括终点选择、出行方式选择功能
 * 
 * @author renlikun
 * 
 */
public class RecordRouteUI implements View.OnClickListener, TextWatcher {

	private View mSelectUI;
	private MyFriendsMain mMainContext;
	private boolean mIsMenuShow = false;
	private Button mConfirmButton;
	private Button mCancelButton;
	private RadioGroup mWayGroup;
	private AutoCompleteTextView mEndPosition;
	private int mTravelWay = -1; // 1 walk; 2 bus; 3 drive;
	private MKSearch mSearch = null;
	private ArrayAdapter<String> mSugAdapter = null;
	private LineView mLineView;
	private String BEIJING;
	private FrameLayout mContainerLayout;

	public RecordRouteUI(MyFriendsMain app) {
		mMainContext = app;
		
		BEIJING = app.getResources().getString(R.string.beijing);
		mLineView = new LineView(app);

		mSelectUI = LayoutInflater.from(mMainContext).inflate(
				R.layout.recordactivity, null);
		// mConfirmUI = LayoutInflater.from(mMainContext).inflate(
		// R.layout.recordconfirm, null);

		mSelectUI.setFocusable(true);

		mEndPosition = (AutoCompleteTextView) mSelectUI
				.findViewById(R.id.autoCompleteEnd);

		mEndPosition.addTextChangedListener(this);
		mSugAdapter = new ArrayAdapter<String>(mMainContext,
				android.R.layout.simple_dropdown_item_1line);
		mEndPosition.setAdapter(mSugAdapter);

		mWayGroup = (RadioGroup) mSelectUI.findViewById(R.id.waygroup);
		mWayGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.walk:
					mTravelWay = CustomRouteOverlay.ROUTE_MODE_WALK;
					break;

				case R.id.drive:
					mTravelWay = CustomRouteOverlay.ROUTE_MODE_DRIVE;
					break;

				case R.id.bus:
					mTravelWay = CustomRouteOverlay.ROUTE_MODE_TRANSIT;
					break;

				default:
					mTravelWay = -1;
					break;
				}
			}
		});

		mConfirmButton = (Button) mSelectUI.findViewById(R.id.confirmrecord);
		mConfirmButton.setOnClickListener(this);

		mCancelButton = (Button) mSelectUI.findViewById(R.id.cancelrecord);
		mCancelButton.setOnClickListener(this);

		mSearch = new MKSearch();
		mSearch.init(FriendsApplication.getInstance().mBMapManager,
				new MKSearchListener() {

					@Override
					public void onGetWalkingRouteResult(
							MKWalkingRouteResult arg0, int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetTransitRouteResult(
							MKTransitRouteResult res, int error) {
						if (error == MKEvent.ERROR_ROUTE_ADDR) {
						// TODO Auto-generated method stub
							return;
						}
						if (error != 0 || res == null) {
							Toast.makeText(mMainContext, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
							return;
						}
						int numplan = res.getNumPlan();
						StringBuffer[] routeStrng = new StringBuffer[numplan];
						for (int i = 0; i < numplan; i++) {
							routeStrng[i] = new StringBuffer();
							MKTransitRoutePlan routePlan = res.getPlan(i);
							int numroute = routePlan.getNumRoute();
							MKRoute rou = null;
							int numline = routePlan.getNumLines();
							int indexroute = 0;
							rou = routePlan.getRoute(indexroute);
							boolean walkfirst = isSameGeo(routePlan.getStart(),
									routePlan.getLine(0).getGetOnStop().pt);
							if (!walkfirst) {
								routeStrng[i].append(rou.getTip());
								indexroute++;
								if (indexroute < numroute) {
									rou = routePlan.getRoute(indexroute);
								}
							}
							for (int j = 0; j < Math.max(numline, numroute); j++) {
								MKLine mkLine = null;
								if (j < numline) {
									mkLine = routePlan.getLine(j);
								}
								if (mkLine != null && j < numline - 1) {// 公交线路未遍历完
									boolean same = isSameGeo(mkLine
											.getGetOffStop().pt, routePlan
											.getLine(j + 1).getGetOnStop().pt);
									if (!same) {
										routeStrng[i].append(rou.getTip());
										indexroute++;
										if (indexroute < numroute) {
											rou = routePlan
													.getRoute(indexroute);
										}
									}
								}
								if (mkLine != null) {
									Log.e("rlk",
											"start:" + mkLine.getGetOnStop().pt
													+ " stop:"
													+ mkLine.getGetOffStop().pt);
									routeStrng[i].append(mMainContext
											.getResources().getString(
													R.string.take));
									if (mkLine.getTitle() != null) {
										routeStrng[i].append(mkLine.getTitle());
									}
									MKPoiInfo mkOnPoiInfo = mkLine
											.getGetOnStop();
									MKPoiInfo mkOffPoiInfo = mkLine
											.getGetOffStop();
									int via = mkLine.getNumViaStops();
									String lineInfo = mMainContext.getString(
											R.string.onabus, mkOnPoiInfo.name,
											via, mkOffPoiInfo.name);
									routeStrng[i].append(lineInfo);
								}
								if (mkLine == null && j <= indexroute) {// 剩余最后的步行路段
									routePlan.getRoute(numroute - 1);
									routeStrng[i].append(routePlan.getRoute(j)
											.getTip());
								}
							}
						}
						if (numplan > 1) {
						} else if (numplan == 1) {
							mMainContext.drawTransitRoute(res.getPlan(0));
						}

					}

					@Override
					public void onGetSuggestionResult(MKSuggestionResult res,
							int arg1) {

						if (res == null || res.getAllSuggestions() == null) {
							return;
						}
						mSugAdapter.clear();
						for (MKSuggestionInfo info : res.getAllSuggestions()) {
							if (info.key != null)
								mSugAdapter.add(info.key);
						}
						mSugAdapter.notifyDataSetChanged();
					}

					@Override
					public void onGetPoiResult(MKPoiResult res, int type,
							int error) {

						if (error != 0 || res == null) {
							Toast.makeText(mMainContext, "抱歉，未找到结果",
									Toast.LENGTH_LONG).show();
							return;
						}

						// 将地图移动到第一个POI中心点
						if (res.getCurrentNumPois() > 0) {

						}
					}

					@Override
					public void onGetPoiDetailSearchResult(int type, int error) {

						if (error != 0) {
							Toast.makeText(mMainContext, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mMainContext, "成功，查看详情页面",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onGetDrivingRouteResult(
							MKDrivingRouteResult res, int error) {

						// 起点或终点有歧义，需要选择具体的城市列表或地址列表
						if (error == MKEvent.ERROR_ROUTE_ADDR) {
							// 遍历所有地址
							// ArrayList<MKPoiInfo> stPois =
							// res.getAddrResult().mStartPoiList;
							// ArrayList<MKPoiInfo> enPois =
							// res.getAddrResult().mEndPoiList;
							// ArrayList<MKCityListInfo> stCities =
							// res.getAddrResult().mStartCityList;
							// ArrayList<MKCityListInfo> enCities =
							// res.getAddrResult().mEndCityList;
							return;
						}
						// 错误号可参考MKEvent中的定义
						if (error != 0 || res == null) {
							Toast.makeText(mMainContext, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
							return;
						}

						int numplan = res.getNumPlan();// 路线方案
						StringBuffer[] routeStrng = new StringBuffer[numplan];
						for (int i = 0; i < numplan; i++) {
							MKRoutePlan plan = res.getPlan(i);
							int numroute = res.getPlan(i).getNumRoutes();
							for (int j = 0; j < numroute; j++) {
								String tip = plan.getRoute(j).getTip();
								routeStrng[i] = new StringBuffer();
								routeStrng[i].append(tip);
								routeStrng[i].append('\n');
							}
							
							Log.i("rlk", "num route:"+numroute);
						}

						if(numplan > 1){
							mLineView.showLineView(mContainerLayout, numplan);							
						}else if(numplan == 1){
							mMainContext.drawDriveRoute(res.getPlan(0));
						}
					}

					@Override
					public void onGetBusDetailResult(MKBusLineResult arg0,
							int arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});

	}

	public void showRecordUI(FrameLayout frame) {
		if (mIsMenuShow) {
			return;
		}

		mContainerLayout = frame;
		frame.addView(mSelectUI, new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT));
		frame.setVisibility(View.VISIBLE);
		frame.bringToFront();

		mEndPosition.requestFocus();
		mIsMenuShow = true;
	}

	public void hideRecordUI() {
		if (!mIsMenuShow) {
			return;
		}

		mContainerLayout.setVisibility(View.GONE);
		mContainerLayout.removeView(mSelectUI);
		mIsMenuShow = false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.confirmrecord:
			String endName = mEndPosition.getText().toString();

			MKPlanNode stNode = new MKPlanNode();
			stNode.pt = mMainContext.getCurrentGeoPoint();
			MKPlanNode enNode = new MKPlanNode();
			enNode.name = endName;

			switch (mTravelWay) {
			case CustomRouteOverlay.ROUTE_MODE_WALK:
				mSearch.walkingSearch(BEIJING, stNode, BEIJING, enNode);
				break;

			case CustomRouteOverlay.ROUTE_MODE_DRIVE:
				mSearch.drivingSearch(BEIJING, stNode, BEIJING, enNode);
				break;
				
			case CustomRouteOverlay.ROUTE_MODE_TRANSIT:
				mSearch.transitSearch(BEIJING, stNode, enNode);
				break;
				
			default:
				break;
			}
			hideRecordUI();
			// if(mDoTravel != null && mTravelWay != -1){
			// mDoTravel.selectTravelWay(enNode, mTravelWay);
			// }
			break;

		case R.id.cancelrecord:

			break;
		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence cs, int start, int before, int count) {

		mSearch.suggestionSearch(cs.toString(), "北京");
	}


	/**
	 * 确定终点和出行方式后处理接口
	 * 
	 * @author renlikun
	 * 
	 */
	private boolean isSameGeo(GeoPoint pt1, GeoPoint pt2) {
		if (pt1.getLatitudeE6() == pt2.getLatitudeE6()
				&& pt1.getLongitudeE6() == pt2.getLongitudeE6()) {
			return true;
		} else {
			return false;
		}
	}
	public void takeMeHome(){
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt = mMainContext.getCurrentGeoPoint();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = "天安门";
		mSearch.drivingSearch(BEIJING, stNode, BEIJING, enNode);
	}
}
