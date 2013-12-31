package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.JohnnyWorks.videoNpix.LazyLoad.ImageCallback;
import com.zunidata.zunidataapi.ZunidataEnvironment;

public class MainActivity extends Activity {
	private LazyLoad loader;
	private static final String TAG = "video2pix";
	public static final String URL_PREFIX = "video2pix:";
	private ImageButton[] imgViews;
	private IdleMonitorUtil idleTimer;
	private File vPath = null;
	private SDCardWatcher sdCardWatcher;
	private WebView webView;
	private LinearLayout lay01;
	private LinearLayout lay02;
	Timer timer;
	private int playMaxLenth;
	private String DIR_PREFIX;
	private ImageView barcodeimg;
	private boolean delayScrSaver;
	private SoundPool soundPool;
	private int spId;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		System.out.println("===oncreat");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		ExitApplication.getInstance().addActivity(this);
		loader=new LazyLoad();
		SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		playMaxLenth = preferences.getInt("pixnum", 0);
		DIR_PREFIX = "/video" + playMaxLenth + "pix/";
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        spId = soundPool.load(this,R.raw.di, 1);
        onConfigurationChanged(getResources().getConfiguration());
		if (!ZuniMachineLib.useInternalMem) {
			sdCardWatcher = new SDCardWatcher();
			sdCardWatcher.registerSDCardStateChangeListener(this,
					new SDRemovedListener() {

						@Override
						public void onSDRemoved() {
							Toast.makeText(MainActivity.this,
									R.string.sdcard_not_found,
									Toast.LENGTH_SHORT).show();
							MainActivity.this.finish();
							HideStatusBar.disable();
						}
					});

		}
		
		System.out.println("==="+getWindowManager().getDefaultDisplay().getHeight());
		imgViews =  new ImageButton[playMaxLenth];
		barcodeimg=(ImageView) findViewById(R.id.imageView1);
    	imgViews[0] = (ImageButton) findViewById(R.id.imageButton1);
    	imgViews[1] = (ImageButton) findViewById(R.id.imageButton2);
    	
        if (playMaxLenth == 4) {
        	imgViews[2] =  (ImageButton) findViewById(R.id.imageButton3);
        	imgViews[3] =  (ImageButton) findViewById(R.id.imageButton4);
        } else if(playMaxLenth == 6) {
        	imgViews[2] = (ImageButton) findViewById(R.id.imageButton3);
        	imgViews[3] = (ImageButton) findViewById(R.id.imageButton4);
        	imgViews[4] = (ImageButton) findViewById(R.id.imageButton5);
        	imgViews[5] = (ImageButton) findViewById(R.id.imageButton6);
        	
        }
	}
	
	@Override
	protected void onResume() {
		 
		System.out.println("===onre");
		
		readStrFromSD();
		if (idleTimer == null) {
			idleTimer = new IdleMonitorUtil();
		}
       
		if (idleTimer != null) {
			delayScrSaver = false;
//			idleTimer.stopTimer();
//			idleTimer.startTimer();
		}
		ZuniMachineLib.logToText("MainAct is active.", DIR_PREFIX);
		super.onResume();
	}

	private void readStrFromSD() {
//		try {
			if (ZuniMachineLib.useInternalMem) {
				vPath = new File(ZunidataEnvironment.getInternalStoragePath()
						+ DIR_PREFIX );
			} else {

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_REMOVED)) {
					Toast.makeText(MainActivity.this,
							R.string.sdcard_not_found,
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					vPath = new File(ZunidataEnvironment.getExternalStoragePath()
							+ DIR_PREFIX );
				}

			}
			vPath.mkdirs();
					File ff=new File(vPath.getAbsolutePath()+"/thumbnail");
					if(!(ff.exists()))
						ff.mkdir();
					for (int i = 0; i < imgViews.length; i++) {						
						String dir=vPath.getAbsolutePath() + "/thumbnail/"
								+ (i + 1) + ".jpg";
						loader.loadDrawable(dir, new lazyloadc(imgViews[i]));						
						imgViews[i].setTag(vPath.getAbsolutePath()
								+ "/" + (i + 1) + ".mp4");
					}
					if(new File(GlobalString.background).exists()){
						ARelativeLayout background=(ARelativeLayout) findViewById(R.id.ARelativeLayout1);
						Bitmap bt=BitmapFactory.decodeFile(GlobalString.background);
						Drawable bb=new BitmapDrawable(bt);
						background.setBackgroundDrawable(bb);
					}
		return;
	}
	
	public class lazyloadc implements ImageCallback{
		ImageButton imageButton;
		public lazyloadc(Object imageButton) {
			this.imageButton=(ImageButton) imageButton;
		}
		
		public void ImageLoaded(Object imageDrawable) {
			Bitmap bitmap=(Bitmap) imageDrawable;
			Drawable dr=new BitmapDrawable(bitmap);
			imageButton.setBackgroundDrawable(dr);		
		}
	}
	
	public void IntentToPlayer(String videoPath) {
		Intent intent = new Intent(MainActivity.this, Player.class);
		ZuniMachineLib.logToText("Intent to Player...{" + videoPath + "}", DIR_PREFIX);
		intent.putExtra("playFile", videoPath);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	class IdleMonitorUtil extends Mod_IdleMonitorUtil {
		protected void startScreenSaverAct() {
			idleTimer.stopTimer();
			Intent intent = new Intent(MainActivity.this, Player.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			overridePendingTransition(0, 0);
			startActivity(intent);
		}

		@Override
		protected void timerTick(int counter) {
		}

	}
	
	@Override
	protected void onPause() {
		System.out.println("===onpau");
		if (idleTimer != null) {
			idleTimer.stopTimer();
			idleTimer = null;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		System.out.println("===ondes");
		if (sdCardWatcher != null)
			sdCardWatcher.unRegisterSDCardStateChangeListener(this);
		super.onDestroy();
	}
	

	
	final Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==1234){
				idleTimer.startTimer();
				barcodeimg.setVisibility(View.GONE);
				for(int i=0;i<imgViews.length;i++)imgViews[i].setVisibility(View.VISIBLE);
			}
				
		}		
	};
	
	Runnable back=new Runnable() {
		
		@Override
		public void run() {
			handler.sendEmptyMessage(1234);					
		}
	};
	Context c=this;
	Barcode barcode=new Barcode(GlobalString.sdcard+"/",this) {	
		@Override
		void showPic() {
			
	//		Toast.makeText(mContext, res, Toast.LENGTH_LONG).show();
			String pic=where+"barcode/"+GlobalString.orientation+"/"+res+".jpg";
			System.out.println("==="+pic);
//			System.out.println(res);
			handler.removeCallbacks(back);
			
			if(res=="")return;
			if(new File(pic).exists()){
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(pic));
				barcodeimg.setBackgroundDrawable(da);
				soundPool.play(spId, 1, 1, 1, 0, 1);
				for(int i=0;i<imgViews.length;i++)imgViews[i].setVisibility(View.INVISIBLE);
				barcodeimg.setVisibility(View.VISIBLE);
				res="";				
				handler.postDelayed(back, 5000);
			}else{
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(where+"noinformation.jpg"));
				barcodeimg.setBackgroundDrawable(da);
				for(int i=0;i<imgViews.length;i++)imgViews[i].setVisibility(View.INVISIBLE);
				barcodeimg.setVisibility(View.VISIBLE);
				res="";					
				handler.postDelayed(back, 5000);			
			}
			
		}
	};
	
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(back);
			handler.sendEmptyMessage(1234);
			return true;
		}
		
		return false;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return false;
		}
		idleTimer.resetTimer(-1);
		barcode.show(keyCode);
		return true;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		System.out.println(getWindowManager().getDefaultDisplay().getWidth());
		System.out.println(getWindowManager().getDefaultDisplay().getHeight());
		switch(newConfig.orientation){		
		case Configuration.ORIENTATION_PORTRAIT:
			GlobalString.orientation="PORTRAIT";
			 if (playMaxLenth == 2) {
				 switch(getWindowManager().getDefaultDisplay().getWidth()){
				 case 768:
					 System.out.println("here");
					 setContentView(R.layout.standby2_p_1366);
					 break;
				 case 600:
					 setContentView(R.layout.standby2_p_1024);
					 break;
				 }
		        	
		        } else if (playMaxLenth == 4) {
		        	switch(getWindowManager().getDefaultDisplay().getWidth()){
					 case 768:
						 System.out.println("here");
						 setContentView(R.layout.standby4_p_1366);
						 break;
					 case 600:
						 setContentView(R.layout.standby4_p_1024);
						 break;
					 }
			        	
		        } else if(playMaxLenth == 6) {
		        	switch(getWindowManager().getDefaultDisplay().getWidth()){
					 case 768:
						 System.out.println("here");
						 setContentView(R.layout.standby6_p_1366);
						 break;
					 case 600:
						 setContentView(R.layout.standby6_p_1024);
						 break;
					 }
		        } 
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			GlobalString.orientation="LANDSCAPE";
			 if (playMaxLenth == 2) {
				 switch(getWindowManager().getDefaultDisplay().getWidth()){
				 case 1366:
					 setContentView(R.layout.standby2_l_1366);
					 break;
				 case 1024:
					 setContentView(R.layout.standby2_l_1024);
					 break;
				 }
		        } else if (playMaxLenth == 4) {
		        	switch(getWindowManager().getDefaultDisplay().getWidth()){
					 case 1366:
						 setContentView(R.layout.standby4_l_1366);
						 break;
					 case 1024:
						 setContentView(R.layout.standby4_l_1024);
						 break;
					 }
		        } else if(playMaxLenth == 6) {
		        	switch(getWindowManager().getDefaultDisplay().getWidth()){
					 case 1366:
						 setContentView(R.layout.standby6_l_1366);
						 break;
					 case 1024:
						 setContentView(R.layout.standby6_l_1024);
						 break;
					 }
		        } 
			break;
		}
		super.onConfigurationChanged(newConfig);
	}
}
