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

import java.util.Properties;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.imbos.chat.base.BaseApplication;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.LogUtil;
import com.imbos.chat.util.SharedUtils;

/** 
 * This class is to manage the message service and to load the configuration.
 *
 * @author 
 */
public final class ServiceManager {

    private static final String TAG = LogUtil
            .makeLogTag(ServiceManager.class);

    private Context context;

    private Properties props;

    private String version = "0.5.0";

    private String apiKey;

    private String xmppHost;

    private String xmppPort;

   
	
	private NotificationManager mNotificationManager;
    
    public ServiceManager(Context context) {
        this.context = context;

        Config config = BaseApplication.getConfig();
        
        apiKey = config.apiKey;
        xmppHost = config.xmppHost;
        xmppPort = config.xmppPort;
        
        Log.i(TAG, "apiKey=" + apiKey);
        Log.i(TAG, "xmppHost=" + xmppHost);
        Log.i(TAG, "xmppPort=" + xmppPort);

        SharedUtils sharedUtil = SharedUtils.instance(context);
        
        sharedUtil.putString(Constants.API_KEY, apiKey);
        sharedUtil.putString(Constants.VERSION, version);
        sharedUtil.putString(Constants.XMPP_HOST, xmppHost);
        sharedUtil.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
        
        
    }

    public void startService() {
    	Intent intent =new Intent(context,MessageService.class);
        context.startService(intent);
    }

    public void stopService() {
        Intent intent = new Intent(context, MessageService.class); 
        context.stopService(intent);
    }

    public static void sendMessage(Context ctx,String id,String froms,String tos,String content,String date){
    	Intent intent =new Intent(ctx,MessageService.class)
		.setAction(Constants.ACTION_SEND)
		.putExtra(Constants.EXTRA_MSG_ID,id)
		.putExtra(Constants.EXTRA_MSG_FROMS, froms)
		.putExtra(Constants.EXTRA_MSG_TOS, tos)
		.putExtra(Constants.EXTRA_MSG_CONTENT, content)
		.putExtra(Constants.EXTRA_MSG_DATE, date);
    	 ctx.startService(intent);
    }
   
    
    
    private Properties loadProperties() {
       
        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("server", "raw",
                    context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(TAG, "Could not find the properties file.", e);
            // e.printStackTrace();
        }
        return props;
    }

}
