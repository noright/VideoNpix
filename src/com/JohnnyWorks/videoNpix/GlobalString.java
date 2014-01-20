package com.JohnnyWorks.videoNpix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

import com.zunidata.zunidataapi.ZunidataEnvironment;

public class GlobalString {
	public static final String TAG="dummy";
	public static final boolean DEBUG =false;
	public static final String Fb0Blank= "/sys/class/graphics/fb0/blank";
	public static final String Fb1Blank= "/sys/class/graphics/fb1/blank";
	public static final String Videoaxis= "/sys/class/video/axis";
	public static final String videoangle= "/sys/class/ppmgr/angle";
	public static int time=30;
	public static String sdcard=ZunidataEnvironment.External_Storage_Path;
	public static String imagepath=sdcard+"/video6pix/thumbnail/";
	public static String videopath=sdcard+"/video6pix/";
	public static String background=sdcard+"/barcode/background.jpg";
	public static String orientation="LANDSCAPE";
	public static PlayList mpl;
	static{
		mpl=new PlayList(GlobalString.imagepath);
	}
	static public void writeFile(String file, String value){
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
