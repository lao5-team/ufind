package com.findu.demo.adapter;

import java.util.ArrayList;

import com.findu.demo.R;
import com.findu.demo.db.Plan;
import com.findu.demo.db.XMLPlanManager;
import com.findu.demo.manager.PlanManager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlansAdapter extends BaseAdapter {

	private ArrayList<Plan> mPlans = null;
	private Activity mContext = null;
	public PlansAdapter(Activity context)
	{
		mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		mPlans = XMLPlanManager.getInstance().getPlans();
		return mPlans.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = mContext.getLayoutInflater().inflate(R.layout.item_plan, null);
		TextView tvPlanName = (TextView)view.findViewById(R.id.tv_plan_name);
		tvPlanName.setText(mPlans.get(position).name);
		return view;
	}

}
