package com.imbos.chat.sync;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.ex.NetException;
import com.imbos.chat.net.NetClient;
import com.imbos.chat.net.NetClient.OnNetListener;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.JSONUtil;
import com.imbos.chat.util.StringUtil;


public class SyncTask implements Runnable,Serializable{
	protected static final long serialVersionUID = 1L;
	public static final String FLAG_SUCCESS="1";
	
	public static final String RESULT_CODE="s";
	public static final String RESULT_MSG="i";
	
	public static final int STATUS_READY=0;
	public static final int STATUS_START=1;
	public static final int STATUS_FINISH=2;
	public static final int STATUS_FAILED=3;
	
	protected String id;
	protected String methodName;
	protected Map<String,?> args;

	protected int status;
	protected OnSyncListener syncListener;
	protected SyncHandler syncHandler = new SyncSqliteHandler();
	
	protected File file;
	protected Context context;
	
	protected String errorMessage;
	protected HashMap<String,?> response; 
	
	public SyncTask(String id) {
		this();
		this.id = id;
		
	}
	
	public SyncTask() {
		context = ChatApp.getContext();
	}
	
	@Override
	public void run() {
		
		try{
			if(status!=STATUS_READY)
				return;
			
			notify(STATUS_START);
			
			HashMap<String,Object> request = new HashMap<String,Object>();
			request.put("f", methodName);
			if(args!=null)
				request.put("args",args);
			
			String requestStr = JSONUtil.encode(request);
			Config config  = ChatApp.getConfig();
			
			String responseStr = null;
			if(this.file!=null){
				responseStr = new NetClient(config.saasHost, config.saasPort,config.saasPath)
						.setToken(config.saasToken)
						.setHeader(args)
						.setOnNetListener(new OnNetListener() {
							@Override
							public void onProgress(int cur, int max, String msg) {
								if(syncListener!=null)
									syncListener.onProgress(cur, max, msg);
							}
						}).request(file);
			}else{
				responseStr = new NetClient(config.saasHost, config.saasPort,
						config.saasPath).setToken(config.saasToken)
						.request(requestStr);
			}
			
			response= (HashMap<String, ?>) JSONUtil.decode(responseStr);
			
			String resultCode = StringUtil.toString(response.get(RESULT_CODE));
			
			if(StringUtil.isINTEGER_NEGATIVE(resultCode)){
				if(syncHandler==null || syncHandler.handle(this)){
					notify(STATUS_FINISH);
				}else {
					notify(STATUS_FAILED);
				}
			}else{
				errorMessage = response.get(RESULT_MSG)+"";
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
	
	
	
	private String debugResponse() {
		
		if(Constants.MENTHOD.FRIENDS.equals(methodName)){
			return "{\"dt\":[\"friend\"],\"s\":1,\"ds\":[[[\"ID\",\"USER\"],[\"1\",\"wxz\"],[\"2\",\"lx\"]],[[\"ID\",\"NAME\"],[\"1\",\"truedev\"],[\"2\",\"ebestmobile\"]]],\"i\":\"成功\"}";

		}else if("".equals(methodName)){
			
		}
		return null;
	}



	protected void notify(int status){
		
		if(syncListener!=null){
			syncListener.onChange(this, status);
		}else{
			context.sendBroadcast(
					new Intent(Intents.ACTION_SYNC)
					.putExtra(Intents.EXTRA_ID,id)
					.putExtra(Intents.EXTRA_STATUS,status));
		}
		this.status = status;
	}
	protected void onProgress(int cur, int max, String msg){
		
		if(syncListener!=null){
			syncListener.onProgress(cur, max,msg);
		}else{
			context.sendBroadcast(
					new Intent(Intents.ACTION_SYNC_PROGRESS)
					.putExtra(Intents.EXTRA_ID,id)
					.putExtra(Intents.EXTRA_TASK_CUR,cur)
					.putExtra(Intents.EXTRA_TASK_MAX,max));
		}
	
	}
	
	public SyncTask setSyncListener(OnSyncListener syncListener) {
		this.syncListener = syncListener;
		return this;
	}
	
	
	
	public String getMethodName() {
		return methodName;
	}


	public SyncTask setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}


	public Map<String, ?> getArgs() {
		return args;
		
	}


	public SyncTask setArgs(Map<String, ?> args) {
		this.args = args;
		return this;
	}
	

	public SyncHandler getSyncHandler() {
		return syncHandler;
	}


	public SyncTask setSyncHandler(SyncHandler syncHandler) {
		this.syncHandler = syncHandler;
		return this;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public HashMap<String, ?> getResponse() {
		return response;
	}
	
	public int getStatus() {
		return status;
	}
	
	public SyncTask setFile(File file) {
		this.file = file;
		return this;
	}
	public File getFile() {
		return file;
	}

	public static interface OnSyncListener{
		void onChange(SyncTask task,int status);
		void onProgress(int cur,int max,String msg);
	}
}
