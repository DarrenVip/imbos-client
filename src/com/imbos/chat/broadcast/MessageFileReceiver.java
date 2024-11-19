package com.imbos.chat.broadcast;

import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imbos.chat.db.DbManager;
import com.imbos.chat.manage.ApiManager;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.util.ThreadPool;
import com.imbos.client.MessageIQ;

public class MessageFileReceiver extends BroadcastReceiver implements Runnable{
	
	
	private Context ctx;
	private String link;
	private MessageIQ messageIQ;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
		Serializable serializable = intent.getSerializableExtra(Constants.EXTRA_DATA);
		if(serializable!=null && serializable instanceof MessageIQ){
			
			messageIQ = (MessageIQ)serializable;
			String content = StringUtil.toString(messageIQ.getContent());
			
			
			if(content.startsWith(Constants.PREFIX_LINK)){//开始下载文件
				abortBroadcast();
				
				DbManager.saveMessage(messageIQ.getId(),messageIQ.getFroms(),
						messageIQ.getTos(), messageIQ.getContent(),
						DateUtil.formatDate2Str(),
						Constants.MessageStatus.DOWLOADING.ordinal());
				link = messageIQ.getContent().replace(Constants.PREFIX_LINK,"");
				link = URLDecoder.decode(link); 
				ThreadPool.submit(this);
				
			}else if(content.startsWith(Constants.PREFIX_FILE)){//文件下载完成
				String date = DateUtil.formatDate2Str();
				
				DbManager.updateMessage(messageIQ.getId(),
						Constants.MessageStatus.FINISH.ordinal(), 
						content,date);
				//增加到会话
				DbManager.saveSession(messageIQ.getFroms(),content,date);
			}else{//普通消息
				DbManager.saveMessage(messageIQ.getId(),messageIQ.getFroms(),
						messageIQ.getTos(), messageIQ.getContent(),
						DateUtil.formatDate2Str(),
						Constants.MessageStatus.FINISH.ordinal());
				//增加到会话
				DbManager.saveSession(messageIQ.getFroms(),content,DateUtil.formatDate2Str());
			}
		}
	}
	@Override
	public void run() {
		try {
			String dir = FileUtil.getFileDir(ctx); 
			
			File file =ApiManager.download(dir,link);
			if(file!=null && file.exists())
			{
				messageIQ.setContent("file:"+file.getPath());
				ctx.sendOrderedBroadcast(new Intent(Constants.ACTION_MESSAGE_RECEVER)
					.putExtra(Constants.EXTRA_DATA,messageIQ),null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
