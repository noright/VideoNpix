package com.JohnnyWorks.videoNpix;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ARelativeLayout extends RelativeLayout {
	Context mContext;
	public ARelativeLayout(Context context) {
		super(context);
		mContext=context;
		setFocusable(true);
		
	}

	public ARelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;	
		setFocusable(true);
	}

	public ARelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		setFocusable(true);
	}
	
	Timer timer;
	boolean touchclose=false;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			System.out.println("==="+"imagelinear"+ev.getX()+" "+ev.getY());
			if (ev.getX() <= 50 && ev.getY() <= 50) {
				System.out.println("===1");
				touchclose=true;
				timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						
						GlobalString.writeFile(GlobalString.Fb0Blank, "0");
						HideStatusBar.disable();
						ExitApplication.getInstance().exit(mContext);

					}
				}, 5000);
				return true;
			}
		case MotionEvent.ACTION_UP:		
			if (timer != null) {
				timer.cancel();
				if(touchclose){
					touchclose=false;
					return true;
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:		
			if (timer != null && (ev.getX() > 50 || ev.getY() > 50)) {
				timer.cancel();
			}
			break;	
		}
		
		return false;
	}	
	
}
