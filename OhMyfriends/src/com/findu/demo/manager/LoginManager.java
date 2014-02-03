package com.findu.demo.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;

import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.util.Log;

import com.findu.demo.activity.FriendsApplication;
import com.findu.demo.activity.MapActivity;
import com.findu.demo.user.User;

public class LoginManager {
	
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

                FriendsApplication.getInstance().mXmppConnection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);
                // registration.setTo(xmppHost);
                // Map<String, String> attributes = new HashMap<String, String>();
                // attributes.put("username", rUsername);
                // attributes.put("password", rPassword);
                // registration.setAttributes(attributes);
                registration.addAttribute("username", mUser.mUsername);
                registration.addAttribute("password", mUser.mPassword);
                FriendsApplication.getInstance().mXmppConnection.sendPacket(registration);

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
		pullOfflineMessage(username, password);
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
	/**
	 * 设置当前用户的头像数据
	 * @param data 头像数据的字节流
	 * @throws XMPPException
	 * @throws IOException
	 */
	public void setAvatarBytes(byte[] data)
			throws XMPPException, IOException {
		VCard vcard = new VCard();
		vcard.load(FriendsApplication.getInstance().mXmppConnection);
		String encodedImage = StringUtils.encodeBase64(data);
		vcard.setAvatar(data, encodedImage);
		vcard.setEncodedImage(encodedImage);
		vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
				+ "</BINVAL>", true);
		vcard.save(FriendsApplication.getInstance().mXmppConnection);

	}
	
	private void pullOfflineMessage(String username, String password)
    {
    	ConnectionConfiguration connConfig = new ConnectionConfiguration(SERVER_IP_ADDRESS, 5222);  
        
    	  connConfig.setSendPresence(false); // where connConfig is object of .  
    	  
    	 XMPPConnection connection = new XMPPConnection(connConfig);  
    	 try {
			connection.connect();
			connection.login(username, password);
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    	     
    	 OfflineMessageManager offlineManager = new OfflineMessageManager(  
    			 connection);  
    	        try {  
    	            Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager  
    	                    .getMessages();  
    	  
    	            System.out.println(offlineManager.supportsFlexibleRetrieval());  
    	            System.out.println("离线消息数量: " + offlineManager.getMessageCount());  
    	  
    	              
    	            Map<String,ArrayList<org.jivesoftware.smack.packet.Message>> offlineMsgs = new HashMap<String,ArrayList<org.jivesoftware.smack.packet.Message>>();  
    	              
    	            while (it.hasNext()) {  
    	                org.jivesoftware.smack.packet.Message message = it.next();  
    	                Log.v(TAG, "收到离线消息, Received from 【" + message.getFrom()  
    	                                + "】 message: " + message.getBody());  
    	                String fromUser = message.getFrom().split("/")[0];  
    	  
    	                if(offlineMsgs.containsKey(fromUser))  
    	                {  
    	                    offlineMsgs.get(fromUser).add(message);  
    	                }else{  
    	                    ArrayList<org.jivesoftware.smack.packet.Message> temp = new ArrayList<org.jivesoftware.smack.packet.Message>();  
    	                    temp.add(message);  
    	                    offlineMsgs.put(fromUser, temp);  
    	                }  
    	            }  
    	  
    	            //在这里进行处理离线消息集合......  
    	            Set<String> keys = offlineMsgs.keySet();  
    	            Iterator<String> offIt = keys.iterator();  
    	            while(offIt.hasNext())  
    	            {  
    	                String key = offIt.next();  
    	                ArrayList<org.jivesoftware.smack.packet.Message> ms = offlineMsgs.get(key);  
    	                for (int i = 0; i < ms.size(); i++) {  
    	                    Log.v(TAG, "offline message " + ms.get(i));  
    	                }  
    	            }  
    	              
    	              
    	            offlineManager.deleteMessages();  
    	        } catch (Exception e) {  
    	            e.printStackTrace();  
    	        }  
    }
	
	
	
}
