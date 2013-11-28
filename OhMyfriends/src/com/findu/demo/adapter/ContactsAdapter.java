package com.findu.demo.adapter;

import java.util.ArrayList;

import junit.framework.Assert;

import com.findu.demo.R;
import com.findu.demo.activity.ContactsActivity;
import com.findu.demo.user.User;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {

	private ArrayList<User> mFriends = null;
	private ArrayList<User> mSelectedFriends = null;
	private Activity mActivity = null;
	public ContactsAdapter(Activity activity)
	{
		Assert.assertNotNull(activity);
		mActivity = activity;
		mSelectedFriends = new ArrayList<User>();
	}
	
	public void setFriends(ArrayList<User> friends)
	{
		mFriends = friends;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null == mFriends)
		{
			return 0;
		}
		else
		{
			return mFriends.size();
		}
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = mActivity.getLayoutInflater().inflate(R.layout.useritem, null);
		TextView tv = (TextView)v.findViewById(R.id.name);
		tv.setText(mFriends.get(position).mNickname);
		final int pos = position;
		if(((ContactsActivity)mActivity).getType() == ContactsActivity.TYPE_SET_FRIENDS)
		{
			CheckBox cb = (CheckBox)v.findViewById(R.id.checkJoin);
			cb.setVisibility(View.VISIBLE);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(true == isChecked)
					{
						mSelectedFriends.add(mFriends.get(pos));
					}
					else
					{
						mSelectedFriends.remove(mFriends.get(pos));
					}
				}
			});
		}
		return v;
	}
	
	public ArrayList<User> getSelectedUsers()
	{
		return (ArrayList<User>) mSelectedFriends.clone();
	}
	

}
