package com.imbos.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLayout extends LinearLayout{
	private OnResizeListener onResizeListener;
	
	
	public ResizeLayout(Context ctx) {
		super(ctx);
		this.setOrientation(LinearLayout.VERTICAL);
	}
	
	public ResizeLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        this.setOrientation(LinearLayout.VERTICAL);
       
	}  
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(onResizeListener!=null)
			onResizeListener.onResize(changed, l, t, r, b);
		
	}
	
	public void setOnResizeListener(OnResizeListener onResizeListener) {
		this.onResizeListener = onResizeListener;
	}
	
	public static interface OnResizeListener{
		public void onResize(boolean changed, int l, int t, int r, int b);
	}
}
