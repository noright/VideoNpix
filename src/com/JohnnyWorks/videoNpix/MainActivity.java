package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.JohnnyWorks.videoNpix.LazyLoad.ImageCallback;
import com.zunidata.zunidataapi.ZunidataEnvironment;

public class MainActivity extends Activity {
	private LazyLoad loader;
	public static final String URL_PREFIX = "video2pix:";
	private ImageButton[] imgViews;
	private IdleMonitorUtil idleTimer;
	private File vPath = null;
	private SDCardWatcher sdCardWatcher;
	Timer timer;
	private int playMaxLenth;
	private String DIR_PREFIX;
	private ImageView barcodeimg;
	private SoundPool soundPool;
	private int spId;
	private List<View> mListView;
	private ViewPager vp;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		vp=new ViewPager(this);
		GlobalString.mpl=new PlayList(GlobalString.imagepath);
		mListView=new ArrayList<View>();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ExitApplication.getInstance().addActivity(this);
		loader=new LazyLoad();
		LayoutInflater _li=getLayoutInflater();
		SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		playMaxLenth = preferences.getInt("pixnum", 0);
		
		int size=GlobalString.mpl.count()/playMaxLenth+(GlobalString.mpl.count()%playMaxLenth>0?1:0);
		for (int i = 0; i < size; i++) {
			switch(playMaxLenth){
			case 2:
				mListView.add(_li.inflate(R.layout.standby2, null));
				break;
			case 4:
				mListView.add(_li.inflate(R.layout.standby4, null));
				break;
			case 6:
				mListView.add(_li.inflate(R.layout.standby6, null));
				break;
				
			}
		}

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
			
		imgViews =  new ImageButton[size*playMaxLenth];		
		barcodeimg=new ImageView(this);
		
		List<Integer> idlist=new ArrayList<Integer>();		
		switch(playMaxLenth){
		case 6:
			idlist.add(R.id.imageButton6);
			idlist.add(R.id.imageButton5);
		case 4:
			idlist.add(R.id.imageButton4);
			idlist.add(R.id.imageButton3);
		case 2:
			idlist.add(R.id.imageButton2);
			idlist.add(R.id.imageButton1);
		}
		Collections.sort(idlist, new Comparator<Integer>() {
			
			public int compare(Integer arg0, Integer arg1) {
				return arg0-arg1;
			}
		});
		for (int i = 0; i < mListView.size(); i++) {
			for (int j = 0; j < playMaxLenth; j++) {
				imgViews[playMaxLenth*i+j]=(ImageButton) mListView.get(i).findViewById(idlist.get(j));
				if(playMaxLenth*i+j>=GlobalString.mpl.count()){
					imgViews[playMaxLenth*i+j].setVisibility(View.INVISIBLE);
					continue;
				}else{
					imgViews[playMaxLenth*i+j].setTag(playMaxLenth*i+j);
					loader.loadDrawable(GlobalString.imagepath+GlobalString.mpl.getItem(playMaxLenth*i+j), new lazyloadc(imgViews[playMaxLenth*i+j]));
//					imgViews[playMaxLenth*i+j].setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(GlobalString.imagepath+GlobalString.mpl.getItem(playMaxLenth*i+j))));
				}			
			}
		}
		if(new File(GlobalString.background).exists()){
			System.out.println("====hahahah");		
			for (int i = 0; i < mListView.size(); i++) {
				ARelativeLayout a=(ARelativeLayout) mListView.get(i).findViewById(R.id.ARelativeLayout1);
				a.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(GlobalString.background)));
			}
		}
		vp.setAdapter(new ViewPagerAdapter());
		setContentView(vp);
	}
	class ViewPagerAdapter extends PagerAdapter{
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListView.get(position), 0);
			return mListView.get(position);
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListView.get(position));
		}
		@Override
		public int getCount() {
			
			return mListView.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==(arg1);
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
//			delayScrSaver = false;
			idleTimer.stopTimer();
			idleTimer.startTimer();
		}
		ZuniMachineLib.logToText("MainAct is active.", DIR_PREFIX);
		super.onResume();
	}

	private void readStrFromSD() {
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
			//	barcodeimg.setVisibility(View.GONE);
				setContentView(vp);
				//for(int i=0;i<imgViews.length;i++)imgViews[i].setVisibility(View.VISIBLE);
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
	Barcode barcode=new Barcode(GlobalString.barcodepath,this) {	
		@Override
		void showPic() {
			String pic=where+GlobalString.orientation+"/"+res+".jpg";
			handler.removeCallbacks(back);
			System.out.println(pic);
			if(res=="")return;
			if(new File(pic).exists()){
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(pic));
				barcodeimg.setBackgroundDrawable(da);
				System.out.println("hellp");
				setContentView(barcodeimg);
				soundPool.play(spId, 1, 1, 1, 0, 1);
				//for(int i=0;i<imgViews.length;i++)imgViews[i].setVisibility(View.INVISIBLE);
//				barcodeimg.setVisibility(View.VISIBLE);
				res="";	
				handler.postDelayed(back, 5000);
			}else{
				System.out.println(where);
				System.out.println(where+"noinformation.jpg");
				Drawable da=new BitmapDrawable(BitmapFactory.decodeFile(where+"noinformation.jpg"));
				barcodeimg.setBackgroundDrawable(da);
				setContentView(barcodeimg);
				//for(int i=0;i<imgViews.length;i++)imgViews[i].setVisibility(View.INVISIBLE);
				//barcodeimg.setVisibility(View.VISIBLE);
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
		switch(newConfig.orientation){		
		case Configuration.ORIENTATION_PORTRAIT:
			GlobalString.orientation="PORTRAIT";
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			GlobalString.orientation="LANDSCAPE";
			break;
		}
		super.onConfigurationChanged(newConfig);
	}
}
