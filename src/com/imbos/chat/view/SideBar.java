package com.imbos.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends TextView {  
	 private char[] l;  
	    private SectionIndexer sectionIndexter = null;  
	    private ListView list;  
	    private TextView mDialogText;
	    private int m_nItemHeight = 15;  
	    private Paint paint;
	    public SideBar(Context context) {  
	        super(context);  
	        init();  
	    }  
	    public SideBar(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        
	        init();  
	    }  
	    private void init() {  
	        l = new char[] { '$','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };  
	        
	        paint = new Paint();  
	        paint.setColor(0xff595c61);  
	        paint.setTextSize(getTextSize());  
	        paint.setAntiAlias(true);
	        paint.setTextAlign(Paint.Align.CENTER);  
	    }  
	    public SideBar(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle); 
	        init();  
	    }  
	    public void setListView(ListView _list) {  
	        list = _list;  
	        if(list.getAdapter() instanceof HeaderViewListAdapter){
	        	sectionIndexter = (SectionIndexer) ((HeaderViewListAdapter)list.getAdapter()).getWrappedAdapter();
	        }  else
	        	sectionIndexter = (SectionIndexer) list.getAdapter();
	    }  
	    
	    public SectionIndexer getSectionIndexter() {
	    	if (sectionIndexter == null) {  
	    		if(list.getAdapter() instanceof HeaderViewListAdapter){
		        	sectionIndexter = (SectionIndexer) ((HeaderViewListAdapter)list.getAdapter()).getWrappedAdapter();
		        }  else
		        	sectionIndexter = (SectionIndexer) list.getAdapter();
            }  
			return sectionIndexter;
		}
	    
	    public void setTextView(TextView mDialogText) {  
	    	this.mDialogText = mDialogText;  
	    }  
	    
	    @Override
	    protected void onLayout(boolean changed, int left, int top, int right,
	    		int bottom) {
	    	if(changed){ 
	    		m_nItemHeight = (bottom-top)/l.length;
	    	}
	    	super.onLayout(changed, left, top, right, bottom);
	    }
	    public boolean onTouchEvent(MotionEvent event) {  
	        super.onTouchEvent(event);  
	        int i = (int) event.getY();  
	        int idx = i / m_nItemHeight;  
	        if (idx >= l.length) {  
	            idx = l.length - 1;  
	        } else if (idx < 0) {  
	            idx = 0;  
	        }  
	        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
	        	mDialogText.setVisibility(View.VISIBLE);
	        	mDialogText.setText(""+l[idx]);
	            
	            int position = getSectionIndexter().getPositionForSection(l[idx]);  
	            if (position == -1) {  
	                return true;  
	            }  
	            list.setSelection(position);  
	        }else{
	        	mDialogText.setVisibility(View.INVISIBLE);
	        }  
	        return true;  
	    }  
	    protected void onDraw(Canvas canvas) {  
	        
	        float widthCenter = getMeasuredWidth() / 2;  
	        for (int i = 0; i < l.length; i++) {  
	            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);  
	        }  
	        super.onDraw(canvas);  
	    }  
}
