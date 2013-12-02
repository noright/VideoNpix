package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.conn.scheme.LayeredSocketFactory;

import com.JohnnyWorks.videoNpix.LazyLoad.ImageCallback;
import com.JohnnyWorks.videoNpix.R;
import com.example.barcode.KeyCodeTable;
import com.example.barcode.Main;
import com.zunidata.zunidataapi.ZunidataEnvironment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {
	private LazyLoad loader;
	private static final String TAG = "video2pix";
	public static final String URL_PREFIX = "video2pix:";
	private ImageButton[] imgViews;
	private IdleMonitorUtil idleTimer;
	private boolean delayScrSaver = false;
	private File vPath = null;
	private int sysVersion = Integer.parseInt(VERSION.SDK);
	private SDCardWatcher sdCardWatcher;
	private WebView webView;
	private LinearLayout lay01;
	private LinearLayout lay02;
	private KeyCodeTable kt;
	Timer timer;
	private int playMaxLenth;
	private String DIR_PREFIX;
	private String res="";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		kt=new KeyCodeTable();
		ExitApplication.getInstance().addActivity(this);
		loader=new LazyLoad();
		SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		playMaxLenth = preferences.getInt("pixnum", 0);
		DIR_PREFIX = "/video" + playMaxLenth + "pix/";

        if (playMaxLenth == 2) {
        	setContentView(R.layout.activity_main_p2);
        } else if (playMaxLenth == 4) {
        	setContentView(R.layout.activity_main_p4);
        } else if(playMaxLenth == 6) {
        	setContentView(R.layout.activity_main_p6);
        } 

		if (!ZuniMachineLib.useInternalMem) {
			// 偵測SD卡拔除
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
		webView = (WebView) findViewById(R.id.webview);
		lay01 = (LinearLayout) findViewById(R.id.lay01);
		lay02 = (LinearLayout) findViewById(R.id.lay02);
		lay01.setOnTouchListener(this);
		
		imgViews =  new ImageButton[playMaxLenth];
		
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
		
       
		if (idleTimer != null) {
			delayScrSaver = false;
			idleTimer.startTimer();
		}
		ZuniMachineLib.logToText("MainAct is active.", DIR_PREFIX);
		

		
	}

	private void readStrFromSD() {
		try {
			
			System.out.println("iamhere");
			if (ZuniMachineLib.useInternalMem) {
				vPath = new File(ZunidataEnvironment.getInternalStoragePath()
						+ DIR_PREFIX );
			} else {
				// 判斷 SD Card 有無插入
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_REMOVED)) {
					Toast.makeText(MainActivity.this,
							R.string.sdcard_not_found,
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					// 取得 SD Card 位置
					vPath = new File(ZunidataEnvironment.getExternalStoragePath()
							+ DIR_PREFIX );
				}

			}
			vPath.mkdirs();
			Log.v(TAG, "Reading data from " + vPath.getAbsolutePath());
			ArrayList<String> myList = new ArrayList<String>();

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
						loader.loadDrawable(dir, new lazyloadc(imgViews[i]));						
						imgViews[i].setOnTouchListener(this);
						imgViews[i].setTag(vPath.getAbsolutePath()
								+ "/" + (i + 1) + ".mp4");
						imgViews[i].setFocusable(false);

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
		public Bitmap getscreenshot(String path) {
			
			return null;
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
			Intent intent = new Intent(MainActivity.this, Player.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			overridePendingTransition(0, 0);
			startActivity(intent);
		}

		@Override
		protected void timerTick(int counter) {
			// if (idleCounter != null)
			// idleCounter.setText(String.valueOf(counter));
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
	protected void onResume() {
		if (sysVersion >= 14) {
		}
		
		if (idleTimer == null) {
			idleTimer = new IdleMonitorUtil();
		}
		if (!delayScrSaver)
		//idleTimer.startTimer();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (sdCardWatcher != null)
			sdCardWatcher.unRegisterSDCardStateChangeListener(this);
		super.onDestroy();
	}

	
	
	public final float[] BT_SELECTED = new float[] {1,0,0,0,99,0,1,0,0,99,0,0,1,0,99,0,0,0,1,0};
    public final float[] BT_NOT_SELECTED = new float[]  {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};;
    public final static float[] BT_SELECTED1 = new float[] {         
           0.5f, 0.339f, 0.332f, 0, 0,       
           0.5f, 0.339f, 0.332f, 0, 0,  
           0.5f, 0.339f, 0.332f, 0, 0,  
           0,     0,      0,     1, 0  
       }; 
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (event.getX() <= 50 && event.getY() <= 50) {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						Log.i(TAG, "Long-press catched, apk will shutdown.");				
						HideStatusBar.disable();
						ExitApplication.getInstance().exit(MainActivity.this);

					}
				}, 5000);
			}
			break;

		case MotionEvent.ACTION_UP:
			Log.i(TAG, "Touch Up");
			if (timer != null) {
				timer.cancel();
			}
			break;

		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "Touch Move");
			if (timer != null && (event.getX() > 50 || event.getY() > 50)) {
				timer.cancel();
			}
			break;
		}
		return super.dispatchTouchEvent(event);

	}
    
	public boolean onTouch(View v, MotionEvent event) {
		Drawable da=v.getBackground();
		if(event.getAction()==MotionEvent.ACTION_UP){				
				da.setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(da);
				Intent intent =new Intent(MainActivity.this,Player.class);
				intent.putExtra("playFile",(String) v.getTag());
				startActivity(intent);				
			}else if(event.getAction()==MotionEvent.ACTION_DOWN){
				da.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED1));
				v.setBackgroundDrawable(da);
		}		
		return true;

	}
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		System.out.println(event.getAction());
		if (idleTimer != null)
			idleTimer.resetTimer();
		return super.dispatchKeyEvent(event);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println(res);
		if(keyCode==155||keyCode==156)
			res=res+'\t';
		else if(keyCode==66){
			if(res=="")return true;
			Intent intent =new Intent(MainActivity.this, Main.class);
			intent.putExtra("res", res);
			intent.putExtra("from", "mainactivity");
			res="";			

			startActivity(intent);
			ExitApplication.getInstance().exit(MainActivity.this);
			
		}else if(kt.getChar(keyCode)==' ')
				;
			else
				res=res+new KeyCodeTable().getChar(keyCode);	
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		MainActivity.this.finish();
		Intent intent = new Intent(MainActivity.this, Player.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		ZuniMachineLib.logToText("Intent to Player...", DIR_PREFIX);
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	
	
}
