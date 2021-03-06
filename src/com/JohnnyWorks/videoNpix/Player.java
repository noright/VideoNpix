package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.farcore.playerservice.AmPlayer;
import com.farcore.playerservice.PlayerHandler;
import com.zunidata.zunidataapi.ZunidataEnvironment;

public class Player extends Activity {
	private static final String TAG = "videoNpix";
	private AmPlayer video = new AmPlayer();
	boolean wantStop = false;
	private WakeLock mWakeLock;
	private int playFile = 0; 
	private String DIR_PREFIX;
	private Boolean isRandomPlay;
	private SharedPreferences preferences;
	private int playTimes = 0;
	private SDCardWatcher sdCardWatcher;	
	private Toast mBrightnessToast;
	private Toast mSoundToast;
	final static int REQUEST_CLOSE = 0x02;
	Timer timer;
	private ImageView barcodeimg;
	private SoundPool soundPool;
	private int spId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		ExitApplication.getInstance().addActivity(this);		
		setContentView(R.layout.activity_player);
		barcodeimg=(ImageView) findViewById(R.id.imageView1);
		barcodeimg.setVisibility(View.INVISIBLE);
		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		isRandomPlay = preferences.getBoolean("isRandomPlay", false);

		if (!ZuniMachineLib.useInternalMem) {
			sdCardWatcher = new SDCardWatcher();
			sdCardWatcher.registerSDCardStateChangeListener(this,
					new SDRemovedListener() {

				public void onSDRemoved() {
					Toast.makeText(Player.this,
							R.string.sdcard_not_found,
							Toast.LENGTH_SHORT).show();
					Player.this.finish();
				}
			});
		}
		
		Intent intent = getIntent();
		if (intent != null) {
			playFile = intent.getIntExtra("playFile", 0);
			System.out.println(GlobalString.mpl.getItem(playFile));
			Log.v(TAG, "playFile=" + playFile);
		}

		logPlayTimes();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTag");
		mWakeLock.acquire();		
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        spId = soundPool.load(this,R.raw.di, 1);
        onConfigurationChanged(getResources().getConfiguration());
        
        
	}
	@Override
	protected void onResume() {
		super.onResume();
		setPlayingFile();
		
	}
	private void logPlayTimes() {
		ZuniMachineLib.logToText("PlayTimes=" + (playTimes++), DIR_PREFIX);
	}
	class HandlerImp extends PlayerHandler {

		@Override
		public void playMedia() {
			logPlayTimes();		
			setPlayingFile();
		}		
	}
	
	private void setPlayingFile() {
		try {
			if (!ZuniMachineLib.useInternalMem) {

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_REMOVED)) {
					Toast.makeText(Player.this,
							R.string.sdcard_not_found,
							Toast.LENGTH_SHORT).show();
					Player.this.finish();
					return;
				}
			}
			System.out.println("==="+GlobalString.mpl.count()+"==="+playFile);
			String play;
			if(GlobalString.mpl.count()-1>playFile){
				play=GlobalString.mpl.getItem(playFile);
				play=GlobalString.videopath+play.replaceAll("jpg", "mp4");
				if (isRandomPlay) {
					playFile = (int) (Math.random() * GlobalString.mpl.count());
				}else{
					playFile++;
				}			
			}else{
				play=GlobalString.mpl.getItem(playFile);
				play=GlobalString.videopath+play.replaceAll("jpg", "mp4");
				playFile=0;
			}
			
			
			System.out.println("==="+play);
			video.Init();
			video.RegisterClientMessager(new Messenger(new HandlerImp()).getBinder());
			video.Open(play);
			video.Play();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	


	
	@Override
	protected void onPause() {
		super.onPause();
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
		video.Close();
	}

	@Override
	protected void onDestroy() {
		if (sdCardWatcher != null)
			sdCardWatcher.unRegisterSDCardStateChangeListener(this);

		video.Close();
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Player.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		ZuniMachineLib.logToText("Intent to MainActivity...", DIR_PREFIX);
		overridePendingTransition(0, 0);
		intent.putExtra("firstStart", "false");
		startActivity(intent);	
		Player.this.finish();
	}




	// -------------------------------------------------------------------
	private void adjustBrightness(int offset) {
		ContentResolver resolver = getContentResolver();

		int brightness;
		try {
			brightness = Settings.System.getInt(resolver,
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			throw new Error(e);
		}

		brightness += offset * 16;
		if (brightness < 0) {
			brightness = 0;
		} else if (brightness > 255) {
			brightness = 255;
		}

		Window window = getWindow();
		WindowManager.LayoutParams layoutparams = window.getAttributes();
		layoutparams.screenBrightness = (1f + (50f - 1f)
				* ((float) brightness / 255f)) / 50f;
		if(layoutparams.screenBrightness<0.1)layoutparams.screenBrightness=(float)0.08;
		window.setAttributes(layoutparams);

		Settings.System.putInt(resolver,
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
				brightness);

		if (mSoundToast != null) {
			mSoundToast.cancel();
		}
		if (mBrightnessToast == null) {
			mBrightnessToast = createToast(R.string.brightness, 255);
		}
	}

	private void adjustVolume(int offset) {
		AudioManager audiomanager = (AudioManager) getSystemService("audio");

		float max = audiomanager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		float max2 = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float current = audiomanager
				.getStreamVolume(AudioManager.STREAM_SYSTEM);
		current += offset;
		if (current >= max) {
			current = max;
		} else if (current < 0) {
			current = 0;
		}
		float current2 = (current / max) * max2;

		audiomanager.setStreamVolume(AudioManager.STREAM_SYSTEM, (int) current,
				0);
		audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) current2,
				0);

		if (mBrightnessToast != null) {
			mBrightnessToast.cancel();
		}
		if (mSoundToast == null) {
			mSoundToast = createToast(R.string.volume, (int) max);
		}
	}

	private Toast createToast(String text, int max) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		LinearLayout layout = (LinearLayout) toast.getView();
		layout.setOrientation(LinearLayout.HORIZONTAL);
		ProgressBar progress = new ProgressBar(this, null,
				android.R.attr.progressBarStyleHorizontal);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 0, 0);
		progress.setLayoutParams(params);
		progress.setMax(max);
		progress.setIndeterminate(false);
		layout.addView(progress);

		return toast;
	}

	private Toast createToast(int resid, int max) {
		return createToast(this.getResources().getString(resid), max);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "Touch Up");
			if (timer != null) {
				timer.cancel();
			}
			if(barcodeimg.isShown()==false){
				handler.removeCallbacks(back);
				video.Close();
				onBackPressed();
				return true;
			}
			video.Close();
			handler.removeCallbacks(back);
			handler.sendEmptyMessage(1234);
			
		}
		return true;
	}
	
	
	final Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==1234){
				barcodeimg.setVisibility(View.INVISIBLE);
				setPlayingFile();
			}
		}
		
	};
	
	Runnable back=new Runnable() {
		
		@Override
		public void run() {
			handler.sendEmptyMessage(1234);
		}
	};
	Barcode barcode=new Barcode(GlobalString.barcodepath,this) {	
		@Override
		void showPic() {
			String pic=where+GlobalString.orientation+"/"+res+".jpg";
			System.out.println("==="+pic);
			video.Close();
			handler.removeCallbacks(back);
			if(res=="")return;
			if(new File(pic).exists()){
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(pic));
				barcodeimg.setBackgroundDrawable(da);
				soundPool.play(spId, 1, 1, 1, 0, 1);
				barcodeimg.setVisibility(View.VISIBLE);
				res="";
				handler.postDelayed(back, 5000);
			}else{
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(where+"noinformation.jpg"));
				barcodeimg.setBackgroundDrawable(da);
				barcodeimg.setVisibility(View.VISIBLE);
				res="";
				handler.postDelayed(back, 5000);
			}			
		}
	};
	
		public boolean onKeyDown(int keyCode, KeyEvent event) {	
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(!barcode.res.equals("")){
				;
			}else if(barcode.oldkey==66){
				barcode.oldkey=keyCode;
			}else{
				adjustBrightness(1);				
			}		
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			adjustBrightness(-1);
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_DPAD_LEFT:
			adjustVolume(-1);
			return super.onKeyDown(KeyEvent.KEYCODE_VOLUME_DOWN, event);
		
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			adjustVolume(1);
			return super.onKeyDown(KeyEvent.KEYCODE_VOLUME_UP, event);
		}
		barcode.show(keyCode);
		return true;
	}
	
//--------------------------------------------------------------------
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (event.getAction() == KeyEvent.ACTION_UP) {
//			switch (event.getKeyCode()) {
//			case KeyEvent.KEYCODE_DPAD_DOWN:
//				adjustBrightness(-1);
//				return true;
//			case KeyEvent.KEYCODE_DPAD_UP:
//				adjustBrightness(1);
//				return true;
//			case KeyEvent.KEYCODE_DPAD_LEFT:
//				adjustVolume(-1);
//				return true;
//			case KeyEvent.KEYCODE_DPAD_RIGHT:
//				adjustVolume(1);
//				return true;
//			default:
//			}
//		}
//		return false;
//	}
	WindowManager mWindowManager;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mWindowManager=getWindowManager();
		int getRotation = mWindowManager.getDefaultDisplay().getRotation();
		switch(getRotation){
		case 0:
			GlobalString.writeFile(GlobalString.videoangle, "3 0 1 2");			
			break;
		case 1:
			GlobalString.writeFile(GlobalString.videoangle, "0 1 2 3");			
			break;
		case 2:
			GlobalString.writeFile(GlobalString.videoangle, "1 2 3 0");			
			break;
		case 3:			
			GlobalString.writeFile(GlobalString.videoangle, "2 3 0 1");
			break;
		}
		super.onConfigurationChanged(newConfig);
	}
}
