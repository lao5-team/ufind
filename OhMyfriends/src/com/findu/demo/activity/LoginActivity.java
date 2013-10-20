package com.findu.demo.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
    private String mQQOpenID = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		loginInQQ();
		
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
						getAppFriends();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            };
            mTencent.login(this, "all", listener);
        }
//        else {
//            mTencent.logout(this);
//            updateLoginButton();
//        }
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
	 * 检查之前是否已经登录过
	 * @return 
	 */
	private boolean isPrevLogined()
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
	private boolean login(String userID, String password, boolean isQQ)
	{
		return false;
	}
	
	/**
	 * 保存用户登录信息
	 * @param userID
	 * @param password
	 * @param isQQ
	 */
	private void saveLoginInfo(String userID, String password, boolean isQQ)
	{
		
	}
	
	private boolean logout()
	{
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
                // azrael 2/1注释掉了, 这里为何要在api返回的时候设置token呢,
                // 如果cgi返回的值没有token, 则会清空原来的token
                // String token = response.getString("access_token");
                // String expire = response.getString("expires_in");
                // String openid = response.getString("openid");
                // mTencent.setAccessToken(token, expire);
                // mTencent.setOpenId(openid);
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

	
}
