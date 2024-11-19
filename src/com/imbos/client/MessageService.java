package com.imbos.client;
/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.imbos.chat.LoginActivity;
import com.imbos.chat.MainActivity;
import com.imbos.chat.R;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.manage.ApiManager;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.LogUtil;
import com.imbos.chat.util.SharedUtils;


/**
 * Service that continues to run in background and respond to the push 
 * notification events from the server. This should be registered as service
 * in AndroidManifest.xml. 
 * 
 * @author 
 */
public class MessageService extends Service {

    private static final String TAG = LogUtil
            .makeLogTag(MessageService.class);


    public static final String ACTION_LAUNCH_TOP_TASK = "imbos.action.launch_top_task";
    public static final String ACTION_FOREGROUND = "imbos.action.FOREGROUND";
	public static final String ACTION_BACKGROUND = "imbos.action.BACKGROUND";
	
    private TelephonyManager telephonyManager;
    private NotificationManager notificationManager;
    private BroadcastReceiver notificationReceiver;

    private BroadcastReceiver connectivityReceiver;

    private PhoneStateListener phoneStateListener;

    private ExecutorService executorService;

    private TaskSubmitter taskSubmitter;

    private TaskTracker taskTracker;

    private XmppManager xmppManager;

    private String deviceId;

    
    private static final Class<?>[] mStartForegroundSignature = new Class[] {
		int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };



	
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	private Method mStartForeground;
	private Method mStopForeground;
	
	
    public MessageService() {
        connectivityReceiver = new ConnectivityReceiver(this);
        phoneStateListener = new PhoneStateChangeListener(this);
        executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);
    }
   
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()...");
        
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
        try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}
        
        deviceId = telephonyManager.getDeviceId();
        
        SharedUtils.instance(getApplication())
        	.putString(Constants.DEVICE_ID, deviceId);
        
        Log.d(TAG, "deviceId=" + deviceId);

        
        startForeground(R.string.app_name,createNotification());
        
        
        xmppManager = new XmppManager(this);

        taskSubmitter.submit(new Runnable() {
            public void run() {
                MessageService.this.start();
            }
        });
        
        
    }
    public static void  startForeground(Context context){
		context.startService(new Intent(context, MessageService.class)
				.setAction(ACTION_FOREGROUND));
    }
    public static void  stopForeground(Context context){
		context.startService(new Intent(context, MessageService.class)
				.setAction(ACTION_BACKGROUND));
    }
    /**
	 * 启动为前台进程
	 * 
	 * @param id
	 * @param notification
	 */
	public void startForegroundCompat(int id, Notification notification) {
		
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}
		// 使用新的API
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			try {
				mStartForeground.invoke(this, mStartForegroundArgs);
			} catch (InvocationTargetException e) {
				Log.e(TAG, "Unable to invoke startForeground", e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Unable to invoke startForeground", e);
			}
			return;
		}

		// 如果不存在，则使用旧的API
		this.setForeground(true);
		notificationManager.notify(id, notification);
	}
	/**
	 * 停止启动为前台进程
	 * 
	 * @param id
	 */
	void stopForegroundCompat(int id) {
		// 使用新的API
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			try {
				mStopForeground.invoke(this, mStopForegroundArgs);
			} catch (InvocationTargetException e) {
				Log.e(TAG, "Unable to invoke stopForeground", e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Unable to invoke stopForeground", e);
			}
			return;
		}

		// 如果不存在，则使用旧的API
		notificationManager.cancel(id);
		setForeground(false);
	}
	/**
	 * 创建一个Notification
	 * 
	 * @param text
	 * @return
	 */
	public static  Notification createNotification(Context ctx ,CharSequence text) {
		Notification notification = new Notification(R.drawable.icon,
				text, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_FOREGROUND_SERVICE;
		
		return notification;
	}
	
	public static Notification bindNotifiyOnClickEvent(Context ctx,String text,boolean needSound,boolean needVibrate,PendingIntent pendingIntent){
		Notification notification = createNotification(ctx,text);
		notification.setLatestEventInfo(ctx,ctx.getText(R.string.app_name),
						text,pendingIntent);
		//声音和震动
        if (needSound && needVibrate) {
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        } else if (needSound){
            notification.defaults = Notification.DEFAULT_SOUND;
        } else if (needVibrate) {
            notification.defaults = Notification.DEFAULT_VIBRATE;
        }
		
		NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, notification);
		return notification;
		
	} 
	
	public static void bindNotifiyOnClickEvent(Context ctx){
		
		Intent tagetIntent = new Intent(ctx,MessageService.class)
		.setAction(ACTION_LAUNCH_TOP_TASK);
	
		PendingIntent pendingIntent =  PendingIntent.getService(ctx,0,tagetIntent, 0);
		Notification notification = createNotification(ctx,ctx.getString(R.string.GENERAL_RUN));
		notification.setLatestEventInfo(ctx,ctx.getText(R.string.app_name),
				ctx.getString(R.string.GENERAL_RUN),pendingIntent);
	
		NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, notification);
		
	} 
	
	
	
	private Notification createNotification(){
		
		Intent tagetIntent = new Intent(this,MessageService.class)
			.setAction(ACTION_LAUNCH_TOP_TASK);
		
		PendingIntent pendingIntent =  PendingIntent.getService(this,0,tagetIntent, 0);
		
		Notification notification = createNotification(this,getString(R.string.GENERAL_RUN));
		notification.setLatestEventInfo(this,this.getText(R.string.app_name),
				getString(R.string.GENERAL_RUN),pendingIntent);
		
		return notification;
	}
	
	
	/**
	 * 启动本程序中Task中处于栈顶的Activity
	 */
	private void startTopActivity() {
		ActivityManager am = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(Integer.MAX_VALUE);
		RunningTaskInfo taskInfo = null;
		for (int i = 0; i < list.size(); i++) {
			taskInfo = list.get(i);
			if (taskInfo.baseActivity.getPackageName().equals(
					getApplication().getPackageName())) {
				ComponentName targetActivity = taskInfo.topActivity;

				Intent intent = new Intent();
				intent.setComponent(targetActivity);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				return;
			}

		}
		startActivity(new Intent(this,MainActivity.class)
			.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if(intent==null)
    		return super.onStartCommand(intent, flags, startId);
    	
    	String action = intent.getAction();
    	
    	if (ACTION_FOREGROUND.equals(action)) {
			CharSequence text = getText(R.string.app_name);
			Notification notification = createNotification();
			startForegroundCompat(R.string.app_name,
					notification);

		} else if (ACTION_BACKGROUND.equals(action)) {
			stopForegroundCompat(R.string.app_name);
		} else if (ACTION_LAUNCH_TOP_TASK.equals(action)){
			startTopActivity();
		}
    	
    	if(Constants.ACTION_SEND.equals(action)){
    		MessageTask task = new MessageTask(intent.getStringExtra(Constants.EXTRA_MSG_ID),
    				intent.getStringExtra(Constants.EXTRA_MSG_FROMS),
    				intent.getStringExtra(Constants.EXTRA_MSG_TOS),
    				intent.getStringExtra(Constants.EXTRA_MSG_CONTENT),
    				intent.getStringExtra(Constants.EXTRA_MSG_DATE));
    	   
    		
    		DbManager.saveMessage(task.id, task.froms, task.tos, task.content, task.date,
        			Constants.MessageStatus.UPLOADING.ordinal());
    		this.getXmppManager().addTask(task);
    	} 
    	return START_STICKY;
    }
    
 
    
    private class MessageTask implements Runnable {

        
        private String id;
        private String tos;
        private String froms;
        private String content;
        private String date;
        

        public MessageTask(String id,String froms, String tos, String content, String date) {
        	this.id = id;
            this.froms = froms;
            this.tos = tos;
            this.content = content;
            this.date = date;
        }

        public void run() {
            Log.i(TAG, "MessageTask.run()...");
            try{
                
            	if(content.startsWith(Constants.PREFIX_FILE)){
                	String filePath = content.split(":")[1];
                	String link = ApiManager.uploadFile(new File(filePath));
                	if(link!=null)
                		content = Constants.PREFIX_LINK+URLEncoder.encode(link);
                	else
                		throw new RuntimeException("upload file failure");
                }
  
                if (xmppManager.isAuthenticated()) {
                	
                	MessageIQ messageIQ = new MessageIQ();
                    messageIQ.setId(id);
                    messageIQ.setPacketID(id);
                    messageIQ.setApiKey(xmppManager.getApiKey());
                    messageIQ.setFroms(froms);
                    messageIQ.setTos(tos);
                    messageIQ.setContent(content);
                    messageIQ.setDate(date);
                    messageIQ.setType(IQ.Type.SET);
                    
                    
                    Connection connection = xmppManager.getConnection();
                    PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                            messageIQ.getPacketID()), new PacketTypeFilter(
                            IQ.class));
                    connection.addPacketListener(messageReplyListener, packetFilter);
                    
                    connection.sendPacket(messageIQ);
                
                } else{
                	Log.d(TAG, "xmppManager off line or unauth");//不在线，未连接
                	xmppManager.startReconnectionThread();
                	throw new RuntimeException("xmppManager off line or unauth");
                }
            }catch (Exception e) {
            	xmppManager.startReconnectionThread();
            	DbManager.updateMessage(id,Constants.MessageStatus.FAILED.ordinal(),null);
				e.printStackTrace();
			}finally{
				
				DbManager.saveSession(tos,content,DateUtil.formatDate2Str());//添加到会话
				
				//发送消息已发送完毕广播
				sendOrderedBroadcast(new Intent(Constants.ACTION_MESSAGE_SEND),null);
            	if(xmppManager!=null)
					xmppManager.runTask();
			}
        }
    }
    private PacketListener messageReplyListener = new PacketListener() {
		
		@Override
		public void processPacket(Packet packet) {
			
			if(((IQ)packet).getType() == IQ.Type.RESULT){
				DbManager.updateMessage(packet.getPacketID(),Constants.MessageStatus.FINISH.ordinal(),null);
			}else if(((IQ)packet).getType() == IQ.Type.ERROR){
				DbManager.updateMessage(packet.getPacketID(),Constants.MessageStatus.FAILED.ordinal(),null);
			}
		}
	};
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        try{
        	this.stop();
        }catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()...");
        return true;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public TaskSubmitter getTaskSubmitter() {
        return taskSubmitter;
    }

    public TaskTracker getTaskTracker() {
        return taskTracker;
    }

    public XmppManager getXmppManager() {
        return xmppManager;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void connect() {
        Log.d(TAG, "connect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                MessageService.this.getXmppManager().connect();
            }
        });
    }

    public void disconnect() {
        Log.d(TAG, "disconnect()...");
        taskSubmitter.submit(new Runnable() {
            public void run() {
                MessageService.this.getXmppManager().disconnect();
            }
        });
    }


    private void unregisterNotificationReceiver() {
        unregisterReceiver(notificationReceiver);
    }

    private void registerConnectivityReceiver() {
        Log.d(TAG, "registerConnectivityReceiver()...");
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        IntentFilter filter = new IntentFilter();
        // filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
        Log.d(TAG, "unregisterConnectivityReceiver()...");
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(connectivityReceiver);
    }

    private void start() {
        Log.d(TAG, "start()...");
        registerConnectivityReceiver();
        // Intent intent = getIntent();
        // startService(intent);
        xmppManager.connect();
    }

    private void stop() {
        Log.d(TAG, "stop()...");
        unregisterConnectivityReceiver();
        xmppManager.disconnect();
        executorService.shutdown();
    }

    /**
     * Class for summiting a new runnable task.
     */
    public class TaskSubmitter {

        final MessageService messageService;

        public TaskSubmitter(MessageService notificationService) {
            this.messageService = notificationService;
        }

        @SuppressWarnings("unchecked")
        public Future submit(Runnable task) {
            Future result = null;
            if (!messageService.getExecutorService().isTerminated()
                    && !messageService.getExecutorService().isShutdown()
                    && task != null) {
                result = messageService.getExecutorService().submit(task);
            }
            return result;
        }

    }

    /**
     * Class for monitoring the running task count.
     */
    public class TaskTracker {

        final MessageService messageSevice;

        public int count;

        public TaskTracker(MessageService messageSevice) {
            this.messageSevice = messageSevice;
            this.count = 0;
        }

        public void increase() {
            synchronized (messageSevice.getTaskTracker()) {
                messageSevice.getTaskTracker().count++;
                Log.d(TAG, "Incremented task count to " + count);
            }
        }

        public void decrease() {
            synchronized (messageSevice.getTaskTracker()) {
                messageSevice.getTaskTracker().count--;
                Log.d(TAG, "Decremented task count to " + count);
            }
        }

    }

}
