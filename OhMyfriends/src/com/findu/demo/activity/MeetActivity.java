package com.findu.demo.activity;

import com.findu.demo.R;
import com.findu.demo.constvalue.ConstValue;
import com.findu.demo.db.Plan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * @author Administrator
 * 
 */
public class MeetActivity extends Activity {
	
	private Button mBtnBack;
	private Button mBtnSwitch;
	private ViewFlipper mVfpContent;
	private Plan mCurPlan = null;
	private TextView mTvName = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mCurPlan = (Plan)getIntent().getSerializableExtra(ConstValue.OPEN_PLAN);
		initUI();
		
	}
	
	private void initUI()
	{
		setContentView(R.layout.meet);
		mBtnBack = (Button)findViewById(R.id.btn_back);
		mBtnSwitch = (Button)findViewById(R.id.btn_swtich);
		mVfpContent = (ViewFlipper)findViewById(R.id.vfp_content);
		
		mBtnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MeetActivity.this.finish();
			}
		});
		
		mBtnSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mVfpContent.showNext();
			}
		});
		
		mTvName = (TextView)findViewById(R.id.tv_meet_name);
		mTvName.setText(mCurPlan.name);
	}
}
