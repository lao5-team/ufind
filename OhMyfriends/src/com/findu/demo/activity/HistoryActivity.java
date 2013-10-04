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
import android.os.Bundle;
import android.util.Xml;
import android.widget.ListView;

/**
 * @author Administrator
 * 点击没去过,拉起PlanActivity
 * 加载XML，显示去过的地方
 */
public class HistoryActivity extends Activity {
	
	private ListView mListView;
	private HistoryAdapter mAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);
		mListView = (ListView)this.findViewById(R.id.listView_history);
		mAdapter = new HistoryAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	
}
