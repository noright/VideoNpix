package com.JohnnyWorks.videoNpix;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class VideoLinearLayout extends LinearLayout {

	public VideoLinearLayout(Context context) {
		super(context);
	}
	
	public VideoLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public VideoLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		System.out.println("shiyan");
		return false;
	}
	
}
