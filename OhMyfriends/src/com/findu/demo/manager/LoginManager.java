package com.findu.demo.manager;

import java.util.HashMap;
import java.util.Map;

import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;

import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.activity.MapActivity;
import com.findu.demo.user.User;

public class LoginManager {
	
	private Connection mXmppConnection = null;
	private AccountManager mXmppAccountManager = null;
	private final static String TAG = LoginManager.class.getName();
	private final static String SERVER_IP_ADDRESS = "115.28.59.116";
	public interface LoginListener
	{
		void onReceive(Object result);
	}
	
    private class RegisterTask implements Runnable {

        private User mUser = null;
        private RegisterTask(User user) {
        	mUser = user;
        }

        public void run() {
            Log.i(TAG, "RegisterTask.run()...");


                Registration registration = new Registration();

                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(
                        IQ.class));

                PacketListener packetListener = new PacketListener() {

                    public void processPacket(Packet packet) {
                        Log.d("RegisterTask.PacketListener",
                                "processPacket().....");
                        Log.d("RegisterTask.PacketListener", "packet="
                                + packet.toXML());

                        if (packet instanceof IQ) {
                            IQ response = (IQ) packet;
                            if (response.getType() == IQ.Type.ERROR) {
                                if (!response.getError().toString().contains(
                                        "409")) {
                                    Log.e(TAG,
                                            "Unknown error while registering XMPP account! "
                                                    + response.getError()
                                                            .getCondition());
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                                Log.i(TAG,"Account registered successfully");
                            }
                        }
                    }
                };

                mXmppConnection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);
                // registration.setTo(xmppHost);
                // Map<String, String> attributes = new HashMap<String, String>();
                // attributes.put("username", rUsername);
                // attributes.put("password", rPassword);
                // registration.setAttributes(attributes);
                registration.addAttribute("username", mUser.mUsername);
                registration.addAttribute("password", mUser.mPassword);
                mXmppConnection.sendPacket(registration);

        }
    }


	
	private static LoginManager _instance = null;
	
	private LoginManager()
	{
		//mXmppConnection = FriendsApplication.getInstance().getConnection();
		//mXmppAccountManager = mXmppConnection.getAccountManager();
	}
	
	public static LoginManager getInstance()
	{
		if(null == _instance)
		{
			_instance = new LoginManager();
		}	
		return _instance;	
	}
	
	/**
	 * 使用AccountManager进行注册
	 * @param user
	 * @return
	 */
	public boolean register(User user)
	{
		boolean regmsg = false;//注册消息返回信息，用于显示给用户的提示
		ConnectionConfiguration config = new ConnectionConfiguration(SERVER_IP_ADDRESS, 5222);
		config.setSASLAuthenticationEnabled(false);
		
		Connection xmppConnection = new XMPPConnection(config);
        try {
        	xmppConnection.connect();
			Log.v(TAG, "xmpp connected ");
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//这里有点疑惑，这里使用AccountManger中的createAccount方法和使用Registration的区别是什么
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("name", user.mNickname);
		try {
			mXmppAccountManager = xmppConnection.getAccountManager();
			mXmppAccountManager.createAccount(user.mUsername, user.mPassword, attributes);
			regmsg = true;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return regmsg;
	}
	
	/**使用Registration进行注册
	 * @param user
	 */
	public void register2(User user)
	{
		RegisterTask task = new RegisterTask(user);
		Thread t = new Thread(task);
		t.start();
	}
	
	public boolean login(String username, String password)
	{
		ConnectionConfiguration config = new ConnectionConfiguration(SERVER_IP_ADDRESS, 5222);
		config.setSASLAuthenticationEnabled(false);
		
		Connection xmppConnection = new XMPPConnection(config);
		FriendsApplication.getInstance().mXmppConnection = xmppConnection;
        try {
        	xmppConnection.connect();
        	xmppConnection.login(username, password);
        	mXmppAccountManager = xmppConnection.getAccountManager();
        	User user = new User();
        	user.mUsername = username;
        	user.mPassword = password;
        	user.mNickname = mXmppAccountManager.getAccountAttribute("name");
        	UserManager.getInstance().setCurrentUser(user);
        	return true;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
       
	}
	
	
	
}
