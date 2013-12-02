package com.JohnnyWorks.videoNpix;

import android.os.Environment;

public class GlobalString {
	public static final String TAG="dummy";
	public static final boolean DEBUG =false;
	public static final String Fb0Blank= "/sys/class/graphics/fb0/blank";
	public static final String Fb1Blank= "/sys/class/graphics/fb1/blank";
	public static final String Videoaxis= "/sys/class/video/axis";
	public static final String dbPath= Environment.getExternalStorageDirectory().getPath()+"/db.txt";
	public static int time=30;
}
