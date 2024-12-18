package com.imbos.code.scanning;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.code.scanning.camera.CameraManager;
import com.imbos.code.scanning.decoding.CaptureActivityHandler;
import com.imbos.code.scanning.decoding.InactivityTimer;
import com.imbos.code.scanning.view.ViewfinderView;


public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtResult;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private Button confirm;
	private Button cancel;
	
	private String resultCode; 
	

	private static Class<?> nextActivity;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_activity);
		
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtResult = (TextView) findViewById(R.id.txtResult);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		confirm = (Button)findViewById(R.id.ok);
		cancel = (Button)findViewById(R.id.cancel);
		confirm.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.ok:
				
				Intent data = new Intent();
				data.putExtra("resultCode", resultCode);
				setResult(1, data);
				finish();
				break;
			case R.id.cancel:
				txtResult.setText("");
				confirm.setVisibility(View.INVISIBLE);
				cancel.setVisibility(View.INVISIBLE);
				viewfinderView.flag = false;
				reStart();
				break;
			default:
				break;
			}
		}
	};
	
	public void reStart()
	{
		handler = new CaptureActivityHandler(this, decodeFormats,characterSet);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
		if(viewfinderView!=null){
			viewfinderView.resultBitmap = null;
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		 playBeepSoundAndVibrate();
		//txtResult.setText(obj.getBarcodeFormat().toString() + ":" + obj.getText());
		
		resultCode = obj.getText();
		
		//viewfinderView.flag = true;
		//viewfinderView.resultBitmap = null;
		
//		confirm.setVisibility(View.VISIBLE);
//		cancel.setVisibility(View.VISIBLE);
		Intent data = new Intent();
		data.putExtras(getIntent());
		data.putExtra(Intents.EXTRA_DATA, resultCode);
		
		if(nextActivity!=null){
			startActivity(new Intent(data).setClass(this,nextActivity));
		}else{
			setResult(RESULT_OK, data);
			finish();
		}
	}

	public static void setNextActivity(Class<?> nextActivity) {
		CaptureActivity.nextActivity = nextActivity;
	}
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

//			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
//			try {
//				mediaPlayer.setDataSource(file.getFileDescriptor(),
//						file.getStartOffset(), file.getLength());
//				file.close();
//				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
//				mediaPlayer.prepare();
//			} catch (IOException e) {
//				mediaPlayer = null;
//			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}