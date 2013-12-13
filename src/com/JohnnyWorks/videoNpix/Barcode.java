package com.JohnnyWorks.videoNpix;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


public abstract class Barcode {
	String res,where;
	KeyCodeTable kt;
	Context mContext;
	Barcode(String w,Context context){
		mContext=context;
		where=w;
		res="";
		kt=new KeyCodeTable();
	}

	final private boolean readKey(int keyCode){
		if(keyCode==155||keyCode==156)
			res=res+'\t';
		else if(keyCode==66){
			if(res=="")return false;
			return true;			
		}else if(kt.getChar(keyCode)==' ')
				;
			else			
				res=res+kt.getChar(keyCode);
		return false;
	}
	Barcode show(int keyCode){
		
		
		while(readKey(keyCode)){	
			System.out.println(res);
			showPic();
			break;
		}
		return this;
		
	}
	
	abstract void showPic();
	
}
