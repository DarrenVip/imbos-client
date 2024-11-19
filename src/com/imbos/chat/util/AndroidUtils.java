package com.imbos.chat.util;

import java.io.File;
import java.util.Vector;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class AndroidUtils {
	/**
	 * Returns version code
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns version name
	 * 
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean extisSDCard(){
		return Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
	}
	public static File getDownloadCacheDirectory(){
		return Environment.getDownloadCacheDirectory();
	}
	
	
	public static float averageSpace(float longer, float shorter) {
		if(shorter > longer){
			return 0;
		}
		float space = Math.abs(longer - shorter);
		return space / 2;
	}

	public static float getFontHeight(Paint paint) {
		Rect rect = new Rect();
		paint.getTextBounds("高", 0, 1, rect);
		float height = rect.height();
		return height;
	}

	public static float getFontWidth(Paint paint, int num) {
		Rect rect = new Rect();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sb.append("宽");
		}
		String resultStr = sb.toString();
		paint.getTextBounds(resultStr, 0, resultStr.length(), rect);
		float width = rect.width();
		return width;
	}

	public static float getFontWidth(Paint paint, String str) {
		Rect rect = new Rect();
		paint.getTextBounds(str, 0, str.length(), rect);

		return rect.width();
	}

	/**
	 * 将字符串按固定宽分行
	 * 
	 * @param paint
	 * @param str
	 * @param rowWidth
	 * @return
	 */
	public static String[] getRowsValue(Paint paint, String str, float rowWidth) {
		if(str == null || str.length() < 1){
			return null;
		}
		float allWidth = getFontWidth(paint, str);
		if (rowWidth < getFontWidth(paint, 1)) {
			// 大于等于一个字的宽度
			rowWidth = getFontWidth(paint, 1);
		}
		Vector rows = new Vector();
		StringBuffer temp = new StringBuffer();
		int charNum = str.length();
		for(int i = 0; i < charNum; i++){
			// 循环各字符
			char c = str.charAt(i);
			if(c == '\n'){
				rows.add(temp.toString());
				temp = null;
				temp = new StringBuffer();
			}else{
				if(getFontWidth(paint, temp.toString() + c) > rowWidth){
					rows.add(temp.toString());
					temp = null;
					temp = new StringBuffer();
					temp.append(c);
				}else{
					temp.append(c);
				}
			}
		}
		if(temp.length() > 0){
			rows.add(temp.toString());
		}
		String[] rowsStr = new String[rows.size()];
		rows.copyInto(rowsStr);
		return rowsStr;
	}
	/**
	 * 字符串转整型 
	 * @param str
	 * @return
	 */
	public static int getIntByString(String str){
		int result = 0;
		try{
			result = Integer.parseInt(str);
		}catch(Exception ex){
			result = 0;
		}
		return result;
	}
	/**
	 * 检测输入数字
	 * @param number
	 * @return
	 */
	public static String parseNumber(String number){
		String result = "";
		try{
			Float.parseFloat(number);
			result = number;
		}catch(Exception ex){
			result = "";
		}
		return result;
	}
	
	
	
	/**
	 * px转dip
	 * @param context对象
	 * @param px值
	 * @return dip值
	 */
	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(pxValue / scale + 0.5f);  
	}
	
	/**
	 * dip转px
	 * @param context对象
	 * @param dip值
	 * @return px值
	 */
	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	}
	
	/**
	 * 获取手机电话号码
	 * 函数功能说明 
	 * 修改者名字 
	 * 修改日期 
	 * 修改内容
	 * @author wanxianze@gmail.com 2013-1-10
	 * @return
	 * String
	 */
	public static String getTelephone(Context ctx){
		TelephonyManager telMgr= (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE); 
		return telMgr.getLine1Number();
	}
	
	private static long lastClickTime;
	
	/**
	 * 防止频繁点击
	 * @return
	 */
	public static  boolean isFastDoubleClick() {   
		        long time = System.currentTimeMillis();   
		        long timeD = time - lastClickTime;   
		        if ( 0 < timeD && timeD < 500) {      
		            return true;      
		        }      
		        lastClickTime = time;      
		        return false;      
		    } 
}
