package com.imbos.chat.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
/**
 * 
 * @author Kingly
 * 2012-1-17
 */
public class BitmapUtil {
	public BitmapFactory.Options CreateOptions(){
		 BitmapFactory.Options options= new BitmapFactory.Options();  
	   	 options.inPreferredConfig = Bitmap.Config.RGB_565;   
	   	 options.inPurgeable = true;  
	   	 options.inInputShareable = true;
	   	 return options;
	}
	public Bitmap decodeFile(String imageFile,int minSideLength, int maxNumOfPixels)
	{
		BitmapFactory.Options opts = this.CreateOptions();
		if(!(minSideLength==-1 || maxNumOfPixels==-1)){
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageFile, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, minSideLength*maxNumOfPixels);  
		}
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imageFile, opts);
	}
	public Bitmap decodeStream(InputStream inputStream,int minSideLength, int maxNumOfPixels)
	{
		BitmapFactory.Options opts = this.CreateOptions();
		if(!(minSideLength==-1 || maxNumOfPixels==-1)){
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(inputStream, null, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, minSideLength*maxNumOfPixels);  
		}
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(inputStream, null, opts);
	}
	public Bitmap decodeStream(byte[] bytes,int minSideLength, int maxNumOfPixels)
	{
		BitmapFactory.Options opts = this.CreateOptions();
		if(!(minSideLength==-1 || maxNumOfPixels==-1)){
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes,0,bytes.length, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, minSideLength*maxNumOfPixels); 
		}
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bytes,0,bytes.length, opts);
	}
	public Bitmap decodeFile(File file,int maxsize) throws Exception{
	    //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(file), null, o);
        double scale = 1;
        if (o.outHeight > maxsize || o.outWidth > maxsize) {
            scale = Math.pow(2, (int) Math.round(Math.log(maxsize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize =(int)scale;
	    return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
	}
	
	public void decodeFile(File srcFile,File targetFile,int maxsize)throws Exception{
		Bitmap bitmap =  decodeFile(srcFile, maxsize);
		if(bitmap!=null){
			bitmap.compress(CompressFormat.JPEG,100,new FileOutputStream(targetFile));
			bitmap.recycle();
		}
		
	}
	public Bitmap decodeStream(InputStream stream,int maxsize) throws Exception{
	    //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, o);
        double scale = 1;
        if (o.outHeight > maxsize || o.outWidth > maxsize) {
            scale = Math.pow(2, (int) Math.round(Math.log(maxsize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize =(int)scale;
	    return BitmapFactory.decodeStream(stream, null, o2);
	}
	
	public byte[] encodeBitmap(Bitmap bitmap,int quality) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG,quality,byteStream);
		try {
			byteStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteStream.toByteArray();
	}
	public Bitmap decodeByteArray(byte[] bytes,int maxsize) throws Exception{
	    //Decode image size
		maxsize+=20;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length,o);
        double scale = 1;
        if (o.outHeight > maxsize || o.outWidth > maxsize) {
            scale = Math.pow(2, (int) Math.round(Math.log(maxsize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize =(int)scale;
	    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,o2);
	}
	
	
	public static int computeSampleSize(BitmapFactory.Options options,

	        int minSideLength, int maxNumOfPixels) {

	    int initialSize = computeInitialSampleSize(options, minSideLength,

	            maxNumOfPixels);



	    int roundedSize;

	    if (initialSize <= 8) {

	        roundedSize = 1;

	        while (roundedSize < initialSize) {

	            roundedSize <<= 1;

	        }

	    } else {

	        roundedSize = (initialSize + 7) / 8 * 8;

	    }



	    return roundedSize;

	}
	private static int computeInitialSampleSize(BitmapFactory.Options options,

	        int minSideLength, int maxNumOfPixels) {

	    double w = options.outWidth;

	    double h = options.outHeight;



	    int lowerBound = (maxNumOfPixels == -1) ? 1 :

	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

	    int upperBound = (minSideLength == -1) ? 128 :

	            (int) Math.min(Math.floor(w / minSideLength),

	            Math.floor(h / minSideLength));



	    if (upperBound < lowerBound) {

	        // return the larger one when there is no overlapping zone.

	        return lowerBound;

	    }



	    if ((maxNumOfPixels == -1) &&

	            (minSideLength == -1)) {

	        return 1;

	    } else if (minSideLength == -1) {

	        return lowerBound;

	    } else {

	        return upperBound;

	    }

	} 
	public Bitmap RotateImage(Bitmap srcBitmap,int rotate){
		Matrix m = new Matrix();
		m.postRotate(90); // ��ת90��
		return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
				srcBitmap.getHeight(), m, false);
	}
}
