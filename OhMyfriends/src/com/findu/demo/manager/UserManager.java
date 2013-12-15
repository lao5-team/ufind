package com.findu.demo.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import junit.framework.Assert;
import android.util.Log;

import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.user.User;


public class UserManager {
	public interface ContactsChangerListener
	{
		void onContactsChange(List<User> users);
	}
	
	private ArrayList<ContactsChangerListener> mCCListener = new ArrayList<UserManager.ContactsChangerListener>();
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
		//mFriends.add(user);
		Roster roster = FriendsApplication.getInstance().getConnection().getRoster();
		try {
			String[] groups = new String[1];
			groups[0] = "friends";
			roster.createEntry(user.mFindUID + "@" + FriendsApplication.getInstance().getConnection().getServiceName(), user.mNickname, groups);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private UserManager()
	{
		mFriends = new ArrayList<User>();
		Roster roster = FriendsApplication.getInstance().getConnection().getRoster();
		
		roster.setSubscriptionMode(SubscriptionMode.manual);
		
		roster.addRosterListener(new RosterListener() {
			
			@Override
			public void presenceChanged(Presence arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, "presenceChanged");
			}
			
			@Override
			public void entriesUpdated(Collection<String> arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, "entriesUpdated");
			}
			
			@Override
			public void entriesDeleted(Collection<String> arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, "entriesDeleted");
			}
			
			@Override
			public void entriesAdded(Collection<String> arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, "entriesAdded");
				loadFriends();
				notifyAllListeners(mFriends);
				
			}
		});
		
		FriendsApplication.getInstance().getConnection().addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, arg0.toString());
			}
		}, null);
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
				user.mFindUID = entry.getUser();
				user.mNickname = entry.getName();
				mFriends.add(user);
			}
		}
		
		
		

		
	}
	
	public void addFriend()
	{
//		Roster roster = FriendsApplication.getInstance().getConnection().getRoster();
//		try {
//			roster.createEntry("rlk@" + FriendsApplication.getInstance().getConnection().getServiceName(), null, null);
//		} catch (XMPPException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void addContactsChangeListener(ContactsChangerListener listener)
	{
		mCCListener.add(listener);
	}
	
	public void removeContactsChangeListener(ContactsChangerListener listener)
	{
		mCCListener.remove(listener);
	}

	private void notifyAllListeners(List<User> users)
	{
		for(ContactsChangerListener listener:mCCListener)
		{
			listener.onContactsChange(users);
		}
	}
}
