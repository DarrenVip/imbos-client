package com.imbos.chat.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.setting.Config;
import com.imbos.chat.util.BitmapUtil;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class AsyncImageLoader {
	private static final String TAG = "AsyncImageLoader";

	private HashMap<String, SoftReference<Drawable>> imageCache;
	private BlockingQueue<Runnable> queue;
	private ThreadPoolExecutor executor;
	private Context context;
	public AsyncImageLoader(Context context) {
		this.context = context;
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		// 线程池：最大5条，每次执行：1条，空闲线程结束的超时时间：180秒
		queue = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(1, 5, 180, TimeUnit.SECONDS, queue);
	}
	public static String builderUrl(String file){
		if(file==null ||"".equals(file))
			return null;
		
		Config config = ChatApp.getConfig();
		return "http://"+config.saasHost+":"+config.saasPort+"/"+file;
	}
	public static String imageMiniFileName(String fileName){
		if(fileName==null ||"".equals(fileName))
			return null;
		String miniFileName=fileName.substring(0, fileName.lastIndexOf('.'))+"_mini"+fileName.substring(fileName.lastIndexOf('.'),fileName.length());
		return miniFileName;

	}
	
	public void loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
		
		if(null==imageUrl){
			imageCallback.imageLoaded(null, imageUrl);
			return;
		}
		
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				imageCallback.imageLoaded(drawable, imageUrl);
				return;
			}
		}
		
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};

		// 用线程池来做下载图片的任务
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Drawable drawable = null;
				if(imageUrl.startsWith("http"))
					drawable= loadImageFromUrl(context, imageUrl);
				else
					drawable= loadImageFromLocal(context, imageUrl);
				
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		});
	}
	public static Drawable loadImageFromLocal(Context context, String file) {
		Drawable drawable = null;
		try {
			File srcFile = new File(file);
			if(srcFile.exists()){
				Bitmap bitmap = new BitmapUtil().decodeFile(srcFile, 300); 
				drawable  = new BitmapDrawable(bitmap);
			}else{
				Log.e(TAG,"无本地缓存文件:"+srcFile.getPath());
			}
				
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "读取本地缓存文件失败:"+file,e);
		}
		return drawable;
	}
	// 网络图片先下载到本地cache目录保存，以imagUrl的图片文件名保存。如果有同名文件在cache目录就从本地加载
	public static Drawable loadImageFromUrl(Context context, String imageUrl) {
		Drawable drawable = null;
		if (imageUrl == null)
			return null;
		String fileName = "";

		// 获取url中图片的文件名与后缀
		if (imageUrl != null && imageUrl.length() != 0) {
			fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		}

		// Log.i(TAG,"imagePath = " + imagePath);
		File file = new File(context.getCacheDir(), fileName);// 保存文件
		System.out.println(context.getCacheDir());
		if (!file.exists() && !file.isDirectory()) {
			try {
				//Log.e(TAG,"下载："+ imageUrl);
				// 可以在这里通过文件名来判断，是否本地有此图片
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = new URL(imageUrl).openStream();
				int data = is.read();
				while (data != -1) {
					fos.write(data);
					data = is.read();
				}
				fos.close();
				is.close();
				drawable = Drawable.createFromPath(file.toString());
			} catch (IOException e) {
				Log.e(TAG, e.toString() + "图片下载及保存时出现异常！");
				
			}
		} else {
			drawable = Drawable.createFromPath(file.toString());
		}
		return drawable;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
	
	public void destroy(){
		executor.shutdown();
		clearCache();
	}
	
	public void destroyImage(String imageUrl){
		if(imageCache!=null&&imageCache.containsKey(imageUrl)){
			
			SoftReference<Drawable> reference = imageCache.get(imageUrl);
			imageCache.remove(imageUrl);
			if(reference!=null){
				Drawable drawable = reference.get();
				if(drawable!=null)
					((BitmapDrawable)drawable).getBitmap().recycle();
			}
		}
		System.gc();
	}
	
	public void clearCache(){
		imageCache.clear();
		System.gc();
	}
}
