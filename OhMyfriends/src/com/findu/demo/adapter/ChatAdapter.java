package com.findu.demo.adapter;

import java.util.ArrayList;

import com.findu.demo.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {

	private ArrayList<String> mMessages = new ArrayList<String>();
	private Activity mContext = null;
	private ArrayList<Integer> mUserIndex = new ArrayList<Integer>();
	public ChatAdapter(Activity activity)
	{
		mContext = activity;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMessages.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view;
		if(0==mUserIndex.get(arg0))
		{
			view = mContext.getLayoutInflater().inflate(R.layout.chat_item_left, null);
		}
		else
		{
			view = mContext.getLayoutInflater().inflate(R.layout.chat_item_right, null);
		}
		TextView tvMessage = (TextView)view.findViewById(R.id.tv_message);
		tvMessage.setText(mMessages.get(arg0));
		return view;
	}
	
	public void addMessage(int index, String message)
	{
		mUserIndex.add(index);
		mMessages.add(message);
	}

}
