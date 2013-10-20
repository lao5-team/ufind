package com.findu.demo.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.findu.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SuTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        
        Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket;
		        try {
		 
		            String para = "#pwd#=123\r\n#username#=abc";
		            int len = para.length();
		            socket = new Socket("192.168.200.7", 8080);
		            socket.setSoTimeout(3000);
		            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
		                    "UTF8"));
		            InputStream ins = socket.getInputStream();
		            StringBuffer sb = new StringBuffer();
		            sb.append("POST /login  HTTP/1.1\r\n");// 注意\r\n为回车换行
		            sb.append("Accept-Language: zh-cn\r\n");
		            sb.append("Connection: Keep-Alive\r\n");
		            sb.append("Host:localhost\r\n");
		            sb.append("Content-Length:"+len+"\r\n");
		            sb.append("Content-Type: application/x-www-form-urlencoded\r\n");
		            sb.append("\r\n");
		            sb.append(para);
		            // 接收Web服务器返回HTTP响应包
		            wr.write(sb.toString());
		            wr.flush();
		             
		            BufferedReader rd = new BufferedReader(new InputStreamReader(ins));
		            String line;
		            while ((line = rd.readLine()) != null) {
		                //System.out.println(line);
		                Log.v("SuTestActivity", line + "\n");
		            }
		        } catch (UnknownHostException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		});
        t.start();
    }
        
}