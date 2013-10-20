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
    private String mQQOpenID = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		loginInQQ();
		
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
	 * ��QQ�Ž���ע��
	 * @param qqOpenID
	 * @return
	 */
	private boolean registerByQQ(String qqOpenID)
	{
		return false;
	}
	
	/**
	 * ���֮ǰ�Ƿ��Ѿ���¼��
	 * @return 
	 */
	private boolean isPrevLogined()
	{
		return false;
	}
	
	/**
	 * ��¼��������
	 * @param userID �û���
	 * @param password ����
	 * @param isQQ �Ƿ���QQ��¼
	 * @return
	 */
	private boolean login(String userID, String password, boolean isQQ)
	{
		return false;
	}
	
	/**
	 * �����û���¼��Ϣ
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
                // azrael 2/1ע�͵���, ����Ϊ��Ҫ��api���ص�ʱ������token��,
                // ���cgi���ص�ֵû��token, ������ԭ����token
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
