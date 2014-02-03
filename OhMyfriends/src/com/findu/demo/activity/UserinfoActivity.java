package com.findu.demo.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import com.findu.demo.R;
import com.findu.demo.manager.LoginManager;
import com.findu.demo.manager.UserManager;
import com.findu.demo.util.FindUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserinfoActivity extends Activity {

	//nickName
	//uFind_ID
	private ImageView mIvAvatar;
	private TextView mNickName;
	private Button mBtnChooseAvatar;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initUI();
	}
	
	@Override 
	public boolean onKeyDown(int keyCode,KeyEvent event) {  
	   // 是否触发按键为back键  
	   Log.v("SettingActivity", keyCode + "");
	   if (keyCode == KeyEvent.KEYCODE_BACK) {  
	       // 弹出退出确认框  
	        //finish(); 
	        System.exit(0);
	        return true;  
	    }
	   return false;
	}
	
	private void initUI()
	{
		this.setContentView(R.layout.setting_layout);
		mNickName = (TextView) this.findViewById(R.id.tv_NickName);
		mNickName.setText(UserManager.getInstance().getCurrentUser().mNickname);
		mBtnChooseAvatar = (Button)this.findViewById(R.id.btn_choose_avatar);
		mBtnChooseAvatar.setOnClickListener(new OnClickListener(
				) {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent();  
	                /* 开启Pictures画面Type设定为image */  
	                intent.setType("image/*");  
	                /* 使用Intent.ACTION_GET_CONTENT这个Action */  
	                intent.setAction(Intent.ACTION_GET_CONTENT);   
	                /* 取得相片后返回本画面 */  
	                startActivityForResult(intent, 1);  
			}
		});
		
		mIvAvatar = (ImageView)findViewById(R.id.iv_Avatar);
		mIvAvatar.setImageBitmap(FindUtil.getAvatar(UserManager.getInstance().getCurrentUser().mNickname));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.d("uri", uri.toString());
			ContentResolver cr = this.getContentResolver();
			Bitmap bitmap;
			try {
				BitmapFactory.Options opts = new Options();
				opts.inSampleSize = 4;
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri),
						null, opts);
				mIvAvatar.setImageBitmap(bitmap);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				try {
//					LoginManager.getInstance().setAvatarBytes(
//							baos.toByteArray());
					FindUtil.saveAvatar(baos.toByteArray(), UserManager.getInstance().getCurrentUser().mNickname
							+ ".jpg");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}


