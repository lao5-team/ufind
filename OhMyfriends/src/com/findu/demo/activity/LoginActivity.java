package com.findu.demo.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.findu.demo.R;
import com.findu.demo.manager.UserManager;
import com.findu.demo.user.User;
import com.findu.demo.user.UserAction;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author Administrator
 * 1.ʹ��QQ�ʺŵ�¼��
 *   ���֮ǰ�Ѿ���¼�������preference�����ļ��������ݿ��Ƿ����openID����������QQ��¼״̬��������
 * ���¼��������
 *   ������������û�������Ӧ�û���ҳ��½QQ������¼�ɹ�������һ��openID�����͵�����������ע�ᣬ
 * �����浽���أ�дpreference����д�ļ������ݿ⣩
 * 
 * 2.ע������ʹ��QQ��¼�����������QQƽ̨������ע��������ֻ�ڷ�������ע����
 * 
 * ���Բ��裺
 * 1.�����������Ӧ�ó������ݡ�
 * 2.��Ӧ�ã�ѡ��QQ�˻���¼����ʾ��¼�ɹ�
 * 3.����ʹ��findu�˻���¼����ʾ��¼�ɹ�
 * 4.ʹ�÷����ŷ�ʽע�ᣬ�ܹ�ע�Ტ��¼�ɹ�
 * 5.�˳�Ӧ�ã��ٽ���Ӧ�ã���ʾ��¼�ɹ�
 * 6.ע���û����ٽ���Ӧ�ã���Ҫ������д�û��������¼����ʹ��QQ��¼
 */
public class LoginActivity extends Activity {
    private Tencent mTencent;
    private final String APP_ID = "100579301";
    private boolean mIsQQLogin = false;
    private String mQQOpenID = null;
    private String mQQNickName = null;
    private String mQQImgUrl = null;
    private String mUsername = null;
    private String mPassword = null;
    private EditText mInputUserName;
    private EditText mInputPassword;
    private Button mLogin;
    private Button mRegister;
    private Button mLogout;
    private ImageButton mLoginQQ;
    private Handler mUIHandler;
    private final int LOGIN_QQ_COMPLETE = 0;
    private final int GET_USERINFO_COMPLETE = 1;
    private final int LOGIN_COMPLETE = 2;
    private final int LOGIN_FAILED = 3;
    private final String PREF_USERINFO = "Userinfo";
    private final String PREF_ISLOGIN_QQ = "isLoginQQ";
    private final String PREF_OPENID = "openID";
    private final String PREF_USERNAME = "username";
    private final String PREF_PASSWORD = "password";
    private final String TAG = LoginActivity.class.getName();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.splash_layout);
		 mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
//		 Thread t = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				loginInQQ();
//			}
//		});
//		 t.start();
		 if(!isNeedManualLogin())
		 {
			 autoLogin();
		 }
		 else
		 {
			 setContentView(R.layout.login);
			 initUI();
		 }
		 
		 mUIHandler = new Handler()
		 {
			 @Override
			 public void handleMessage(Message msg)
			 {
				 switch(msg.arg1)
				 {
				     case LOGIN_QQ_COMPLETE:
				    	 getQQUserInfo();
				    	 login(mQQOpenID, null, true, false);
				    	 break;
				     case GET_USERINFO_COMPLETE:
				    	 setUserInfo();
				    	 break;
				     case LOGIN_COMPLETE:
						 Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
						 startActivity(intent);
						 break;
				     case LOGIN_FAILED:
				    	 Toast.makeText(LoginActivity.this, "��¼ʧ��", Toast.LENGTH_SHORT).show();
						 break;
				 }
			 }
		 };
		
	}
	
	/** 
	 * ��¼QQ������һ��openID
	 */
	private void loginInQQ()
	{
        if (!mTencent.isSessionValid()) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    //updateLoginButton();
                	try {
						mQQOpenID = values.getString("openid");
						Log.v(TAG, values.toString());
						//getAppFriends();
						Toast.makeText(LoginActivity.this, "QQ��¼�ɹ�", Toast.LENGTH_SHORT).show();
						Message msg = mUIHandler.obtainMessage();
						msg.arg1 = LOGIN_QQ_COMPLETE;
						mUIHandler.sendMessage(msg);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            };
            mTencent.login(this, "all", listener);
        }
	}
	
	
	
	/**
	 * ��¼���������������QQ��¼������ж�QQ��openID�Ƿ�Ϊ�£�����ע��
	 * @param userID �û���
	 * @param password ����
	 * @param isQQ �Ƿ���QQ��¼
	 * @param isAutoLogin �Ƿ�Ϊ�Զ���¼
	 * @return
	 */
	private boolean login(final String userID, final String password, final boolean isQQ, final boolean isAutoLogin)
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				User user = new User();
				UserAction ua = new UserAction();
				if(isQQ)
				{
			        if(ua.sendUserLoginRequest(0, mQQOpenID, "", mQQNickName, "" ,user))
			        {
			        	//д���¼��Ϣ
			        	if(!isAutoLogin)
			        	{
			        		saveLoginInfo(mQQOpenID, "", true);
			        	}
			        	//����Complete��Ϣ������HomeActivity
			        	Message msg = mUIHandler.obtainMessage();
			        	msg.arg1 = LOGIN_COMPLETE;
			        	mUIHandler.sendMessage(msg);
			        			
			        }
			        else
			        {
			        	Message msg = mUIHandler.obtainMessage();
			        	msg.arg1 = LOGIN_FAILED;
			        	mUIHandler.sendMessage(msg);
			        }
				}
				else
				{
					if(ua.sendUserLoginRequest(1, userID, password, "", "" ,user))
			        {
			        	//д���¼��Ϣ
			        	if(!isAutoLogin)
			        	{
			        		saveLoginInfo(userID, password, false);
			        	}
			        	//����Complete��Ϣ������HomeActivity
			        	Message msg = mUIHandler.obtainMessage();
			        	msg.arg1 = LOGIN_COMPLETE;
			        	mUIHandler.sendMessage(msg);
			        			
			        }
			        else
			        {
			        	Message msg = mUIHandler.obtainMessage();
			        	msg.arg1 = LOGIN_FAILED;
			        	mUIHandler.sendMessage(msg);
			        }
				}
				UserManager.getInstance().setCurrentUser(user);

			}
		});
		t.start();

		return false;
	}
	
	/**
	 * �����û���¼��Ϣ��preference����
	 * @param userID  �û���
	 * @param password  ����
	 * @param isQQ  �Ƿ�ΪQQ��¼
	 */
	private void saveLoginInfo(String userID, String password, boolean isQQ)
	{
		Log.v(TAG, "saveLoginInfo " + userID + " " + password);
		SharedPreferences preference = getSharedPreferences(PREF_USERINFO,Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putBoolean(PREF_ISLOGIN_QQ, isQQ);
        if(isQQ)
        {
            edit.putString(PREF_OPENID,userID);
        }
        else
        {
        	edit.putString(PREF_USERNAME, userID);
        	edit.putString(PREF_PASSWORD,password);
        }
        edit.commit();
	}
	
	/**ע���û�����δʵ��
	 * @return
	 */
	private boolean logout()
	{
		mTencent.logout(getApplicationContext());
		return false;
	}
	
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            showResult("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
//            showResult("onCancel", "");
        }
    }
    
    private void showResult(final String base, final String msg) {
    	Log.v(TAG, "base " + base + "msg " +msg);
    }
    
    /**
     * ��ȡ��װӦ�õĺ����б�����ȱ��Ȩ�ޣ��޷�����
     */
    private void getAppFriends() {
        if (ready()) {
            mTencent.getAppFriends(new BaseApiListener("get_app_friends", false));
        }
    }
    
    private boolean ready() {
        boolean ready = mTencent.isSessionValid()
                && mTencent.getOpenId() != null;
        if (!ready)
            Toast.makeText(this, "login and get openId first, please!",
                    Toast.LENGTH_SHORT).show();
        return ready;
    }
    
    private class BaseApiListener implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            showResult("IRequestListener.onComplete:", response.toString());
            doComplete(response, state);
        }

        protected void doComplete(JSONObject response, Object state) {
            try {
                int ret = response.getInt("ret");
                if (ret == 100030) {
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                                mTencent.reAuth(LoginActivity.this, mScope, new BaseUiListener());
                            }
                        };
                        LoginActivity.this.runOnUiThread(r);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("toddtest", response.toString());
            }

        }

        @Override
        public void onIOException(final IOException e, Object state) {
            showResult("IRequestListener.onIOException:", e.getMessage());
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
            showResult("IRequestListener.onMalformedURLException", e.toString());
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
            showResult("IRequestListener.onJSONException:", e.getMessage());
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {
            showResult("IRequestListener.onConnectTimeoutException:", arg0.getMessage());

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
            showResult("IRequestListener.SocketTimeoutException:", arg0.getMessage());
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
            showResult("IRequestListener.onUnknowException:", arg0.getMessage());
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
            showResult("IRequestListener.HttpStatusException:", arg0.getMessage());
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
            showResult("IRequestListener.onNetworkUnavailableException:", arg0.getMessage());
        }
    }

    private void initUI()
    {
    	Log.v(TAG, "initUI");
    	mInputUserName = (EditText)findViewById(R.id.inputUserName);
    	mInputPassword = (EditText)findViewById(R.id.inputPassword);
    	mLoginQQ = (ImageButton)findViewById(R.id.login_qq);
    	mLoginQQ.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginInQQ();
			}
		});
    	
    	mRegister = (Button)findViewById(R.id.register);
    	mRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
    	
    	mLogin = (Button)findViewById(R.id.login);
    	mLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login(mInputUserName.getText().toString(), mInputPassword.getText().toString(), false, false);
			}
		});
    }
    
    /**
     * ��ȡQQ���û���Ϣ
     */
    private void getQQUserInfo()
    {
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				 JSONObject json = mTencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null,
			                Constants.HTTP_GET);
			    	 Log.v(TAG, json.toString());
			    	 try {
						mQQNickName = json.getString("nickname");
						mQQImgUrl = json.getString("figureurl_qq_2");
						mQQImgUrl.replace("\\/", "\\");
						Message msg = mUIHandler.obtainMessage();
						msg.arg1 = GET_USERINFO_COMPLETE;
						mUIHandler.sendMessage(msg);
						Log.v(TAG, mQQImgUrl);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		});
    	t.start();
    }
    
    /**
     * ����Ƿ���Ҫ�ֶ�login
     * @return
     */
    private boolean isNeedManualLogin()
    {
    	SharedPreferences preferences = getSharedPreferences(PREF_USERINFO,Context.MODE_PRIVATE);
    	mIsQQLogin = preferences.getBoolean(PREF_ISLOGIN_QQ, false);
    	if(mIsQQLogin)
    	{
    		String openID = preferences.getString(PREF_OPENID, null);
    		Log.v(TAG, "isNeedManualLogin: " + "openID=" + openID);
    		if(openID == null)
    		{
    			return true;
    		}
    		else
    		{
    			mQQOpenID = openID;
    			
    		}
    	}
    	else
    	{
    		String username = preferences.getString(PREF_USERNAME, null);
    		String password = preferences.getString(PREF_PASSWORD, null);
    		Log.v(TAG, "isNeedManualLogin: " + "username=" + username + " password=" + password);
    		if(username == null || password == null)
    		{
    			return true;
    		}
    		else
    		{
    			mUsername = username;
    			mPassword = password;
    		}
    	}
    	return false;
    }
    
    /**
     * �����Զ���¼
     */
    private void autoLogin()
    {
    	Log.v(TAG, "autoLogin");
    	if(mIsQQLogin)
    	{
    		login(mQQOpenID, null, true, true);
    	}
    	else
    	{
    		login(mUsername, mPassword, false, true);
    	}
    	
    }
    
    /**
     * �������д���û���Ϣ
     */
    private void setUserInfo()
    {
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data) ;
}
	
}
