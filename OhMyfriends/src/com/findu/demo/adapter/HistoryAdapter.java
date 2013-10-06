/**
 * 
 */
package com.findu.demo.adapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import com.findu.demo.activity.PlanActivity;
import com.findu.demo.db.Plan;
import com.findu.demo.db.XMLPlanManager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class HistoryAdapter extends BaseAdapter {
	public static String TAG = HistoryAdapter.class.getName();

	private Activity mActivity;
	private ArrayList<Plan> mPlans = null;
	public HistoryAdapter(Activity activity)
	{
		mActivity = activity;
		mPlans = XMLPlanManager.getInstance().getPlans();
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPlans.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TextView tv = new TextView(mActivity);
		//tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		tv.setText(mPlans.get(arg0).name);
		tv.setClickable(true);
		final int index = arg0;
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, PlanActivity.class);
				intent.putExtra("type", "Load");
				
				intent.putExtra("id", mPlans.get(index).id);
				mActivity.startActivity(intent);
			}
		});
		return tv;
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		mPlans = XMLPlanManager.getInstance().getPlans();
		super.notifyDataSetChanged();
	}
}
