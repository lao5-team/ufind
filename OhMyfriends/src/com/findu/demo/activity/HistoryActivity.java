package com.findu.demo.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import com.findu.demo.R;
import com.findu.demo.adapter.HistoryAdapter;
import com.findu.demo.db.Plan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author Administrator
 * 点击没去过,拉起PlanActivity
 * 加载XML，显示去过的地方
 */
public class HistoryActivity extends Activity {
	
	private ListView mListView;
	private HistoryAdapter mAdapter;
	private Button mButtonNotArrived;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);
		mButtonNotArrived = (Button)findViewById(R.id.button_not_arrived);
		mButtonNotArrived.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryActivity.this, PlanActivity.class);
				intent.putExtra("type", "Create");
				startActivity(intent);
			}
		});
		mListView = (ListView)this.findViewById(R.id.listView_history);
		mAdapter = new HistoryAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
	
	
}
