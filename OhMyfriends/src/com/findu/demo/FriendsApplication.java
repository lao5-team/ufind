package com.findu.demo;


import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class FriendsApplication extends Application {

	private static FriendsApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    
    public static final String strKey = "EF327741BE0E40770FC97D2608419E4700A62E2B";
    
    @Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
    
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), 
                    "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
    
    public static FriendsApplication getInstance() {
		return mInstance;
	}
    
 // �����¼���������������ͨ�������������Ȩ��֤�����
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //��ȨKey����
                Toast.makeText(FriendsApplication.getInstance().getApplicationContext(), 
                        "���� DemoApplication.java�ļ�������ȷ����ȨKey��", Toast.LENGTH_LONG).show();
                FriendsApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}
