package com.imbos.chat.image;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.image.AsyncImageLoader.ImageCallback;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class ImageActivity extends SkinActivity{
	private ImageView imageView;
	private ProgressBar progressBar;
	private AsyncImageLoader asyncImageLoader;
	private String imageUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_activity);
		setTitle(R.string.IMG_TITLE);
		btnNext.setVisibility(View.INVISIBLE);
		imageUrl = getIntent().getStringExtra(Intents.EXTRA_DATA);
		
		imageView = (ImageView) findViewById(R.id.image1);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		asyncImageLoader = ((ChatApp)this.getApplication()).getAsyncTempLoader();
		asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				if(imageDrawable==null){
					
					showToast(R.string.GENERAL_IMAGE_FAIL);
				}else
					imageView.setImageDrawable(imageDrawable);
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(asyncImageLoader!=null)
			asyncImageLoader.destroyImage(imageUrl);
	}
}
