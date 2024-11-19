package com.imbos.chat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class SharedUtils {
	private static final String  sharedName= "setting";
	private Context ctx;
	private static SharedUtils sharedUtil;
	private SharedUtils(Context ctx) {
		this.ctx = ctx;
		
	}
	public static SharedUtils instance(Context ctx){
		synchronized (SharedUtils.class) {
			if(sharedUtil==null || sharedUtil.ctx==null){
				sharedUtil = new SharedUtils(ctx);
			}
		}
		return sharedUtil;
	}
	
	public SharedPreferences getSharedPreferences(){
		return PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
	}
	
	public SharedUtils putString(String key,String value){
		
		SharedPreferences preference = this.getSharedPreferences();
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(key,value);
		editor.commit();
		return this;
	}
	
	public  String getString(String key,String defValue){
		SharedPreferences preference = this.getSharedPreferences();
		return preference.getString(key, defValue);
	}
	public  int getInt(String key,int defValue){
		SharedPreferences preference =this.getSharedPreferences();
		return preference.getInt(key, defValue);
	}
	public SharedUtils putInt(String key,int value){
		SharedPreferences preference = this.getSharedPreferences();
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(key,value);
		editor.commit();
		return this;
	}
	
	public boolean contains(String key){
		SharedPreferences preference = this.getSharedPreferences();
		return preference.contains(key);
	}
	
	
	public SharedUtils remove(String key){
		SharedPreferences preference = this.getSharedPreferences();
		SharedPreferences.Editor editor = preference.edit();
		editor.remove(key);
		editor.commit();
		return this;
	}
	public void clear(){
		SharedPreferences.Editor editor= this.getSharedPreferences().edit();
		editor.clear();
		editor.commit();
	}
}
