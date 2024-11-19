package com.imbos.chat.manage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.imbos.chat.base.BaseApplication;
import com.imbos.chat.net.NetClient;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.JSONUtil;

public class ApiManager {
	
	String TAG = ApiManager.class.getSimpleName();
	
	
	
	public static String host = "192.168.2.142";
	public static String port = "8081";
	public static String site ="/imbos-server";
	public final static String URL_LAODUSER="/api.do"; 
	public final static String URL_SEND="/api.do?action=send";
	public final static String URL_LOGIN="/api.do?action=login";
	public final static String URL_REGISTER="/api.do?action=register";
	public final static String URL_SEND_FILE="/api.do?action=sendFile";
	
	
	public static String RESULT_NAME="resultCode";
	public static String RESULT_CODE_OK;
	
	static{
		host = BaseApplication.getConfig().serverHost;
		port = BaseApplication.getConfig().serverPort;
	}
	
	
	
	public static Map<String,Object> loadUsers(){
		
		String jsonStr = new NetClient(host,port,site+URL_LAODUSER).request("");
		Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(jsonStr);
		return "0".equals(map.get(RESULT_NAME))?map:null;
	}
	public static boolean sendMessage(String id,String from,String to,String content){
		
		Map<String,String> msg = new HashMap<String, String>();
		
		msg.put("id",id);
		msg.put("from",from);
		msg.put("to",to);
		msg.put("content",content);
		msg.put("date",DateUtil.formatDate2Str());
		
		
		String requestStr = JSONUtil.encode(msg);
		
		String jsonStr = new NetClient(host,port,site+URL_SEND).request(requestStr);
		Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(jsonStr);
		return "0".equals(map.get(RESULT_NAME)+"");
	}
	public static boolean sendMessage(String id,String from,String to,File file){
		String jsonStr = new NetClient(host,port,site+URL_SEND_FILE).request(file);
		Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(jsonStr);
		boolean result = "0".equals(map.get(RESULT_NAME)+"");
		if(result){
			return sendMessage(id,from, to, "link:"+map.get("link"));
		}else
			return false;
	}
	public static String  uploadFile(File file){
		String jsonStr = new NetClient(host,port,site+URL_SEND_FILE).request(file);
		Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(jsonStr);
		boolean success = "0".equals(map.get(RESULT_NAME)+"");
		return map.get("link")==null?null:map.get("link").toString();
	}
	public static boolean login(String username,String password,String imei){
		
		Map<String,String> msg = new HashMap<String, String>();
		
		msg.put("username",username);
		msg.put("password",password);
		msg.put("imei",imei);
		
		String requestStr = JSONUtil.encode(msg);
		
		String jsonStr = new NetClient(host,port,site+URL_LOGIN).request(requestStr);
		Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(jsonStr);
		return "0".equals(map.get(RESULT_NAME)+"");
	}
	public static boolean register(String username,String password,String imei){
		
		Map<String,String> msg = new HashMap<String, String>();
		
		msg.put("username",username);
		msg.put("password",password);
		msg.put("imei",imei);
		
		String requestStr = JSONUtil.encode(msg);
		
		String jsonStr = new NetClient(host,port,site+URL_REGISTER).request(requestStr);
		Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(jsonStr);
		return "0".equals(map.get(RESULT_NAME)+"");
	}
	public static File download(String dir,String link){
		String url = "http://"+host+":"+port+site+link;
		 return new NetClient(url.toString()).download(dir);
	}
}
