package com.imbos.chat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.imbos.chat.R;

public class AppUtil {
	static Context context;

	static void init(Context cxt) {
		context = cxt;
	}

	/** get external Storage available space */
	public static long getExternaltStorageAvailableSpace() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		File path = Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(path.getPath());
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/** get external Storage available space */
	public static long getExternaltStorageTotalSpace() {
		File path = Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(path.getPath());
		long blockSize = statfs.getBlockSize();
		long totalBlocks = statfs.getBlockCount();
		return blockSize * totalBlocks;
	}

	/** get sdcard2 external Storage available space */
	public static long getSdcard2StorageAvailableSpace() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		String path = getSdcard2StorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/** get sdcard2 external Storage total space */
	public static long getSdcard2StorageTotalSpace() {
		String path = getSdcard2StorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long totalBlocks = statfs.getAvailableBlocks();
		return blockSize * totalBlocks;
	}

	/** get EMMC internal Storage available space */
	public static long getEmmcStorageAvailableSpace() {
		String path = getEmmcStorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();

		return blockSize * availableBlocks;
	}

	/** get EMMC internal Storage available space */
	public static long getEmmcStorageTotalSpace() {
		String path = getEmmcStorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long totalBlocks = statfs.getBlockCount();

		return blockSize * totalBlocks;
	}

	static FstabReader fsReader = null;

	/** get other external Storage available space */
	public static long getOtherExternaltStorageAvailableSpace() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		if (otherExternalStorageState == kOtherExternalStorageStateUnable)
			return 0;
		if (otherExternalStorageDirectory == null) {
			getOtherExternalStorageDirectory();
		}
		if (otherExternalStorageDirectory == null)
			return 0;
		StatFs statfs = new StatFs(otherExternalStorageDirectory);
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	private static String otherExternalStorageDirectory = null;
	private static int kOtherExternalStorageStateUnknow = -1;
	private static int kOtherExternalStorageStateUnable = 0;
	private static int kOtherExternalStorageStateIdle = 1;
	private static int otherExternalStorageState = kOtherExternalStorageStateUnknow;

	public static String getOtherExternalStorageDirectory() {
		if (otherExternalStorageState == kOtherExternalStorageStateUnable)
			return null;
		if (otherExternalStorageState == kOtherExternalStorageStateUnknow) {
			FstabReader fsReader = new FstabReader();
			if (fsReader.size() <= 0) {
				otherExternalStorageState = kOtherExternalStorageStateUnable;
				return null;
			}
			List<StorageInfo> storages = fsReader.getStorages();
			/* 锟斤拷锟节匡拷锟矫空硷拷小锟斤拷100M锟侥癸拷锟截节碉拷锟斤拷缘锟�*/
			long availableSpace = 100 << (20);
			String path = null;
			for (int i = 0; i < storages.size(); i++) {
				StorageInfo info = storages.get(i);
				if (info.getAvailableSpace() > availableSpace) {
					availableSpace = info.getAvailableSpace();
					path = info.getPath();
				}
			}
			otherExternalStorageDirectory = path;
			if (otherExternalStorageDirectory != null) {
				otherExternalStorageState = kOtherExternalStorageStateIdle;
			} else {
				otherExternalStorageState = kOtherExternalStorageStateUnable;
			}
		}
		return otherExternalStorageDirectory;
	}

	public static long getInternalStorageAvailableSpace() {
		String path = getInternalStorageDirectory();
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 锟斤拷取锟街伙拷锟节诧拷锟杰的存储锟秸硷拷
	 * 
	 * @return
	 */
	public static long getInternalStorageTotalSpace() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public final static String getExternalStorageDirectory() {
		return Environment.getExternalStorageDirectory() + File.separator + "";
	}

	public final static String getExternalStoragePublicDirectory(String type) {
		return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
	}

	public final static String getSdcard2StorageDirectory() {
		return "/mnt/sdcard2";
	}

	public final static String getEmmcStorageDirectory() {
		return "/mnt/emmc";
	}

	private static String externalStoragePrivateDirectory;

	static String getExternalPrivateFilesDirectory() {
		if (externalStoragePrivateDirectory == null)
			externalStoragePrivateDirectory = context.getExternalFilesDir(null).getAbsolutePath();
		return externalStoragePrivateDirectory;
	}

	private static String internalStorageDirectory;

	public final static String getInternalStorageDirectory() {
		if (TextUtils.isEmpty(internalStorageDirectory)) {
			File file = context.getFilesDir();
			internalStorageDirectory = file.getAbsolutePath();
			if (!file.exists())
				file.mkdirs();
			String shellScript = "chmod 705 " + internalStorageDirectory;
			runShellScriptForWait(shellScript);
		}
		return internalStorageDirectory;
	}

	public static boolean isInternalStoragePath(String path) {
		String rootPath = getInternalStorageDirectory();
		if (path != null && path.startsWith(rootPath))
			return true;
		return false;
	}

	public static String getFileName(String file) {
		if (file == null)
			return null;
		int index = file.lastIndexOf("/");
		return file.substring(index + 1);
	}

	public static boolean runShellScriptForWait(final String cmd) throws SecurityException {
		ShellThread thread = new ShellThread(cmd);
		thread.setDaemon(true);
		thread.start();
		int k = 0;
		while (!thread.isReturn() && k++ < 20) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (k >= 20) {
			thread.interrupt();
		}
		return thread.isSuccess();
	}

	/** 锟斤拷锟斤拷执锟斤拷shell锟脚憋拷锟斤拷锟竭筹拷 */
	private static class ShellThread extends Thread {
		private boolean isReturn;
		private boolean isSuccess;
		private String cmd;

		public boolean isReturn() {
			return isReturn;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		/**
		 * @param cmd
		 *            shell锟斤拷锟斤拷锟斤拷锟斤拷
		 * @param isReturn
		 *            锟竭筹拷锟角凤拷锟窖撅拷锟斤拷锟斤拷
		 * @param isSuccess
		 *            Process锟角凤拷执锟叫成癸拷
		 */
		public ShellThread(String cmd) {
			this.cmd = cmd;
		}

		@Override
		public void run() {
			try {
				Runtime runtime = Runtime.getRuntime();
				Process proc;
				try {
					proc = runtime.exec(cmd);
					isSuccess = (proc.waitFor() == 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				isSuccess = true;
			} catch (InterruptedException e) {
			}
			isReturn = true;
		}

	}

	public static class FstabReader {
		public FstabReader() {
			init();
		}

		public int size() {
			return storages == null ? 0 : storages.size();
		}

		public List<StorageInfo> getStorages() {
			return storages;
		}

		final List<StorageInfo> storages = new ArrayList<StorageInfo>();

		public void init() {
			File file = new File("/system/etc/vold.fstab");
			if (file.exists()) {
				FileReader fr = null;
				BufferedReader br = null;
				try {
					fr = new FileReader(file);
					if (fr != null) {
						br = new BufferedReader(fr);
						String s = br.readLine();
						while (s != null) {
							if (s.startsWith("dev_mount")) {
								/* "\s"转锟斤拷锟狡ワ拷锟斤拷锟斤拷锟斤拷锟叫ｏ拷锟斤拷/全锟角空革拷 */
								String[] tokens = s.split("\\s");
								String path = tokens[2]; // mount_point
								StatFs stat = new StatFs(path);

								if (null != stat && stat.getAvailableBlocks() > 0) {

									long availableSpace = stat.getAvailableBlocks() * stat.getBlockSize();
									long totalSpace = stat.getBlockCount() * stat.getBlockSize();
									StorageInfo storage = new StorageInfo(path, availableSpace, totalSpace);
									storages.add(storage);
								}
							}
							s = br.readLine();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fr != null)
						try {
							fr.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if (br != null)
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		}
	}

	static class StorageInfo implements Comparable<StorageInfo> {
		private String path;
		private long availableSpace;
		private long totalSpace;

		StorageInfo(String path, long availableSpace, long totalSpace) {
			this.path = path;
			this.availableSpace = availableSpace;
			this.totalSpace = totalSpace;
		}

		@Override
		public int compareTo(StorageInfo another) {
			if (null == another)
				return 1;

			return this.totalSpace - another.totalSpace > 0 ? 1 : -1;
		}

		long getAvailableSpace() {
			return availableSpace;
		}

		long getTotalSpace() {
			return totalSpace;
		}

		String getPath() {
			return path;
		}
	}
	
	public static String getSavePath(long saveSize) {
		String savePath = null;
		if (AppUtil.getExternaltStorageAvailableSpace() > saveSize) {
			savePath = AppUtil.getExternalStorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (AppUtil.getSdcard2StorageAvailableSpace() > saveSize) {
			savePath = AppUtil.getSdcard2StorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (AppUtil.getEmmcStorageAvailableSpace() > saveSize) {
			savePath = AppUtil.getEmmcStorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (AppUtil.getOtherExternaltStorageAvailableSpace() > saveSize) {
			savePath = AppUtil.getOtherExternalStorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (AppUtil.getInternalStorageAvailableSpace() > saveSize) {
			savePath = AppUtil.getInternalStorageDirectory() + File.separator;
		}
		return savePath;
	}
	

	public static boolean checkConnection(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	
	public static long flagTimeMillis=0;
	public static boolean isDoubleClick(){
		long curTimeMillis = System.currentTimeMillis();
		boolean result = flagTimeMillis>0 && (curTimeMillis-flagTimeMillis)<500;
		flagTimeMillis = curTimeMillis;
		return result;
	}
	
	
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI")) {
			return true;
		}
		return false;
	}
	
	public static int getScreenWidth(Activity mContext) {
		DisplayMetrics metric = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
	    int width = metric.widthPixels;   
	    return width;
	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
	/**
	 * 测试网络
	 */
	public static boolean TestNetWork(Context context) {
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetInfo == null) {
				return false;
			}
			boolean netInfo = activeNetInfo.isAvailable();
			if (!netInfo) {
				return false;
			}
		}
		return true;
	}
}