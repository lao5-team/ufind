package com.findu.demo.manager;

import java.util.ArrayList;

import junit.framework.Assert;

import com.findu.demo.user.User;


public class UserManager {

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
		User tempUser = new User();
		tempUser.mNickname = "Test0";
		tempUser.mFindUID = "10000";
		mFriends.add(tempUser);
	}

}
