package com.findu.demo.manager;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import junit.framework.Assert;
import android.util.Log;

import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.user.User;


public class UserManager {
	public static String TAG = UserManager.class.getName();
	private static UserManager mInstance = null;
	private User mCurrUser = null;
	private ArrayList<User> mFriends = null;
	public static UserManager getInstance() {
		// TODO Auto-generated method stub
		if(mInstance == null)
		{
			mInstance = new UserManager();
		}
		return mInstance;
	}
	
	/**
	 * 设置当前用户
	 * @param user
	 */
	public void setCurrentUser(User user)
	{
		mCurrUser = user;
	}
	
	/** 获取当前用户
	 * @return
	 */
	public User getCurrentUser()
	{
		return mCurrUser;
	}
	
	/**
	 * 获取好友列表
	 * @return
	 */
	public ArrayList<User> getFriends()
	{
		return mFriends;
	}
	
	/**
	 * 添加好友
	 * @param user
	 */
	public void addFriend(User user)
	{
		Assert.assertNotNull(user);
		mFriends.add(user);
	}
	
	
	private UserManager()
	{
		mFriends = new ArrayList<User>();
//		User tempUser = new User();
//		tempUser.mNickname = "Test0";
//		tempUser.mFindUID = "10000";
//		mFriends.add(tempUser);
	}
	
	public void loadFriends()
	{
		mFriends.clear();
		Roster roster = FriendsApplication.getInstance().getConnection().getRoster();
		Collection<RosterGroup> groups = roster.getGroups();
		for(RosterGroup group:groups)
		{
			Log.v(TAG, "group: " + group.getName());
			Collection<RosterEntry> entries = group.getEntries();
			for(RosterEntry entry:entries)
			{
				Log.v(TAG, "user: " + entry.getName());
				User user = new User();
				user.mNickname = entry.getName();
				mFriends.add(user);
			}
		}
		
		
	}

}
