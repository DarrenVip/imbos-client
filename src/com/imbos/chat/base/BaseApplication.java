package com.imbos.chat.base;

import com.imbos.chat.db.DatabaseHelper;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.AndroidUtils;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application{
	
	protected static DatabaseHelper DB_HELPER;
	protected static Context ROOT_CONTEXT;
	
	public static String appVersion;
	public static int appVersionCode;
	
	@Override
	public void onCreate() {
		super.onCreate();
		ROOT_CONTEXT = this;
		appVersion = AndroidUtils.getAppVersionName(this);
		appVersionCode = AndroidUtils.getAppVersionCode(this);
	}
	
	public static DatabaseHelper getDbHelper() {
		synchronized (BaseApplication.class) {
			if(DB_HELPER==null){
				
				String uid = getConfig().saasUid;
				String dbname = uid+".db";
				DB_HELPER = new DatabaseHelper(ROOT_CONTEXT,dbname,appVersionCode);
			}
		}
		return DB_HELPER;
	}
	public static Config getConfig() {
		return Config.instance(ROOT_CONTEXT);
	}
	
	public static void resetConfig(){
		getConfig().reset();
		resetDbHelper();
	}
	public static void resetDbHelper(){
		DB_HELPER = null;
	}
	
	public static Context getContext(){
		return ROOT_CONTEXT;
	}
}
