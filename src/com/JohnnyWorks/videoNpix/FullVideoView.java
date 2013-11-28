package com.JohnnyWorks.videoNpix;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullVideoView extends VideoView{

	private int mVideoWidth;
	private int mVideoHeight;
	
	public FullVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FullVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FullVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mVideoWidth = 0;
		mVideoHeight = 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.VideoView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width,height);
        
	}
	
	

}
