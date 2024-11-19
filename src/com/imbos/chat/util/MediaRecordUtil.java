package com.imbos.chat.util;

import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

public class MediaRecordUtil {
	
	public static int STATUS_STOP=0;
	public static int STATUS_RECORDING=1;
	
	private static String TAG = MediaRecordUtil.class.getSimpleName();
	private int status;
	
	
	
	private MediaRecorder mRecorder;
	private String fileName;
	
	
	
	public MediaRecordUtil(Context ctx) {
	
	}
	
	public void start(String fileName) {
		this.fileName = fileName;
		Log.d(TAG, fileName);
		this.status = STATUS_RECORDING;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(this.fileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
            
        }
        mRecorder.start();
    }
	public String stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        this.status = STATUS_RECORDING;
        return this.fileName;
    }
	public int getStatus() {
		return status;
	}
}
