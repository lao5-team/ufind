package com.findu.demo.user;


public class UserManager {

	private static UserManager mInstance = null;
	private User mCurrUser = null;
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
	
	private UserManager()
	{
		
	}

}
