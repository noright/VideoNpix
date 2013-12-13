package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	}
	
	@Override
	protected void onResume() {
		
        if (playMaxLenth == 2) {
        	setContentView(R.layout.standby2);
        } else if (playMaxLenth == 4) {
        	setContentView(R.layout.standby4);
        } else if(playMaxLenth == 6) {
        	setContentView(R.layout.standby6);
        } 

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
		if(new File(GlobalString.background).exists()){
			System.out.println("=======111");
			ARelativeLayout background=(ARelativeLayout) findViewById(R.id.ARelativeLayout1);
			Bitmap bt=BitmapFactory.decodeFile(GlobalString.background);
			Drawable bb=new BitmapDrawable(bt);
			background.setBackgroundDrawable(bb);
		}
		
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
       
				
		readStrFromSD();
		if (idleTimer == null) {
			idleTimer = new IdleMonitorUtil();
		}
       
		if (idleTimer != null) {
			delayScrSaver = false;
			idleTimer.stopTimer();
			idleTimer.startTimer();
		}
		ZuniMachineLib.logToText("MainAct is active.", DIR_PREFIX);
		super.onResume();
	}

	private void readStrFromSD() {
		try {
			
			System.out.println("iamhere");
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
			Log.v(TAG, "Reading data from " + vPath.getAbsolutePath());
			if (vPath.exists()) {


				if ((new File(vPath.getAbsolutePath() + "/index.html"))
						.exists()) {
					Log.v(TAG, "found custom html file! use it.");
					webView.loadUrl("file://" + vPath.getAbsolutePath()
							+ "/index.html");
					webView.setBackgroundColor(Color.TRANSPARENT);
					webView.setBackgroundDrawable(null);
					webView.setBackgroundResource(0);
					WebSettings websettings = webView.getSettings();
					websettings.setSupportZoom(false);
					websettings.setBuiltInZoomControls(false);
					websettings.setJavaScriptEnabled(false);
				

					webView.setWebViewClient(new WebViewClient() {
						@Override
						public boolean shouldOverrideUrlLoading(WebView view,
								String url) {
							

							if (url.startsWith(URL_PREFIX)) {
								IntentToPlayer(vPath.getAbsolutePath() + "/"
										+ url.replace(URL_PREFIX, ""));
								return true;
							}

							return false;
						}

					});

					lay01.setVisibility(View.GONE);
					lay02.setVisibility(View.GONE);
					webView.setVisibility(View.VISIBLE);
				} else {	
					
					File ff=new File(vPath.getAbsolutePath()+"/thumbnail");
					if(!(ff.exists()))
						ff.mkdir();
					for (int i = 0; i < imgViews.length; i++) {						
						String dir=vPath.getAbsolutePath() + "/thumbnail/"
								+ (i + 1) + ".jpg";
						//TODO
						loader.loadDrawable(dir, new lazyloadc(imgViews[i]));						
						imgViews[i].setTag(vPath.getAbsolutePath()
								+ "/" + (i + 1) + ".mp4");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
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
		if (idleTimer != null) {
			idleTimer.stopTimer();
			idleTimer = null;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
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
	Barcode barcode=new Barcode("/sdcard/barcode/",this) {	
		@Override
		void showPic() {
			handler.removeCallbacks(back);
			System.out.println(res);
			if(res=="")return;	
			if(new File(where+res+".png").exists()){
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(where+res+".jpg"));
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
		idleTimer.resetTimer(-1);
		barcode.show(keyCode);
		return true;
	}
}
