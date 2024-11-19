package com.imbos.chat.manage;

import java.util.HashMap;

import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.ThreadPool;

import android.content.Context;

public class SyncManager {
	
	public static void loadUsers(Context ctx){
		ThreadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				ApiManager.loadUsers();
			}
		});
	}
	
	public static void sync(String methodName,HashMap<String,?> args){
		
	}
}
