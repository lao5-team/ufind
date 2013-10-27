package com.findu.demo.user;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class User implements Serializable {
	public String mUsername;
	public String mNickname;
	public String mPassword;
	public String mQQOpenID;
	public Bitmap mAvatar;
	public ArrayList<User> mFriends;
}
