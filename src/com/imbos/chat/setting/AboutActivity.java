package com.imbos.chat.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.imbos.chat.R;
import com.imbos.chat.WebActivity;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.util.Constants;

public class AboutActivity extends SkinActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.SET_ABOUT);
		//btnBack.setVisibility(View.INVISIBLE);
		
		//findViewById(R.id.item_account).findViewById(R.id.txt_)
			
		findViewById(R.id.item_help).setOnClickListener(this);
		findViewById(R.id.item_paly).setOnClickListener(this);
		findViewById(R.id.item_understand).setOnClickListener(this);
		findViewById(R.id.item_welcome).setOnClickListener(this);

		
	}
	

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.item_help:
			startActivity(new Intent(this,WebActivity.class)
					.putExtra(WebActivity.EXTRA_LINK, 
							AsyncImageLoader.builderUrl(Constants.ABOUT_LINK.HELP)));
			break;
		case R.id.item_paly:
			startActivity(new Intent(this,WebActivity.class)
			.putExtra(WebActivity.EXTRA_LINK, 
					AsyncImageLoader.builderUrl(Constants.ABOUT_LINK.PLAY)));
			break;
		case R.id.item_understand:
			startActivity(new Intent(this,WebActivity.class)
			.putExtra(WebActivity.EXTRA_LINK, 
					AsyncImageLoader.builderUrl(Constants.ABOUT_LINK.UNDERSTAND)));
			break;
		case R.id.item_welcome:
			break;
		default:
			super.onClick(view);
		}
	}
	
	
}
