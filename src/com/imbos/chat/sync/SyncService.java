package com.imbos.chat.sync;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.util.ThreadPool;

public class SyncService extends Service{
	
	private static List<SyncTask> taskQueue = new ArrayList<SyncTask>();
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null || intent.getAction() == null)
			return super.onStartCommand(intent, flags, startId);
		
		if(taskQueue==null){
			 taskQueue = new ArrayList<SyncTask>();
		}else{
			//移除已经结束的 任务
			List<SyncTask> finshTask = new ArrayList<SyncTask>();
			for (SyncTask task : taskQueue) {
				if (task != null
						&& (task.getStatus() == SyncTask.STATUS_FINISH || task
								.getStatus() == SyncTask.STATUS_FAILED)) {
					finshTask.add(task);
				}else{
					ThreadPool.submit(task);
				}
			}
			taskQueue.removeAll(finshTask);
		}
		
		return START_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	public static void start(SyncTask task){
		taskQueue.add(task);
		ChatApp.getContext().startService(
				new Intent(Intents.ACTION_SYNC));
	}
}
