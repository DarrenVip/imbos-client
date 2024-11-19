package com.imbos.chat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.image.AsyncImageLoader.ImageCallback;
import com.imbos.chat.model.ChatMessage;
import com.imbos.chat.model.ChatMessage.Type;
import com.imbos.chat.view.EmojiGetter;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage>{
	private String fromsHead;
	private String tosHead;
	private AsyncImageLoader asyncImageLoader;
	public ChatMessageAdapter(Context context, int resource, int textViewResourceId,
			List<ChatMessage> objects) {
		super(context, resource, textViewResourceId, objects);
		
	}
	public void setFromsHead(String fromsHead) {
		this.fromsHead = fromsHead;
	}
	public void setTosHead(String tosHead) {
		this.tosHead = tosHead;
	}
	public void setAsyncImageLoader(AsyncImageLoader asyncImageLoader) {
		this.asyncImageLoader = asyncImageLoader;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		
		ChatMessage item = getItem(position);
		convertView.setTag(item);
		ChatMessage lastItem = position>0?getItem(position-1):null;
		
		
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.p_chat);
		LinearLayout inner = (LinearLayout) convertView.findViewById(R.id.p_inner);
		
		TextView txtCategory = ((TextView)convertView.findViewById(R.id.txt_category));
		
		
		//TextView txtRemark = ((TextView)convertView.findViewById(R.id.txt_remark));
		TextView txtMsg = ((TextView)convertView.findViewById(R.id.txt_msg));
		ImageView imgVoice = ((ImageView)convertView.findViewById(R.id.img_voice));
		
		final ImageView imgImage = ((ImageView)convertView.findViewById(R.id.img_image));
		final ImageView imgFaceLeft = ((ImageView)convertView.findViewById(R.id.img_face_left));
		final ImageView imgFaceRight = ((ImageView)convertView.findViewById(R.id.img_face_rigth));
		
		UrlImageViewHelper.setUrlDrawable(imgFaceRight,AsyncImageLoader.builderUrl(fromsHead));
		UrlImageViewHelper.setUrlDrawable(imgFaceLeft, AsyncImageLoader.builderUrl(tosHead));


		String lastTime = lastItem!=null?lastItem.showDate:"";
		if(item.showDate.equals(lastTime))
			txtCategory.setVisibility(View.GONE);
		else{
			txtCategory.setText(item.showDate);
			txtCategory.setVisibility(View.VISIBLE);
		}
			
		
		
		if(item.direction==0){
			//txtRemark.setText(item.createDate);
			layout.setGravity(Gravity.RIGHT);
			inner.setBackgroundResource(R.drawable.chatto_bg);
			imgFaceLeft.setVisibility(View.INVISIBLE);
			imgFaceRight.setVisibility(View.VISIBLE);
			imgVoice.setImageResource(R.drawable.chatto_voice_playing);
			
		}else{
			//txtRemark.setText(item.froms +":"+ item.createDate);
			inner.setBackgroundResource(R.drawable.chatfrom_bg);
			layout.setGravity(Gravity.LEFT);
			imgFaceLeft.setVisibility(View.VISIBLE);
			imgFaceRight.setVisibility(View.INVISIBLE);
			imgVoice.setImageResource(R.drawable.chatfrom_voice_playing);
		}
		
		
		if(Type.SOUND == item.getType()){
			txtMsg.setText("");
			txtMsg.setTag(item.toString(true));
			imgVoice.setVisibility(View.VISIBLE);
			imgImage.setVisibility(View.GONE);
    	}else if(Type.IMAGE == item.getType()){
    		asyncImageLoader.loadDrawable(item.getFilePath(), new ImageCallback() {
    			@Override
    			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
    				if(imageDrawable!=null)
    					imgImage.setImageDrawable(imageDrawable);
    			}
    		});
    		txtMsg.setText(null);
    		imgVoice.setVisibility(View.GONE);
    		imgImage.setVisibility(View.VISIBLE);
    	}else if(Type.FILE == item.getType()){
    		txtMsg.setText(Html.fromHtml(item.toString(),null,null));
    		imgVoice.setVisibility(View.GONE);
    		imgImage.setVisibility(View.GONE);
    	}else {
    		imgVoice.setVisibility(View.GONE);
    		imgImage.setVisibility(View.GONE);
    		txtMsg.setText(Html.fromHtml(item.toString(true),new EmojiGetter(this.getContext(),txtMsg),null));
    	}
		return convertView;
	};
}
