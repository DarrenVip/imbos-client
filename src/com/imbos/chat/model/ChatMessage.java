package com.imbos.chat.model;

import java.io.Serializable;
import java.util.Date;

import android.database.sqlite.SQLiteCursor;

import com.imbos.chat.db.DbManager;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;

public class ChatMessage implements Serializable{
	
	
	
	public String id;
	public String froms;
	public String tos;
	public String content;
	public String createDate;
	public String showDate;
	public Date date;
	public String recevieDate;
	public int direction;

	public int status;
	
	
	public ChatMessage() {
		
	}
	
	public Type getType(){
		return getType(content);
	}
	
	public static Type getType(String content){
		
		if(content==null)
			return Type.TEXT;
		else if(content.startsWith(Constants.PREFIX_FILE)&&
				content.endsWith(Constants.SUFFIX_SOUND)){
			return Type.SOUND;
		}else if(content.startsWith(Constants.PREFIX_FILE)&&
				(content.toLowerCase().endsWith(Constants.SUFFIX_JPG) ||
				 content.toLowerCase().endsWith(Constants.SUFFIX_JPEG) ||
				 content.toLowerCase().endsWith(Constants.SUFFIX_PNG) )){
			return Type.IMAGE;
		}else if(content.startsWith(Constants.PREFIX_FILE)){
			return Type.FILE;
		}else if(content.startsWith(Constants.PREFIX_LINK))
			return Type.LINK;
		else
			return Type.TEXT;
	}
	
	
	
	@Override
	public String toString() {
		Type type = this.getType();
		if(type==Type.FILE)
			return "[文件] ";
		else if(type==Type.IMAGE)
			return "[图片]";
		else if(type==Type.SOUND)
			return "[语音]";
		else if(type==Type.LINK)
			return "[文件]";
		else if(type==Type.TEXT)
			return content;
		else 
			return "";
	}

	public String toString(boolean replace) {
		String result = toString();
		if(replace)
			result = EmojiFactory.replaceEmojis(result);
		return result;
	}
	
	public enum Type{
		TEXT,IMAGE,SOUND,FILE,LINK
	}
	public enum Status{
		READER,SEND,RECVIE,FINISH,FINAL,REAED
	}
	
	public String getFileName(){
		String filePath = StringUtil.toString(content);
		if(filePath.lastIndexOf("=")>0)
			return filePath.substring(filePath.lastIndexOf("=")+1);
		else if(filePath.lastIndexOf("/")>0)
			return filePath.substring(filePath.lastIndexOf("/")+1);
			
		return "";
	}
	public String getFilePath(){
		return content.replace(Constants.PREFIX_FILE,"");
	}
	
	public static ChatMessage loadMessage(String id){
		SQLiteCursor cursor=DbManager.queryMessage(id);
		ChatMessage message = new ChatMessage();
		if(cursor.moveToNext()){
			message.id=cursor.getString(0);
			message.content=cursor.getString(0);
			message.id=cursor.getString(0);
		}
		cursor.close();
		return message;
	}
}
