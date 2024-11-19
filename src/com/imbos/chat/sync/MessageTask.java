package com.imbos.chat.sync;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.ex.NetException;
import com.imbos.chat.net.NetClient;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.JSONUtil;
import com.imbos.chat.util.StringUtil;

public class MessageTask extends SyncTask {
	
	/**
	* @Fields serialVersionUID : TODO
	*/
	private static final long serialVersionUID = 1L;
	public static String RESULT_NAME="resultCode"; 
	
	
	
	public MessageTask(String id) {
		this.syncHandler = null;
	}
	
	@Override
	public void run() {
		
		try{
			if(status!=STATUS_READY)
				return;
			
			
			
			notify(STATUS_START);
			
			String requestStr = JSONUtil.encode(args);
			Config config  = ChatApp.getConfig();
			
			String responseStr = null;
			if(this.file!=null){
				
				responseStr = new NetClient(config.serverHost, config.serverPort,methodName).request(file);
				Map<String,Object> map = (Map<String, Object>) JSONUtil.decode(responseStr);
				boolean result = "0".equals(map.get(RESULT_NAME)+"");
				if(result){
					String link = StringUtil.toString(map.get("link"));
					Map<String,String> p = (Map<String, String>) args;
					p.put("content", "link:"+link);
					requestStr = JSONUtil.encode(args);
					responseStr = new NetClient(config.serverHost, config.serverPort,Constants.MENTHOD.MSG_SEND)
							.setToken(config.saasToken)
							.request(requestStr);
					
					
				}
			}else{
				responseStr = new NetClient(config.serverHost, config.serverPort,
						methodName)
						.setToken(config.saasToken)
						.request(requestStr);
			}
			
			response= (HashMap<String, ?>) JSONUtil.decode(responseStr);
			
			String resultCode = StringUtil.toString(response.get(RESULT_NAME));
			
			if(StringUtil.isINTEGER_NEGATIVE(resultCode)){
				if(syncHandler==null || syncHandler.handle(this)){
					notify(STATUS_FINISH);
				}else {
					notify(STATUS_FAILED);
				}
			}else{
				errorMessage = response.get("i")+"";
				notify(STATUS_FAILED);
			}
				
			
		}catch (NetException ex) {
			ex.printStackTrace();
			notify(STATUS_FAILED);
		}catch (Exception ex){
			ex.printStackTrace();
			notify(STATUS_FAILED);
		}
		
	}
	
	@Override
	protected void notify(int status) {
		if(syncListener!=null){
			syncListener.onChange(this, status);
		}else{
			context.sendBroadcast(
					new Intent(Constants.ACTION_MESSAGE_SEND)
					.putExtra(Intents.EXTRA_ID,id)
					.putExtra(Intents.EXTRA_STATUS,status)
					//.putExtra(Intents.EXTRA_DATA,this)
					);
		}
		this.status = status;
	}
	
}
