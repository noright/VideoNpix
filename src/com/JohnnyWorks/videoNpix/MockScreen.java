package com.JohnnyWorks.videoNpix;

import java.util.Timer;
import java.util.TimerTask;

import com.JohnnyWorks.videoNpix.R;
import com.JohnnyWorks.videoNpix.SDCardWatcher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MockScreen extends Activity {
	private static final String TAG = "videoNpix";
	private static final int START_PLAYER = 0x0000001;
	private boolean isFirstStart = true;
	private BroadcastReceiver mSDCardStateChangeListener;
	private SDCardWatcher sdCardWatcher;
	private int sysVersion = Integer.parseInt(VERSION.SDK);
	TextView textView;
	final static int REQUEST_CLOSE = 0x02;
	boolean isAboutEixt;
	Timer timer;
	private String DIR_PREFIX;
	private SharedPreferences preferences;
	private Editor editor;
	

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_PLAYER:
				startPlayer();
				break;

			default:
				break;
			}
		}
	};

	public void startMainActivity() {
		
		ZuniMachineLib.logToText("MockScr start MainActivity...", DIR_PREFIX);
		Intent intent = new Intent(MockScreen.this, MainActivity.class);
		overridePendingTransition(0, 0);
		startActivity(intent); 

	}
	
	public void startPlayer() {
		ZuniMachineLib.logToText("MockScr start Player...", DIR_PREFIX);
		Intent intent = new Intent(MockScreen.this, Player.class);
		overridePendingTransition(0, 0);
		startActivity(intent); 
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ExitApplication.getInstance().addActivity(this);
		HideStatusBar.enable();
		
		setContentView(R.layout.mock_screen);
		
		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		DIR_PREFIX = "/video" + preferences.getInt("pixnum", 0) + "pix/";
		
		GlobalString.time=preferences.getInt("time", 30);
		
		System.out.println(GlobalString.time);
		if (!ZuniMachineLib.IsZuniMachine()) {
			finish();
		}
		if (!ZuniMachineLib.useInternalMem) {
			sdCardWatcher = new SDCardWatcher();
			sdCardWatcher.registerSDCardStateChangeListener(this,
					new SDRemovedListener() {
						@Override
						public void onSDRemoved() {
							Toast.makeText(MockScreen.this,
									R.string.sdcard_not_found,
									Toast.LENGTH_SHORT).show();


							MockScreen.this.finish();
						}
					}, new SDMountListener() {

						@Override
						public void onSDMounted() {

						}
					});
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().hasExtra("startFromPlayer")) {
			startPlayer();
		} else {
			startMainActivity();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public void onBackPressed() {
	}

	@Override
	protected void onDestroy() {
		if (sdCardWatcher != null)
			sdCardWatcher.unRegisterSDCardStateChangeListener(this);
		super.onDestroy();
	}

}
