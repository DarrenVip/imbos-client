package com.imbos.chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.imbos.chat.app.Intents;

public class MainDialog  extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_dialog);
		
		findViewById(R.id.menu_chat).setOnClickListener(this);
		findViewById(R.id.menu_receiver).setOnClickListener(this);
		findViewById(R.id.menu_qrcode).setOnClickListener(this);
		
	
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}

	@Override
	public void onClick(View view) {
		setResult(RESULT_OK,   
				new Intent().putExtra(Intents.EXTRA_MENU_ID, view.getId()));
		finish();
	}

}
