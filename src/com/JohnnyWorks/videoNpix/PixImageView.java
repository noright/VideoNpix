package com.JohnnyWorks.videoNpix;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Messenger;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.farcore.playerservice.AmPlayer;
import com.farcore.playerservice.PlayerHandler;

public class PixImageView extends ImageButton {
	String LOG="==="+getClass().getSimpleName();
	private AmPlayer video = new AmPlayer();
	Context mContext;
	
	class HandlerImp extends PlayerHandler {

		@Override
		public void playMedia() {
			System.out.println("stoped");
		}		
	}
	public PixImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		video.Init();
		video.RegisterClientMessager(new Messenger(new HandlerImp()).getBinder());
	}

	public PixImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		video.Init();
		video.RegisterClientMessager(new Messenger(new HandlerImp()).getBinder());
	}

	public PixImageView(Context context) {
		super(context);
		mContext=context;
		video.Init();
		video.RegisterClientMessager(new Messenger(new HandlerImp()).getBinder());
	}
	
	public final float[] BT_SELECTED = new float[] {1,0,0,0,99,0,1,0,0,99,0,0,1,0,99,0,0,0,1,0};
    public final float[] BT_NOT_SELECTED = new float[]  {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};
    public final static float[] BT_SELECTED1 = new float[] {         
           0.5f, 0.339f, 0.332f, 0, 0,       
           0.5f, 0.339f, 0.332f, 0, 0,  
           0.5f, 0.339f, 0.332f, 0, 0,  
           0,     0,      0,     1, 0  
       };
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println(LOG+event.getAction());
		Drawable da=getBackground();
		if(event.getAction()==MotionEvent.ACTION_UP){				
				da.setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				int s=(Integer) getTag();
				Intent intent =new Intent(mContext,Player.class);
				intent.putExtra("playFile",s);
				mContext.startActivity(intent);	
				return true;
			}else if(event.getAction()==MotionEvent.ACTION_DOWN){
				da.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED1));
				
				return true;
		}		
		return false;
	}
}
