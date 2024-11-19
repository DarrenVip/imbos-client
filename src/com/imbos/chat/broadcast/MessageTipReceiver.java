package com.imbos.chat.broadcast;

import java.io.Serializable;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imbos.chat.MainActivity;
import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.manage.LogicManager;
import com.imbos.chat.model.ChatMessage;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.SharedUtils;
import com.imbos.chat.util.StringUtil;
import com.imbos.client.MessageIQ;
import com.imbos.client.MessageService;

/**
 * 自定义接收器
 * @author wanxianze@gmail.com 2012-6-1
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MessageTipReceiver extends BroadcastReceiver{
	
	private Context ctx;
	private MessageIQ messageIQ;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
		//EmojiGetter emojiGetter = new EmojiGetter(context, target)
		Serializable serializable = intent.getSerializableExtra(Constants.EXTRA_DATA);
		if(serializable!=null && serializable instanceof MessageIQ){
			
			messageIQ = (MessageIQ)serializable;
			//String content = messageIQ.getContent();
			
			ChatMessage message = new ChatMessage();
            message.content = messageIQ.getContent();
            
            String froms = messageIQ.getFroms();
			
            String fromsName = LogicManager.findUserName(froms);
            fromsName = StringUtil.isEmpty(fromsName)?context.getString(R.string.CHAT_TEMP):fromsName;
            
            Intent targetIntent = new Intent(context, MainActivity.class)
            	.putExtra(Intents.EXTRA_TITLE,fromsName)
				.putExtra(Intents.EXTRA_MSG_TOS,froms);
          
           SharedUtils sharedUtils = SharedUtils.instance(ctx);
           boolean needNotify = sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_ON,0) ==1;
           if(needNotify){
	          
        	   boolean needSound =  sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_SOUND,0) ==1;
	           boolean needVibrate =  sharedUtils.getInt(Constants.SHARED_SET_NOTIFY_VIBRATOR,0) ==1;
	           
	           
	           MessageService.bindNotifiyOnClickEvent(context,fromsName + ":" + message.toString(),
	        		   needSound,needVibrate,
	        		   PendingIntent.getActivity(context, 0, targetIntent, 0));
	       }
           
		}
	}
	
}
