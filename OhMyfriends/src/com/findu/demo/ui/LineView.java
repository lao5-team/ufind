package com.findu.demo.ui;

import com.findu.demo.MyFriendsMain;
import com.rlk.yh.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LineView {

	Context mContext;
	private WindowManager.LayoutParams mRadioParams;
	private View mLineView;
	private LinearLayout mLineLayout;
	private boolean mIsShowing = false;
	private RadioGroup mLineGroup;
	private FrameLayout mContainerFrameLayout;
	private FrameLayout.LayoutParams mLineParam;

	public LineView(MyFriendsMain context) {
		mContext = context;
		mLineView = LayoutInflater.from(mContext).inflate(R.layout.lineview,
				null);
		mLineLayout = (LinearLayout) mLineView.findViewById(R.id.lineview);
		mLineGroup = (RadioGroup) mLineView.findViewById(R.id.linegroup);

		mLineParam = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
	}

	public void showLineView(FrameLayout frame, int num) {
		if (mIsShowing) {
			return;
		}
		for (int i = 0; i < num; i++) {
			RadioButton rb = new RadioButton(mContext);
			rb.setLayoutParams(mRadioParams);
			mLineGroup.addView(rb, mRadioParams);
		}
		mContainerFrameLayout = frame;
		frame.addView(mLineLayout, mLineParam);
		frame.setVisibility(View.VISIBLE);
		frame.bringToFront();
		mIsShowing = true;
	}

	public void hideLineView() {
		if (!mIsShowing) {
			return;
		}
		mContainerFrameLayout.removeView(mLineLayout);
		mContainerFrameLayout.setVisibility(View.GONE);
		mContainerFrameLayout.bringToFront();
		mIsShowing = false;
	}
}
