package com.findu.demo.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.findu.demo.R;
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
 * 1.使用QQ帐号登录，
 *   如果之前已经登录过（检查preference或者文件或者数据库是否存在openID），则检查其QQ登录状态，若在线
 * 则登录服务器。
 *   否则如果是新用户，拉起应用或网页登陆QQ，若登录成功，返回一个openID，发送到服务器进行注册，
 * 并保存到本地（写preference或者写文件或数据库）
 * 
 * 2.注销，若使用QQ登录，则服务器和QQ平台都进行注销，否则只在服务器上注销。
 * 
 * 测试步骤：
 * 1.在设置中清除应用程序数据。
 * 2.打开应用，选择QQ账户登录，提示登录成功
 * 3.或者使用findu账户登录，提示登录成功
 * 4.使用发短信方式注册，能够注册并登录成功
 * 5.退出应用，再进入应用，提示登录成功
 * 6.注销用户，再进入应用，需要重新填写用户名密码登录或者使用QQ登录
 */
public class LoginActivity extends Activity {
    private Tencent mTencent;
    private final String APP_ID = "100579301";
    private boolean mIsQQLogin;
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.splash_layout);
		 mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		
		 if(!isNeedLogin())
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
				    	 Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
						 break;
				 }
			 }
		 };
		
	}
	
	/** 
	 * 登录QQ并返回一个openID
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
						Log.v("LoginActivity", values.toString());
						//getAppFriends();
						Toast.makeText(LoginActivity.this, "QQ登录成功", Toast.LENGTH_SHORT).show();
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
	 * 用QQ号进行注册
	 * @param qqOpenID
	 * @return
	 */
	private boolean registerByQQ(String qqOpenID)
	{
		
		return false;
	}
	
	
	/**
	 * 登录到服务器
	 * @param userID 用户名
	 * @param password 密码
	 * @param isQQ 是否是QQ登录
	 * @return
	 */
	private boolean login(final String userID, final String password, final boolean isQQ, final boolean isAutoLogin)
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				UserAction ua = new UserAction();
				if(isQQ)
				{
			        if(ua.sendUserLoginRequest(0, mQQOpenID, mQQNickName, ""))
			        {
			        	//写入登录信息
			        	if(!isAutoLogin)
			        	{
			        		saveLoginInfo(mQQOpenID, "", true);
			        	}
			        	//拉起HomeActivity
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
					if(ua.sendUserLoginRequest(1, userID, password, ""))
			        {
			        	//写入登录信息
			        	if(!isAutoLogin)
			        	{
			        		saveLoginInfo(userID, password, false);
			        	}
			        	//拉起HomeActivity
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

			}
		});
		t.start();

		return false;
	}
	
	/**
	 * 保存用户登录信息
	 * @param userID  用户名
	 * @param password  密码
	 * @param isQQ  是否为QQ登录
	 */
	private void saveLoginInfo(String userID, String password, boolean isQQ)
	{
		Log.v("LoginActivity", "saveLoginInfo " + userID + " " + password);
		SharedPreferences preference = getSharedPreferences("Userinfo",Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putBoolean("isLoginQQ", isQQ);
        if(isQQ)
        {
            edit.putString("openID",userID);
        }
        else
        {
        	edit.putString("username", userID);
        	edit.putString("password",password);
        }
        edit.commit();
	}
	
	private boolean logout()
	{
		mTencent.logout(getApplicationContext());
		return false;
	}
	
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
//            mBaseMessageText.setText("onComplete:");
//            mMessageText.setText(response.toString());
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
    	Log.v("LoginActivity", "base " + base + "msg " +msg);
    }
    
    /**
     * 获取安装应用的好友列表，现在缺乏权限，无法调用
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
				login(mInputUserName.getText().toString(), mInputUserName.getText().toString(), false, false);
			}
		});
    }
    
    private void getQQUserInfo()
    {
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				 JSONObject json = mTencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null,
			                Constants.HTTP_GET);
			    	 Log.v("LoginActivity", json.toString());
			    	 try {
						mQQNickName = json.getString("nickname");
						mQQImgUrl = json.getString("figureurl_qq_2");
						mQQImgUrl.replace("\\/", "\\");
						Message msg = mUIHandler.obtainMessage();
						msg.arg1 = GET_USERINFO_COMPLETE;
						mUIHandler.sendMessage(msg);
						Log.v("LoginActivity", mQQImgUrl);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		});
    	t.start();
    }
    
    private boolean isNeedLogin()
    {
    	SharedPreferences preferences = getSharedPreferences("Userinfo",Context.MODE_PRIVATE);
    	boolean isLoginQQ = preferences.getBoolean("isLoginQQ", false);
    	if(isLoginQQ)
    	{
    		String openID = preferences.getString("openID", null);
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
    		String username = preferences.getString("username", null);
    		String password = preferences.getString("password", null);
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
    
    private void autoLogin()
    {
    	if(mIsQQLogin)
    	{
    		login(mQQOpenID, null, true, true);
    	}
    	else
    	{
    		login(mUsername, mPassword, false, true);
    	}
    	
    }
    
    //向服务器写入用户信息
    private void setUserInfo()
    {
    	
    }
	
}
