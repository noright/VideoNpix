package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;

public class LazyLoad {
	public Bitmap loadDrawable(final String imagePath,final ImageCallback callback){
		final Handler handler=new Handler(){
			public void handleMessage(Message msg) {
				callback.ImageLoaded(msg.obj);
			}
		};
		new Thread(){
			public void run(){
				BitmapFactory.Options opts=new BitmapFactory.Options();
				opts.inSampleSize=1;
				Bitmap bitmap=null;
				String path=null;
				if(new File(imagePath).exists()){
					bitmap=BitmapFactory.decodeFile(imagePath, opts);
				}			
				else{
					System.out.println(imagePath);
					path=imagePath.replace("thumbnail/", "");
					path=path.replace("jpg", "mp4");
					System.out.println(path);
					new Thumbnail().saveThumbnail(path.replace(".jpg", ".mp4"), imagePath);
					bitmap=BitmapFactory.decodeFile(imagePath, opts);
				}
				
			//TODO	
			//	bitmap=getRoundedCornerBitmap(bitmap, (float) 0.05);
			//	bitmap=createReflectionImageWithOrigin(bitmap);
				Message message=handler.obtainMessage(0, bitmap);
				handler.sendMessage(message);						
			}
			
			@SuppressWarnings("unused")
			private Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {  				
			    int w = bitmap.getWidth();  
			    int h = bitmap.getHeight();  
			    Matrix matrix = new Matrix(); 
			    matrix.postScale((float)h/w, 1);			    
			    bitmap=Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			    Bitmap output = Bitmap.createBitmap(h, h, Config.ARGB_8888); 
			    Canvas canvas = new Canvas(output);  
			    final int color = 0xff424242;  
			    final Paint paint = new Paint();  
			    final Rect rect = new Rect(0, 0, h, h);  
			    final RectF rectF = new RectF(rect);  
			    paint.setAntiAlias(true);  
			    canvas.drawARGB(0, 0, 0, 0);  
			    paint.setColor(color);
			    
			    canvas.drawRoundRect(rectF, roundPx*h, roundPx*h, paint);  
			    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
			    canvas.drawBitmap(bitmap, rect, rect, paint);  
			    return output;  
			}
			
			@SuppressWarnings("unused")
			private Bitmap createReflectionImageWithOrigin(Bitmap bitmap){
				final int reflectionGap = 1;
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);

				Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
				0, height/4, width, height/4, matrix, false);

				Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/4), Config.ARGB_8888);

				Canvas canvas = new Canvas(bitmapWithReflection);
				canvas.drawBitmap(bitmap, 0, 0, null);
				Paint deafalutPaint = new Paint();
				canvas.drawRect(0, height,width,height + reflectionGap,
				deafalutPaint);

				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
				Paint paint = new Paint();
				LinearGradient shader = new LinearGradient(0,
				bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
				paint.setShader(shader);
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
				canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

				return bitmapWithReflection;
				} 
			@SuppressWarnings("unused")
			private Bitmap getshaw(Bitmap bitmap){
				Bitmap ret=bitmap;
				
				
				
				return ret;
			}
		}.start();
		return null;
	}
	public interface ImageCallback {
        public void ImageLoaded(Object imageDrawable);
  //      public Bitmap getScreenShot(String path);
    }
	
}
