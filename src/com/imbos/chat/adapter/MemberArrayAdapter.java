package com.imbos.chat.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.model.Member;
import com.imbos.chat.util.StringUtil;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;

public class MemberArrayAdapter extends ArrayAdapter<Member>{
	
	
	public MemberArrayAdapter(Activity activity) {
		super(activity,R.layout.member_item,R.id.txt_name,new ArrayList<Member>());
	}
	public MemberArrayAdapter(Activity activity,int layout) {
		super(activity,layout,R.id.txt_name,new ArrayList<Member>());
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Member item = getItem(position);

		TextView txtName = ((TextView)convertView.findViewById(R.id.txt_name));
		txtName.setText(item.name);
		if(item.real>0){
			txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ico_verify,0);
		}else{
			txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
		}
		
		
		((TextView)convertView.findViewById(R.id.txt_remark)).setText(item.doing);
		
		final ImageView imgFace = (ImageView) convertView.findViewById(R.id.img_face);
		UrlImageViewHelper.setUrlDrawable(imgFace, AsyncImageLoader.builderUrl(item.head),R.drawable.default_face);
//		asyncImageLoader.loadDrawable(AsyncImageLoader.builderUrl(item.head), new ImageCallback() {
//	            public void imageLoaded(Drawable drawable, String imageUrl) {
//	            	if(drawable==null && imgFace!=null){
//	            		imgFace.setImageResource(R.drawable.default_face);
//	            	}else if (imgFace != null) {
//	            		imgFace.setImageDrawable(drawable);
//	                }
//	            	
//	            }
//    	});
		return convertView;
	}
	
	@Override
	public long getItemId(int position) {
		String uid = getItem(position).uid;
		long result = StringUtil.isInteger(uid)?Long.parseLong(uid):0;
		return result;
	}
	
}
