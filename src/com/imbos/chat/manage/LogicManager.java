package com.imbos.chat.manage;

import java.util.Date;

import com.imbos.chat.db.DbManager;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.StringUtil;

public class LogicManager {
	
	public static void delFriend(String uid){
		DbManager.delFriend(uid);
		DbManager.delSession(uid);
	}
	
	public static String findUserName(String uid){
		return DbManager.findUserName(uid);
	}
	
	public static String birth2Age(String birth){
		String result = "";
		if(StringUtil.isEmpty(birth))
			return result;
		try {
			Date birthDay = DateUtil.formatStr2Date(birth,"yyyyMd");
			long age = DateUtil.diffYears(new Date(),birthDay);
			result = age>0?Long.toString(age):"";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
