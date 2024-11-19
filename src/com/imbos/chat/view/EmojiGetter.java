package com.imbos.chat.view;

import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.View;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.model.EmojiFactory.Emoji;

public class EmojiGetter implements ImageGetter{
	
	private Context context;
	private View targetView;
	Map<String,Emoji> emojis;
	
	public EmojiGetter(Context context,View target) {
		this.context = context;
		this.targetView = target;
		emojis = ChatApp.getEmojis();
	}
	
	@Override
	public Drawable getDrawable(String url) {
		Emoji emoji = emojis.get(url);
		int resId = emoji==null?0:emoji.resId;
		
		Drawable drawable=null;
		try {
			if(resId==0){
				drawable = context.getResources().getDrawable(R.drawable.smiley_0);
			}else{
				drawable = context.getResources().getDrawable(resId);
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                    .getIntrinsicHeight());
			//drawable.setBounds(0,targetView.getPaddingTop(), targetView.getHeight(),targetView.getHeight()-targetView.getPaddingBottom());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return drawable;
	}
	
}
