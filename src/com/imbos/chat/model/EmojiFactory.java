package com.imbos.chat.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;


public class EmojiFactory{
	
	private static final String TAG = "EmojiFactory";
	
	private Context context;
	public EmojiFactory(Context context) {
		this.context = context;
	}
	
	public Map<String,Emoji> builder(){
		Map<String,Emoji> emojis = new LinkedHashMap<String, Emoji>();
		
		final String EMOJI_TAG = "emoji";
		String packageName =context.getPackageName();
		try {
			Resources res =  context.getResources();
			XmlResourceParser xml = res.getXml(R.xml.emoji);
			int xmlEventType;
			Emoji emoji = null;
			while ((xmlEventType = xml.next()) != XmlResourceParser.END_DOCUMENT) {
				// 一级标签
				if (xmlEventType == XmlResourceParser.START_TAG
						&& EMOJI_TAG.equals(xml.getName())) {
					
					emoji = new Emoji();
					emoji.code = xml.getAttributeValue(null,"code");
					emoji.name = xml.getAttributeValue(null,"name");
					String resName =xml.getAttributeValue(null,"img");
					emoji.resId = res.getIdentifier(resName,"drawable",packageName);
					emojis.put(emoji.code,emoji);
					
				} else if (xmlEventType == XmlResourceParser.END_TAG
						&& EMOJI_TAG.equals(xml.getName())) {
				}
			}
		} catch (Exception e) {
			Log.e(TAG,"Exception" +e.getMessage());
		}
		return emojis;
	}
	
	
	public static String replaceEmojis(String str){
		String result = str;
		Map<String, Emoji> emojis = ChatApp.getEmojis();
		Set<Map.Entry<String, Emoji>> entrys = emojis.entrySet();
		for (Map.Entry<String, Emoji> entry : entrys) {
			result = result.replace(entry.getKey(), entry.getValue().toString());
		}
		return result;
	}
	
	public static class Emoji{
		public int resId;
		public String code;
		public String name;
		
		@Override
		public String toString() {
			return "<img src=\""+code+"\"/>";
		}
	}
}