package com.imbos.chat.util;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtil
{
	
	private static String FILE_DIR=null;
	public static String SDPATH = Environment.getExternalStorageDirectory().toString()+"/";

	public static boolean existSDCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static boolean mkdir(String dirName){
		File file = new File(SDPATH + dirName);		
		return file.mkdirs();
	}
	public static String getFileDir(Context ctx){
		if(FILE_DIR==null){
			 if(existSDCard()){
				String packageName = ctx.getPackageName();
				File file = new File("/mnt/sdcard/"+packageName.substring(packageName.indexOf(".")+1)
						,"files");
				if(!file.exists())
					file.mkdirs();
				FILE_DIR = file.getPath();
			 }else if(new File("/mnt/sdcard2/").exists()){
				 File file = new File("/mnt/sdcard2"
							,"files");
					if(!file.exists())
						file.mkdirs();
					FILE_DIR = file.getPath();
			 }else if(new File("/mnt/emmc/").exists()){
				 File file = new File("/mnt/emmc"
							,"files");
					if(!file.exists())
						file.mkdirs();
					FILE_DIR = file.getPath();
			 }else{
				FILE_DIR = ctx.getFilesDir().getPath();
			 }
		}
		
		return FILE_DIR;
	}
	
	public static File queryFile(Context ctx,Uri uri){
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = ctx.getContentResolver().query(uri,proj,null,null,null);
		int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String img_path = cursor.getString(actual_image_column_index);
		cursor.close();
		return new File(img_path);
	}

	public static String getRealPath(Context ctx, Uri fileUrl) {
		String fileName = null;
		Uri filePathUri = fileUrl;
		if (fileUrl != null) {
			if (fileUrl.getScheme().toString().compareTo("content") == 0) // content://开头的uri
			{
				Cursor cursor = ctx.getContentResolver().query(fileUrl, null,
						null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					int column_index = 0;// cursor.getColumnIndexOrThrow(Str.***.***.DATA);
					fileName = cursor.getString(column_index); // 取出文件路径
					if (!fileName.startsWith("/mnt")) {
						// 检查是否有”/mnt“前缀

						fileName = "/mnt" + fileName;
					}
					cursor.close();
				}
			} else if (fileUrl.getScheme().compareTo("file") == 0) // file:///开头的uri
			{
				fileName = filePathUri.toString();
				fileName = filePathUri.toString().replace("file://", "");
				// 替换file://
				if (!fileName.startsWith("/mnt")) {
					// 加上"/mnt"头
					fileName += "/mnt";
				}
			}
		}
		return fileName;
	}
	public static boolean hashFileBrowserActivity(Context ctx,Intent intent){
		List<ResolveInfo> acts =  ctx.getPackageManager().queryIntentActivities(  
		        intent, 0);  
		if(acts==null || acts.isEmpty())
			return false;
		for (ResolveInfo resolveInfo : acts) {
			Log.d("ResolveInfo:",resolveInfo.activityInfo.name);
			
		}
		return true;
	}
	public static Intent getFileBrowserActivity(Context ctx){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("*/*");
		List<ResolveInfo> acts =  ctx.getPackageManager().queryIntentActivities(  
		        intent, 0);  
		if(acts==null || acts.isEmpty())
			return null;
		for (ResolveInfo resolveInfo : acts) {
			if(resolveInfo.activityInfo.name.indexOf("file")>-1){
				intent.setClassName(ctx, resolveInfo.activityInfo.name);
				intent.setClassName( resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name);
				return intent;
			}
		}
		return null;
	}

}
