package com.imbos.chat.image;

import java.util.List;
import android.content.Context;
import android.widget.ArrayAdapter;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class ImageListAdapter extends ArrayAdapter<Object>{
	
	protected AsyncImageLoader asyncImageLoader = new AsyncImageLoader(this.getContext());
	
	public ImageListAdapter(Context context,
			List<Object> objects) {
		super(context,0, objects);
	}
}
