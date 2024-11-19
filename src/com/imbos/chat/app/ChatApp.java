package com.imbos.chat.app;

import java.util.Map;

import android.content.Intent;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.imbos.chat.LoginActivity;
import com.imbos.chat.base.BaseApplication;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.model.EmojiFactory;
import com.imbos.chat.model.EmojiFactory.Emoji;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.SharedUtils;
import com.imbos.client.MessageService;
import com.imbos.client.ServiceManager;


public class ChatApp extends BaseApplication{
	
	private static final String TAG = ChatApp.class.getSimpleName();
	
	private static Map<String,Emoji> emojis;
	
	
	public static String updateAppVersion;
	public static String updateAppUrl;
	
	
	private AsyncImageLoader asyncImageLoader;
	private AsyncImageLoader asyncTempLoader;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initJPush();
		login();
		getEmojis();
		asyncImageLoader = new AsyncImageLoader(this);
		asyncTempLoader= new AsyncImageLoader(this);
	}
	
	public static Map<String,Emoji> getEmojis(){
		if(emojis==null){
			emojis = new EmojiFactory(ROOT_CONTEXT).builder();
		}
		return emojis;
	}
	
	public AsyncImageLoader getAsyncImageLoader() {
		return asyncImageLoader;
	}
	public AsyncImageLoader getAsyncTempLoader() {
		return asyncTempLoader;
	}
	public void login(){
		resetDbHelper();
		if(SharedUtils.instance(this)
				.contains(Constants.XMPP_PASSWORD)){
			String uid = ChatApp.getConfig().saasUid;
			loginJPush(uid);
		}
	}
	/**
	 * 注销用户
	 */
	public void logout(){
		
	
		MessageService.stopForeground(this);
		new ServiceManager(this).stopService();
		
		logoutJPush();
		ChatApp.getConfig().reset();
		SharedUtils.instance(this)
			.remove(Constants.XMPP_PASSWORD);
		
		
		startActivity(new Intent(this, LoginActivity.class)
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	
	private void initJPush(){
		 JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 
         
	}
	
	private void loginJPush(String uid){
		Log.d(TAG, "jpush:login"+uid);
		JPushInterface.resumePush(this);
		JPushInterface.setAliasAndTags(getApplicationContext(),uid, null);
	}
	private void logoutJPush(){
		JPushInterface.stopPush(this);
		//JPushInterface.setAliasAndTags(getApplicationContext(),"stop", null);
	}
	
}
