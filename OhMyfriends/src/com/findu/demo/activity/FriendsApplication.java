package com.findu.demo.activity;


import java.io.File;

import org.androidpn.client.NotificationService;
import org.androidpn.client.XmppManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.findu.demo.manager.MapManager;
import com.findu.demo.service.FindUService;
import com.findu.demo.util.FindUtil;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class FriendsApplication extends Application {


    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    public static String TAG = FriendsApplication.class.getName();
    public static final String strKey = "EF327741BE0E40770FC97D2608419E4700A62E2B";
    public MapManager mMapManager = null;
	private static FriendsApplication mInstance = null;
	public Connection mXmppConnection = null;
//    private NotificationService mNotificationService;
//	private ServiceConnection mConnection = new ServiceConnection() {  
//        public void onServiceConnected(ComponentName className,IBinder localBinder) {  
//        	Log.d(TAG, className.getClassName());
//        	mNotificationService = ((NotificationService.NotificationServiceBinder)localBinder).getService();  
//        	//连接服务时，获取正要进行的plan
//
//        }  
//        public void onServiceDisconnected(ComponentName arg0) {  
//        	mNotificationService = null;  
//        }  
//    };
    @Override
    public void onCreate() {
    	Log.v(TAG, "onCreate");
	    super.onCreate();
		mInstance = this;
		createDirectories();
		initEngineManager(this);
		
	}
    
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
        
//		Intent intent = new Intent(this, FindUService.class);  
//		startService(intent);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
        Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ConnectionConfiguration config = new ConnectionConfiguration("192.168.100.18", 5222);
				config.setSASLAuthenticationEnabled(false);
				mXmppConnection = new XMPPConnection(config);
		        try {
					mXmppConnection.connect();
					Log.v(TAG, "xmpp connected ");
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
        //t.start();
        
        
	}
    
    public static FriendsApplication getInstance() {
		return mInstance;
	}
    
    public Connection getConnection()
    {
    	return mXmppConnection;
    }
    
 // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                FriendsApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
    
    private void createDirectories()
    {
    	File rootDir = new File(FindUtil.ROOT_PATH);
    	if(!rootDir.exists())
    	{
    		rootDir.mkdir();
    		
    		File avatarDir = new File(FindUtil.AVATAR_PATH);
    		avatarDir.mkdir();
    	}
    }
    
//    public XmppManager getXmppManager()
//    {
//    	if(null != mNotificationService)
//    	{
//    		return mNotificationService.getXmppManager();
//    	}
//    	else
//    	{
//    		return null;
//    	}
//    }


}
