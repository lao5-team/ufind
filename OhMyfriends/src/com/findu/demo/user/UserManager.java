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
	
	private UserManager()
	{
		
	}

}
