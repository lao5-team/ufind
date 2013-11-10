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
	 * ���õ�ǰ�û�
	 * @param user
	 */
	public void setCurrentUser(User user)
	{
		mCurrUser = user;
	}
	
	/** ��ȡ��ǰ�û�
	 * @return
	 */
	public User getCurrentUser()
	{
		return mCurrUser;
	}
	
	/**
	 * ��ȡ�����б�
	 * @return
	 */
	public ArrayList<User> getFriends()
	{
		return mFriends;
	}
	
	/**
	 * ��Ӻ���
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
