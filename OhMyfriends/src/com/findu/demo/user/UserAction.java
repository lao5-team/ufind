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

	private boolean initSocket() {

		try {
			if (mSocket != null) {
				return true;
			}
			System.out.println("123123");

//			mSocket = new Socket("192.168.0.102", 8080);
			mSocket = new Socket("192.168.0.102", 8080);
			System.out.println("456");
			mSocket.setSoTimeout(300);
			mBw = new BufferedWriter(new OutputStreamWriter(
					mSocket.getOutputStream(), "UTF8"));
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
		System.out.println("789");

		return false;
	}

	/*
	 * @param logintype 0 为QQ用户登录， 1为注册用户登录
	 * 
	 * @param qqid 为QQ的id串，数据库长度为30
	 * 
	 * @param nickname 用户昵称，没有时传""，数据库长度为30
	 * 
	 * @param picture 头像链接， 数据库长度为512
	 */
	public boolean sendUserLoginRequest(int logintype, String qqid,String pwd, 
			String nickname, String picture) {
		if (!initSocket()) {
			for (int i = 0; i < 3; i++) {
				if (initSocket()) {
					break;
				}
			}
		}
		if (!mSocketInitOk) {
			return false;
		}

		String msg = "login\r\n" + "#logintype#=" + logintype + "\r\n"
				+ "#qqid#=" + qqid + "\r\n" + "#userpwd#=" + pwd + "\r\n" + "#nickname#=" + nickname + "\r\n"
				+ "#picture#=" + picture;

		StringBuffer sb = makeUserProtocolString("login", msg);
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

	private StringBuffer makeUserProtocolString(String protocol, String msg) {

		int len = msg.length();

		System.out.println(msg);
		StringBuffer sb = new StringBuffer();
		// sb.append("POST /login  HTTP/1.1\r\n");// 注意\r\n为回车换行
		sb.append("POST ").append("/").append(protocol)
				.append("  HTTP/1.1\r\n");
		sb.append("Accept-Language: zh-cn\r\n");
		sb.append("Connection: Keep-Alive\r\n");
		sb.append("Host:localhost\r\n");
		sb.append("Content-Length:" + len + "\r\n");
		sb.append("Content-Type: application/x-www-form-urlencoded\r\n");
		sb.append("\r\n");
		sb.append(msg);

		return sb;

	}

	public boolean sendUserUpdateRequest(int logintype, String id, String pwd,
			String nickname, String picture) {
		if (!initSocket()) {
			for (int i = 0; i < 3; i++) {
				if (initSocket()) {
					break;
				}
			}
		}
		if (!mSocketInitOk) {
			return false;
		}

		String msg = "";
		if (logintype == 0) {
			msg = "update\r\n" + "#logintype#=" + logintype + "\r\n"
					+ "#qqid#=" + id + "\r\n" + "#nickname#=" + nickname
					+ "\r\n" + "#picture#=" + picture;
		} else {
			msg = "update\r\n" + "#logintype#=" + logintype + "\r\n"
					+ "#userid#=" + id + "\r\n" + "#nickname#=" + nickname
					+ "\r\n" + "#picture#=" + picture;
		}

		StringBuffer sb = makeUserProtocolString("update", msg);
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

	/*
	 * @param logintype 0 为QQ用户登录， 1为注册用户登录
	 * 
	 * @param id 为QQ的id串或者用户名id(根据logintype决定)，数据库长度为30
	 * 
	 * @param nickname 用户昵称，没有时传""，数据库长度为30
	 * 
	 * @param picture 头像链接， 数据库长度为512
	 */
	public boolean sendUserAddRequest(int logintype, String id, String pwd,
			String nickname, String picture) {
		if (!initSocket()) {
			for (int i = 0; i < 3; i++) {
				if (initSocket()) {
					break;
				}
			}
		}
		if (!mSocketInitOk) {
			return false;
		}

		String msg = "";
		if (logintype == 0) {
			msg = "adduser\r\n" + "#logintype#=" + logintype + "\r\n"
					+ "#qqid#=" + id + "\r\n" + "#nickname#=" + nickname
					+ "\r\n" + "#picture#=" + picture;
		} else {
			msg = "adduser\r\n" + "#logintype#=" + logintype + "\r\n"
					+ "#userid#=" + id + "\r\n" + "#userpwd#=" + pwd
					+ "\r\n" + "#nickname#=" + nickname + "\r\n" + "#picture#="
					+ picture;
		}

		StringBuffer sb = makeUserProtocolString("adduser", msg);
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
