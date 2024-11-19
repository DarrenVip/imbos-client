package com.imbos.chat;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.util.Constants;

public class QRCardActivity extends SkinActivity{
	private ImageView imageView;
	private ProgressBar  progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_activity);
		
		String id = getIntent().getStringExtra(Intents.EXTRA_ID);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		imageView = (ImageView) findViewById(R.id.image1);
		imageView.setBackgroundResource(R.drawable.card_container_bg);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		try {
			imageView.setVisibility(View.VISIBLE);
			Bitmap bitmap = create2DCode(Constants.QRCODE_PREFIX_MB+id,300,300);
			imageView.setImageBitmap(bitmap);
			
		} catch (WriterException e) {
			e.printStackTrace();
		}finally{
			progressBar.setVisibility(View.GONE);
		}
	}
	public Bitmap create2DCode(String str,int w,int h) throws WriterException {  
		
		Hashtable<EncodeHintType,String> hints = new Hashtable<EncodeHintType,String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		
		
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败  
        BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, w, h,hints);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        //二维矩阵转为一维像素数组,也就是一直横着排了  
        int[] pixels = new int[width * height];  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if(matrix.get(x, y)){  
                    pixels[y * width + x] = 0xff000000;  
                }  
                  
            }  
        }  
          
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        //通过像素数组生成bitmap,具体参考api  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;  
    }
}
