package com.imbos.chat.member;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.R.id;
import com.imbos.chat.R.layout;
import com.imbos.chat.app.Intents;

public class MemberDialog extends Activity implements OnClickListener {
	
	TextView txtAlias;
	TextView txtStar;
	TextView txtDel;
	TextView txtBlack;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_dialog);
				
		txtAlias = (TextView) findViewById(R.id.menu_alias);
		txtStar = (TextView) findViewById(R.id.menu_star);
		txtDel = (TextView) findViewById(R.id.menu_del);
		txtBlack = (TextView) findViewById(R.id.menu_black);
		
		txtAlias.setOnClickListener(this);
		txtStar.setOnClickListener(this);
		txtDel.setOnClickListener(this);
		txtBlack.setOnClickListener(this);
		
		
		
	}
	@Override
	public void onClick(View view) {
		setResult(RESULT_OK,   
				new Intent().putExtra(Intents.EXTRA_MENU_ID, view.getId()));
		finish();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	
	
}
