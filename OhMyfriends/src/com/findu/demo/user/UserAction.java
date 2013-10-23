package com.findu.demo.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class UserAction {

	private Socket mSocket;
	private BufferedWriter mBw;
	private InputStream mIns;
	private boolean mSocketInitOk = false;
	
	private boolean initSocket(){
		
		try {
			mSocket = new Socket("192.168.0.102", 8080);
			mSocket.setSoTimeout(3000);
			mBw = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(),
					"UTF8"));
			mIns = mSocket.getInputStream();
			mSocketInitOk = true;
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private StringBuffer makeUserLoginProtocolString(int logintype, String qqid,
			String nickname, String picture) {
		
		String msg = "login\r\n" + "#logintype#=" + logintype + "\r\n"
				+ "#qqid#=" + qqid + "\r\n" + "#nickname#=" + nickname + "\r\n"
				+ "#picture#=" + picture;

		int len = msg.length();

		StringBuffer sb = new StringBuffer();
		// sb.append("POST /login  HTTP/1.1\r\n");// 注意\r\n为回车换行
		sb.append("POST /login  HTTP/1.1\r\n");// 注意\r\n为回车换行
		sb.append("Accept-Language: zh-cn\r\n");
		sb.append("Connection: Keep-Alive\r\n");
		sb.append("Host:localhost\r\n");
		sb.append("Content-Length:" + len + "\r\n");
		sb.append("Content-Type: application/x-www-form-urlencoded\r\n");
		sb.append("\r\n");
		sb.append(msg);
		
		return sb;

	}
	
	/*
	 * @param logintype 0 为QQ用户登录， 1为注册用户登录
	 * @param qqid 为QQ的id串，数据库长度为30
	 * @param nickname 用户昵称，没有时传""，数据库长度为30 
	 * @param picture 头像链接， 数据库长度为512
	 */
	public boolean sendUserLoginRequest(int logintype, String qqid,
			String nickname, String picture){
		if(!initSocket()){
			for(int i = 0; i < 3; i++){
				if(initSocket()){
					break;
				}
			}
		}
		if(!mSocketInitOk){
			return false;
		}
		
		StringBuffer sb = makeUserLoginProtocolString(logintype, qqid, nickname, picture);
		try {
			mBw.write(sb.toString());
			mBw.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(mIns));
			String line;
			
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
