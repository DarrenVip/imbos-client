package com.imbos.chat.util;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class MediaPlayUtil {
	
	public static int STATUS_STOP=0;
	public static int STATUS_RECORDING=1;
	
	private static String TAG = MediaPlayUtil.class.getSimpleName();
	private int status;
	
	private OnChangeListener onChangeListener;
	
	public void setOnChangeListener(OnChangeListener onChangeListener) {
		this.onChangeListener = onChangeListener;
	}
	
	private MediaPlayer mPlayer;
	
	public MediaPlayUtil(Context ctx) {
		mPlayer = new MediaPlayer();
		
	}
	
	public void start(String fileName) {
		
		this.status = STATUS_RECORDING;
		try {  
			mPlayer.reset();
			Log.d(TAG, "fileName:"+fileName);
			mPlayer.setDataSource(fileName);
			
			mPlayer.prepare();  
			mPlayer.setOnCompletionListener(onChangeListener);
			mPlayer.setOnErrorListener(onChangeListener);  
			
		    mPlayer.start();  
		     
		}catch (Exception e) {
			e.fillInStackTrace();
		}
	}
	
	public void stop() {
        mPlayer.stop();
    }
	public interface OnChangeListener extends OnCompletionListener,OnErrorListener{
	
		
		@Override
		public void onCompletion(MediaPlayer mp);
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra);
	}
}
