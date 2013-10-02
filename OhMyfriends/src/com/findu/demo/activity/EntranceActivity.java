/**
 * 
 */
package com.findu.demo.activity;

import com.findu.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author Administrator
 *
 */
public class EntranceActivity extends Activity{

	private Button mEntranceButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entrance_layout);
		mEntranceButton = (Button)findViewById(R.id.btn_entrance);
		mEntranceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EntranceActivity.this, HistoryActiivty.class);
				startActivity(intent);
			}
		});
	}
}
