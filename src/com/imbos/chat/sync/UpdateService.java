package com.imbos.chat.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.imbos.chat.MainActivity;
import com.imbos.chat.R;
import com.imbos.chat.util.AppUtil;
import com.imbos.chat.util.SharedUtils;

public class UpdateService extends Service {
	// 标题
	private int titleId = 0;

	// 文件存储
	private File updateDir = null;
	private File updateFile = null;
	private String updateUrl = null;

	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;

	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	
	//下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	public static String DOWNLOAD_PATH = "download";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获取传值
		titleId = intent.getIntExtra("titleId", 0);
		updateUrl = intent.getStringExtra("updateUrl");
		
		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();
		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		// 设置通知栏显示内容
		updateNotification.contentView = getProgressBar();
		updateNotification.icon = android.R.drawable.stat_sys_download;
		updateNotification.tickerText = "开始下载";
		// 发出通知
		updateNotificationManager.notify(0, updateNotification);
		
		String path = AppUtil.getSavePath(1024 * 1024 * 50);
		if(path == null) {
			updateHandler.sendEmptyMessage(DOWNLOAD_FAIL);
		} else {			
			// 创建文件
			updateDir = new File(path, DOWNLOAD_PATH);
			updateFile = new File(updateDir.getPath(), getResources().getString(titleId) + ".apk");
			// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
			new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Handler updateHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        switch(msg.what){
	            case DOWNLOAD_COMPLETE:
	            	updateNotificationManager.cancel(0);
	                //点击安装PendingIntent
	                Uri uri = Uri.fromFile(updateFile);
	                Intent installIntent = new Intent(Intent.ACTION_VIEW);
	                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
	                updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
					try {
						updatePendingIntent.send();
						SharedUtils.instance(UpdateService.this).clear();
					} catch (CanceledException e) {
						e.printStackTrace();
					}
	                break;
	            case DOWNLOAD_FAIL:
	            	updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
	            	updateNotification.icon = android.R.drawable.stat_sys_warning;
	            	updateNotification.defaults = Notification.DEFAULT_SOUND;
	            	updateNotification.tickerText = "抱歉，更新失败了...";
	                updateNotification.setLatestEventInfo(UpdateService.this, getString(R.string.app_name), "抱歉,下载失败了...", null);
	                updateNotificationManager.notify(0, updateNotification);
	                
	                updateNotificationManager.cancel(0);
	                Toast.makeText(UpdateService.this, "抱歉，更新失败了...", Toast.LENGTH_SHORT).show();
	                break;
	            default:
	            	break;
	        }
	        stopSelf();
	    }
	};

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();
		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				// 增加权限;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				// 下载函数，以QQ为例子
				// 增加权限;
				long downloadSize = downloadUpdateFile(updateUrl, updateFile);
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}

	public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {

		// 这样的下载代码很多，我就不做过多的说明
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();

			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;
					updateNotification.contentView.setProgressBar(R.id.progressBar, 100, (int) totalSize * 100 / updateTotalSize, false);
					updateNotification.contentView.setTextViewText(R.id.text_progress, (int) totalSize * 100 / updateTotalSize + "%");
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}
	
	private RemoteViews getProgressBar() {
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.progress_bar);
		contentView.setProgressBar(R.id.progressBar, 100, 0, false);
		contentView.setTextViewText(R.id.text_progress, "0%");
		contentView.setTextViewText(R.id.text_appname, getString(R.string.app_name) + " 正在下载...");
		return contentView;
	}
}
