package com.imbos.chat.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.imbos.chat.R;


public class ToggleButton extends FrameLayout implements OnClickListener {
	public static final int LEFT_CHOOSED = 0;
	public static final int RIGHT_CHOOSED = 1;
	
	private View root;
	private TextView leftTextView;
	private TextView rightTextView;
	
	private int choose = LEFT_CHOOSED;
	
	private OnToggleButtonChangedListener listener = null;
	
	public ToggleButton(Context context) {
		super(context);
		initViews(context);
	}

	public ToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}
	
	private void initViews(Context context) {
		root = LayoutInflater.from(context).inflate(R.layout.toggle_btn_layout, this);
		leftTextView = (TextView) root.findViewById(R.id.left_btn);
		rightTextView = (TextView) root.findViewById(R.id.right_btn);
		
		leftTextView.setOnClickListener(this);
		rightTextView.setOnClickListener(this);
	}
	
	public int getChoose() {
		return choose;
	}
	
	public void setOnToggleButtonChangedListener(OnToggleButtonChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if (leftTextView == v) {
			if (choose == RIGHT_CHOOSED) {
				leftTextView.setBackgroundResource(R.drawable.usage_list_green);
				leftTextView.setTextColor(Color.BLACK);
				choose = LEFT_CHOOSED;
				
				rightTextView.setBackgroundDrawable(null);
				rightTextView.setTextColor(Color.WHITE);
				
				if (null != listener) {
					listener.onToggleChanged(choose);
				}
			}
		} else if (rightTextView == v) {
			if (choose == LEFT_CHOOSED) {
				rightTextView.setBackgroundResource(R.drawable.usage_list_green);
				rightTextView.setTextColor(Color.BLACK);
				choose = RIGHT_CHOOSED;
				
				leftTextView.setBackgroundDrawable(null);
				leftTextView.setTextColor(Color.WHITE);
				
				if (null != listener) {
					listener.onToggleChanged(choose);
				}
			}
		}
	}
	
	public interface OnToggleButtonChangedListener {
		public void onToggleChanged(int curChoose);
	}
}
