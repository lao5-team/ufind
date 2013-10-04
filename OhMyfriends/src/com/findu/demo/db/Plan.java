/**
 * 
 */
package com.findu.demo.db;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Administrator
 * 包含如下信息:
 * 1.名称
 * 2.目的地
 * 3.出行开始时间
 * 4.是否每日提醒
 * 5.朋友列表 网络版时添加
 * 6.完成时长
 */
public class Plan {
	public String name;
	public int destLatitude;
	public int destLongitude;
	public Date startTime;
	public boolean isDaylyRemind;
	public ArrayList friends;
	public int duration;
}
