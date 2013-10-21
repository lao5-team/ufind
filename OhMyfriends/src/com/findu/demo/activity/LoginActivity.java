package com.findu.demo.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.findu.demo.R;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
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
    private String mQQOpenID = null;
    private String mQQNickName = null;
    private String mQQImgUrl = null;
    private EditText mInputUserName;
    private EditText mInputPassword;
    private Button mLogin;
    private Button mRegister;
    private Button mLogout;
    private ImageButton mLoginQQ;
    private Handler mUIHandler;
    private final int LOGIN_COMPLETE = 0;
    private final int GET_USERINFO_COMPLETE = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		 mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		 initUI();
		 mUIHandler = new Handler()
		 {
			 @Override
			 public void handleMessage(Message msg)
			 {
				 switch(msg.arg1)
				 {
				     case LOGIN_COMPLETE:
				    	 getQQUserInfo();
					 break;
				     case GET_USERINFO_COMPLETE:
				    	 setUserInfo();
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
						Log.v("LoginActivity", values.toString());
						//getAppFriends();
						Toast.makeText(LoginActivity.this, "QQ��¼�ɹ�", Toast.LENGTH_SHORT).show();
						Message msg = mUIHandler.obtainMessage();
						msg.arg1 = LOGIN_COMPLETE;
						mUIHandler.sendMessage(msg);
						login(mQQOpenID, null, true);
						//��¼�ɹ�����ת������Activity
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
	private boolean isPrevLogin()
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
	
	private void setUserInfo()
	{
		
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

    private void initUI()
    {
    	mLoginQQ = (ImageButton)findViewById(R.id.login_qq);
    	mLoginQQ.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginInQQ();
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
	
}
