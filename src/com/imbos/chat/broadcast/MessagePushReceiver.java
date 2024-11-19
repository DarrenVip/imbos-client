package com.imbos.chat.broadcast;

import java.util.Map;

import com.imbos.chat.MainActivity;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.JSONUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.client.MessageIQ;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;



/**
 * 自定义接收器
 * @author wanxianze@gmail.com 2012-6-1
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MessagePushReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
//        String msg = printBundle(bundle);
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
//		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            //send the Registration Id to your server...
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
        	String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);
          //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	
        	String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        	Map<String,String> map = (Map<String, String>) JSONUtil.decode(msg);
        	String id = StringUtil.toString(map.get("i"));
        	if(StringUtil.isEmpty(id))
        		return;
        	
        	
        	MessageIQ messageIQ = new MessageIQ();
        	messageIQ.setId(id);
        	messageIQ.setFroms(StringUtil.toString(map.get("f")));
        	messageIQ.setTos(StringUtil.toString(map.get("t")));
        	messageIQ.setContent(StringUtil.toString(map.get("c")));
        	messageIQ.setDate(StringUtil.toString(map.get("d")));
        	
        	
        	context.sendOrderedBroadcast(
					new Intent(Constants.ACTION_MESSAGE_RECEVER)
						.putExtra(Constants.EXTRA_DATA, messageIQ), null);
        	
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            
        	//打开自定义的Activity
        	Intent i = new Intent(context, MainActivity.class);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(i);
        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	

}
