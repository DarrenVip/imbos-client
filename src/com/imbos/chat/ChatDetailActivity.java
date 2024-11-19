package com.imbos.chat;

import android.os.Bundle;
import android.view.View;

import com.imbos.chat.app.SkinActivity;

public class ChatDetailActivity extends SkinActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_detail);
		
		setTitle(R.string.CHAT_DETAIL);
		btnNext.setVisibility(View.INVISIBLE);
		
		
	}
}
