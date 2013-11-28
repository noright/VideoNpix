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
		
//		if (isFirstStart) {
			ZuniMachineLib.logToText("MockScr start MainActivity...", DIR_PREFIX);
			Intent intent = new Intent(MockScreen.this, MainActivity.class);
			overridePendingTransition(0, 0);
//			startActivityForResult(intent, 0); 
			startActivity(intent); 
//			isFirstStart = false;
//			
//		} else {
//			ZuniMachineLib.logToText("MockScr start Player...", DIR_PREFIX);
//			Intent intent = new Intent(MockScreen.this, Player.class);
//			overridePendingTransition(0, 0);
////			startActivityForResult(intent, 0); 
//			startActivity(intent); 
//		}

	}
	
	public void startPlayer() {
		ZuniMachineLib.logToText("MockScr start Player...", DIR_PREFIX);
		Intent intent = new Intent(MockScreen.this, Player.class);
		overridePendingTransition(0, 0);
		startActivity(intent); 
	}
	
//	@Override
//	protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
//		switch (resultCode) { 
//			case REQUEST_CLOSE:
//				MockScreen.this.finish();
//				break;
//			default:
//				break;	 
//		}
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ExitApplication.getInstance().addActivity(this);

//		if (sysVersion >= 14)
		// 將系統列變暗（Android 4.0使用�?
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		setContentView(R.layout.mock_screen);
		
		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		DIR_PREFIX = "/video" + preferences.getInt("pixnum", 0) + "pix/";

//		if (getIntent().hasExtra("close")) {
//			finish();
//		} 
		
		if (!ZuniMachineLib.IsZuniMachine()) {
			finish();
		}

		if (sysVersion >= 14) {
			// new Thread()
			// {
			// @Override
			// public void run()
			// {
			//
			// // 強制刪除SystemBar變成全螢幕模�?
			// for (int i = 0; i < 20; i++)
			// {
			// ZuniMachineLib
			// .RootCommand(ZuniMachineLib.cmdKill_systemui);
			// try
			// {
			// Thread.sleep(100);
			// } catch (InterruptedException e)
			// {
			// }
			// }
			//
			// }
			// }.start();

			// new Thread()
			// {
			// @Override
			// public void run()
			// {
			// Process proc;
			// try
			// {
			// proc = Runtime
			// .getRuntime()
			// .exec(new String[] { "su", "-c",
			// "service call activity 79 s16 com.android.systemui" });
			// proc.waitFor();
			// } catch (IOException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (InterruptedException e)
			// {
			// }
			// }
			// }.start();

		}
		if (!ZuniMachineLib.useInternalMem) {
			// 偵測SD卡拔�?
			sdCardWatcher = new SDCardWatcher();
			sdCardWatcher.registerSDCardStateChangeListener(this,
					new SDRemovedListener() {
						@Override
						public void onSDRemoved() {
							Toast.makeText(MockScreen.this,
									R.string.sdcard_not_found,
									Toast.LENGTH_SHORT).show();
							// if (sysVersion >= 14)
							// ZuniMachineLib
							// .RootCommand(ZuniMachineLib.cmdStart_systemui);

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
	
		// if (sysVersion >= 14)
		// {
		// new Thread()
		// {
		// @Override
		// public void run()
		// {
		//
		// // 強制刪除SystemBar變成全螢幕模�?
		// for (int i = 0; i < 5; i++)
		// ZuniMachineLib
		// .RootCommand(ZuniMachineLib.cmdKill_systemui);
		// try
		// {
		// Thread.sleep(500);
		// } catch (InterruptedException e)
		// {
		// }
		// mHandler.sendEmptyMessage(START_PLAYER);
		// }
		// }.start();
		// } else
		// {
		if (getIntent().hasExtra("startFromPlayer")) {
			startPlayer();
		} else {
			startMainActivity();
		}
		// }
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
