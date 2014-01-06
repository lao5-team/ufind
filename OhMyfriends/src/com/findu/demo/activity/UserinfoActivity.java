package com.findu.demo.activity;

import java.io.FileNotFoundException;

import com.findu.demo.R;
import com.findu.demo.manager.UserManager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private ImageView mAvatar;
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
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.d("uri", uri.toString());
			ContentResolver cr = this.getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr
						.openInputStream(uri));
				ImageView imageView = (ImageView) findViewById(R.id.iv01);
				/* 将Bitmap设定到ImageView */
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}


