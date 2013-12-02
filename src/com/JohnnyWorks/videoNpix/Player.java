package com.JohnnyWorks.videoNpix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.JohnnyWorks.videoNpix.R;
import com.JohnnyWorks.videoNpix.Player;
import com.JohnnyWorks.videoNpix.ZuniMachineLib;
import com.example.barcode.KeyCodeTable;
import com.example.barcode.Main;
import com.farcore.playerservice.AmPlayer;
import com.zunidata.zunidataapi.ZunidataEnvironment;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
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
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Player extends Activity {
	private static final String TAG = "videoNpix";
	private String res="";
	private AmPlayer video = new AmPlayer();
	boolean wantStop = false;
	private KeyCodeTable kt=null;
	// @Leo add 20121110-114600 start
	private long startTime = -1;
	private final long RESTART_DURATION = 1L * 60L * 60L * 1000L;// 1 hour
	// @Leo add 20121110-114600 end
	private boolean again=false;
	private TextView screenInfo;
	private Button startBtn;
	private FullVideoView mVideoView;
	private BroadcastReceiver mSDCardStateChangeListener;
	private MediaController mediaController;
	private WakeLock mWakeLock;
	private String playFile = null; 
	private int playnum = 0; 
	private int playMaxLenth; 
	private static boolean[] playedMark;
	private String DIR_PREFIX;
	private Boolean isRandomPlay;
	private SharedPreferences preferences;
	private Editor editor;
	private File vPath = null;
	private int sysVersion = Integer.parseInt(VERSION.SDK);
	private int playTimes = 0;
	private static int media_error_times = 0;

	private SDCardWatcher sdCardWatcher;
	
	private Toast mBrightnessToast;
	private Toast mSoundToast;

	final static int REQUEST_CLOSE = 0x02;
	Timer timer;
	
	private boolean can=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ExitApplication.getInstance().addActivity(this);

		setContentView(R.layout.activity_player);
		kt=new KeyCodeTable();
		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		playMaxLenth = preferences.getInt("pixnum", 0);

		DIR_PREFIX = "/video" + playMaxLenth + "pix/";
		isRandomPlay = preferences.getBoolean("isRandomPlay", false);

		if (isRandomPlay) {
			playedMark = new boolean[playMaxLenth];
			for (int i = 0; i < playMaxLenth; i++) {
				playedMark[i] = false;
			}
		}

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
		mVideoView = (FullVideoView) findViewById(R.id.videoView1);

		Intent intent = getIntent();
		if (intent != null && intent.getStringExtra("playFile") != null) {
			playFile = intent.getStringExtra("playFile");
			Log.v(TAG, "playFile=" + playFile);
		}

		if (ZuniMachineLib.useInternalMem) {
			vPath = new File(ZunidataEnvironment.getInternalStoragePath()
					+ DIR_PREFIX);
		} else {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_REMOVED)) {
				Toast.makeText(Player.this,
						R.string.sdcard_not_found,
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				vPath = new File(ZunidataEnvironment.getExternalStoragePath()
						+ DIR_PREFIX);
			}

		}


		if (intent.hasExtra("videoPos")) {
			playnum = intent.getIntExtra("videoPos", 0);
		} else {
			if (isRandomPlay) {
				playnum = (int) (Math.random() * playMaxLenth);
			} else {
				if (TextUtils.isEmpty(playFile)) {
					playnum = 0;
				} else {
					playnum = Integer.parseInt(playFile.substring(
							playFile.lastIndexOf(".mp4") - 1,
							playFile.lastIndexOf(".mp4"))) - 1;
				}
			}
		}

		Log.v(TAG, "playnum=" + playnum);

		startTime = System.currentTimeMillis();// @Leo add

		Log.v(TAG, "now=" + startTime);
		playTimes = 0;

		logPlayTimes();
		setPlayingFile();
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				can=true;
			}
		}, 2000);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTag");
		mWakeLock.acquire();
	}

	private void logPlayTimes() {
		ZuniMachineLib.logToText("PlayTimes=" + (playTimes++), DIR_PREFIX);
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


			if (!TextUtils.isEmpty(playFile) && (new File(playFile)).exists()) {
				
				video.Init();
				video.RegisterClientMessager(p_msg.getBinder());
				video.Open(playFile);
				video.Play();
				

				return;
			}

			if (TextUtils.isEmpty(playFile)) {
				String filePath = null;
				if (ZuniMachineLib.opt_playSingleFile) {
					filePath = vPath.getAbsolutePath() + "/0.mp4";

				} else {
					if (!isRandomPlay) {
						if (playnum >= playMaxLenth) {
							playnum = 0;
						}
					}
					filePath = vPath.getAbsolutePath() + "/" + (playnum + 1)
							+ ".mp4";
				}

				video.Init();
				video.RegisterClientMessager(p_msg.getBinder());
				video.Open(filePath);
				video.Play();
				
//				mVideoView.setVideoPath(filePath);
//				mVideoView.setVisibility(View.VISIBLE);
//				// mVideoView.setMediaController(mediaController);
				ZuniMachineLib.logToText("Start looping video {" + filePath
						+ "}", DIR_PREFIX);
//				mVideoView.requestFocus();
//				mVideoView.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean checkPlayedMark(int playnum) {
		boolean isAllPlayed = true; 
		for (int i = 0; i < playedMark.length; i++) {
			if (playedMark[i] == false) {

				isAllPlayed = false;
				break;
			}
		}
		if (isAllPlayed) {
			for (int i = 0; i < playedMark.length; i++) {
				playedMark[i] = false;
			}
		}
		return playedMark[playnum];
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	
	@Override
	protected void onPause() {
		super.onPause();
		wantStop = true;
		video.Close();
		video.CloseAll();
	}

	@Override
	public void onBackPressed() {
		
		Intent intent = new Intent(Player.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		ZuniMachineLib.logToText("Intent to MainActivity...", DIR_PREFIX);
		overridePendingTransition(0, 0);
		intent.putExtra("firstStart", "false");
		startActivity(intent);
		
		video.Close();
		video.CloseAll();
		Player.this.finish();
	}



	@Override
	protected void onDestroy() {
		if (sdCardWatcher != null)
			sdCardWatcher.unRegisterSDCardStateChangeListener(this);

		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
		video.Close();
		video.CloseAll();
		super.onDestroy();
	}

	/** 鍋垫脯鍏ㄥ煙(鍏ㄨ灑骞�涓婄殑瑙告帶浜嬩欢 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "Touch Down");

			if (event.getX() <= 50 && event.getY() <= 50) {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						Log.i(TAG, "Long-press catched, apk will shutdown.");
						// Intent intent = new Intent(Player.this, Start.class);
						// setResult(REQUEST_CLOSE, intent);
						// Player.this.finish();
						
						wantStop = true;
						video.Close();
						video.CloseAll();
						
//						Intent intent = new Intent(Intent.ACTION_MAIN);
//						intent.addCategory(Intent.CATEGORY_HOME);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(intent);
//						android.os.Process.killProcess(android.os.Process.myPid());
						
//						ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//						am.forceStopPackage(getPackageName());
						
						HideStatusBar.disable();
						ExitApplication.getInstance().exit(Player.this);

					}
				}, 5000);
			}
			break;

		case MotionEvent.ACTION_UP:
			Log.i(TAG, "Touch Up");
			if (timer != null) {
				timer.cancel();
			}
			onBackPressed();
			break;

		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "Touch Move");
			if (timer != null && (event.getX() > 50 || event.getY() > 50)) {
				timer.cancel();
			}
			break;
		}
		return true;

	}


//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//
//		return false;
//	}

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

		brightness += offset * 30;
		if (brightness < 0) {
			brightness = 0;
		} else if (brightness > 255) {
			brightness = 255;
		}

		Window window = getWindow();
		WindowManager.LayoutParams layoutparams = window.getAttributes();
		layoutparams.screenBrightness = (1f + (50f - 1f)
				* ((float) brightness / 255f)) / 50f;
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

//		ViewGroup layout = (ViewGroup) mBrightnessToast.getView();
//		ProgressBar progress = (ProgressBar) layout.getChildAt(layout
//				.getChildCount() - 1);
//		progress.setIndeterminate(false);
//		progress.setProgress(brightness);
//		mBrightnessToast.show();
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

//		ViewGroup layout = (ViewGroup) mSoundToast.getView();
//		ProgressBar progress = (ProgressBar) layout.getChildAt(layout
//				.getChildCount() - 1);
//		progress.setProgress((int) current);
//		mSoundToast.show();
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

	// -------------------------------------------------------------------
	
	private Messenger p_msg =new Messenger(new Handler(){
		@Override
		public void handleMessage(Message msg) {
		switch(msg.what){
			case 1001:
				switch (msg.arg1) {	
				case 0x20006:
					writeFile(GlobalString.Fb0Blank, "1");
					writeFile(GlobalString.Fb1Blank, "1");
					writeFile(GlobalString.Videoaxis, "0 0 0 0");
					break;
				case 0x30004:	
					if (wantStop) {
						wantStop = false;
						break;
					}
					video.Close();
					video.CloseAll();
					
					logPlayTimes();

					if (!TextUtils.isEmpty(playFile)) {
						playFile = null;
					}
					mVideoView.setVisibility(View.GONE);

					if (isRandomPlay) {
						playedMark[playnum] = true;
						int tmpPlaynum = 0;
						do {
							tmpPlaynum = (int) (Math.random() * playMaxLenth);
						} while (tmpPlaynum == playnum || checkPlayedMark(tmpPlaynum));
						playnum = tmpPlaynum;
					} else {
						playnum++;
					}
					
					setPlayingFile();
				}
				
			case 1003:

				Log.e("", "Tony shit!!!   " + msg.arg2);
				
				if (msg.arg2 != 0) {
					wantStop = true;
					video.Close();
					video.CloseAll();
					finish();
				}
			}
		}
	});	
	
//--------------------------------------------------------------------
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
				adjustBrightness(-1);
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				adjustBrightness(1);
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				adjustVolume(-1);
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				adjustVolume(1);
				return true;
			default:
//				onBackPressed();
			}
		}
		if(keyCode==155||keyCode==156)
			res=res+'\t';
		else if(keyCode==66){
			if(res=="")return true;
			System.out.println(res);
			Intent intent =new Intent(Player.this, Main.class);
			intent.putExtra("res", res);
			intent.putExtra("from", "play");
			intent.putExtra("videoPos", playnum);
			res="";			
			again=true;
			video.CloseAll();
			startActivity(intent);
			ExitApplication.getInstance().exit(Player.this);
	//		finish();
			
		}else if(kt.getChar(keyCode)==' ')
				;
			else
				res=res+new KeyCodeTable().getChar(keyCode);	
	
		return super.onKeyDown(keyCode, event);
	}
/////////////////////////////////////////////////////////////////////////
	private void writeFile(String file, String value){
		File OutputFile = new File(file);
		if(!OutputFile.exists()) {        	
        	return;
        }

    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try {
				Log.d("", "----------------set" + file + ": " + value);
    			out.write(value);    
    		} 
			finally {
				out.close();
			}
		}
		catch (IOException e) {
			Log.e("", "IOException when write "+OutputFile);
		}
	}
}
