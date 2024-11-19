package com.imbos.chat.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class VibratorUtil {
	public static void vibrate(Context ctx, long milliseconds) {
		Vibrator vib = (Vibrator) ctx
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void vibrate(Context ctx) {
		Vibrator vib = (Vibrator) ctx
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(100);
	}
	
	public static void vibrate(Context ctx, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) ctx
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
}
