package com.findu.demo.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baidu.mapapi.map.MapView;
import com.findu.demo.MyFriendsMain;
import com.findu.demo.R;

public class CommandMenu implements View.OnClickListener {

	private static final int MSG_INITCOMMANDMENU = 1;
	private static final int MSG_SHOWCOMMANDMENU = 2;
	private static final int MSG_ANIMATESHOWCOMMANDMENU = 3;

	MyFriendsMain mActivityMain;
	MapView mMapView;

	// 设备屏幕相关
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;

	// 命令菜单相关
	private MenuHandler mMenuHandler;
	private View mCommandMenuView;
	private ImageView mMenuHeadView;
	private boolean mAnimating = false;

	WindowManager mWindowManager = null;
	
	public CommandMenu(MyFriendsMain activity, MapView mapview) {
		mActivityMain = activity;
		mMapView = mapview;

		SCREEN_WIDTH = mActivityMain.getWindowManager().getDefaultDisplay()
				.getWidth();
		SCREEN_HEIGHT = mActivityMain.getWindowManager().getDefaultDisplay()
				.getHeight();

		mWindowManager = (WindowManager) mActivityMain
				.getSystemService(Context.WINDOW_SERVICE);
		
		mMenuHandler = new MenuHandler(mActivityMain);

		showMenuView();

	}

	/*
	 * @param int what 2负责显示命令菜单
	 * 
	 * @param int arg1 菜单显示的x位置
	 * 
	 * @param int arg2 菜单显示的宽度
	 * 
	 * @param int delay 延时
	 */
	private void sendMenuMessage(int what, int arg1, int arg2, int delay) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		mMenuHandler.sendMessageDelayed(msg, delay);
	}

	private void startMenuAnimation() {
		Message msg = Message.obtain();
		msg.what = MSG_ANIMATESHOWCOMMANDMENU;
		mMenuHandler.sendEmptyMessage(msg.what);
	}
	
	public void removeMenuView(){
		if(mCommandMenuView != null){
			mWindowManager.removeView(mCommandMenuView);
			mCommandMenuView = null;
		}
	}
	
	public void showMenuView(){
		sendMenuMessage(MSG_INITCOMMANDMENU, 0, 0, 10);// 初始化menu
		int arraywid = mActivityMain.getResources()
				.getDrawable(R.drawable.u12_normal).getIntrinsicWidth();
		sendMenuMessage(MSG_SHOWCOMMANDMENU, SCREEN_WIDTH - arraywid, arraywid,
				2000);// 显示menu
	}
	

	private void updateMenuView(int x, int y, int width, int alpha) {

		
		if (mCommandMenuView != null) {
			WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
			windowParams.gravity = Gravity.TOP | Gravity.LEFT;
			windowParams.x = x;
			windowParams.y = y;
			windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
			windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
			windowParams.alpha = alpha;
			windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
					| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
			mWindowManager.updateViewLayout(mCommandMenuView, windowParams);
			return;
			// windowManager.removeView(mCommandMenuView);
		}

		mCommandMenuView = LayoutInflater.from(mActivityMain).inflate(
				R.layout.commandmenu, null);
		mMenuHeadView = (ImageView) mCommandMenuView
				.findViewById(R.id.menuhead);
		mMenuHeadView.setOnClickListener(this);
		WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = x - mMenuHeadView.getWidth();
		windowParams.y = y;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.alpha = alpha;
		windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

		mWindowManager.addView(mCommandMenuView, windowParams);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.menuhead:
			if (mAnimating) {
				break;
			}	
			startMenuAnimation();
			break;

		default:
			break;
		}
	}

	private class MenuHandler extends Handler {

		// 功能菜单相关
		private MyFriendsMain mContext;
		private int mCurrentWidth;
		private int mHeadWidth;
		private static final int ANI_STEP = 25;
		private int mCurrentStep = 1;
		

		MenuHandler(MyFriendsMain context) {
			mContext = context;
		}

		@Override
		public void handleMessage(Message msg) {

			int what = msg.what;
			switch (what) {
			case MSG_INITCOMMANDMENU:// 初始化弹出菜单
				mHeadWidth = mContext.getResources()
						.getDrawable(R.drawable.u12_normal).getIntrinsicWidth();
				// int step = (mContext.mCommandMenuView.getWidth() -
				// mHeadWidth);
				mCurrentWidth = mHeadWidth;

				// mAniStep = step / ANI_STEP;

				break;

			case MSG_SHOWCOMMANDMENU: {
				int local = msg.arg1;
				int width = msg.arg2;
				int alpha = 128;

				updateMenuView(local, SCREEN_HEIGHT >> 1, width, alpha);

			}
				break;

			case MSG_ANIMATESHOWCOMMANDMENU:
				if (mCurrentWidth >= mCommandMenuView.getWidth()) {
					mCurrentWidth = mCommandMenuView.getWidth();
					updateMenuView(SCREEN_WIDTH - mCurrentWidth,
							SCREEN_HEIGHT >> 1, mCurrentWidth, 300);
					mCurrentStep = 1;
					mAnimating = false;
					break;
				}

				mAnimating = true;
				mCurrentWidth += ANI_STEP;
				mCurrentStep++;
				if (mCurrentWidth > mCommandMenuView.getWidth()) {
					mCurrentWidth = mCommandMenuView.getWidth();
				}
				updateMenuView(SCREEN_WIDTH - mCurrentWidth,
						SCREEN_HEIGHT >> 1, mCurrentWidth, 128);
				sendMenuMessage(MSG_ANIMATESHOWCOMMANDMENU, SCREEN_WIDTH
						- mCurrentWidth, mCurrentWidth, 30);// 显示menu

				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	}

}
