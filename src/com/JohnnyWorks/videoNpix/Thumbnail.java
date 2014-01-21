package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class Thumbnail {
	private Bitmap bitmap;
	@SuppressLint("NewApi")
	private int getThumbnail(String file){
		
		MediaMetadataRetriever mmr=new MediaMetadataRetriever();
		mmr.setDataSource(file);		
		bitmap=mmr.getFrameAtTime();
		mmr.release();
		return 0;
		
	}
	
	private int bit2jpg(String file,String path,int quality){
		try {
			File fi=new File(path);
			if(fi.exists())fi.delete();
			fi.createNewFile();
			FileOutputStream fo=new FileOutputStream(fi);
			getThumbnail(file);
		//	bitmap=getRoundedCornerBitmap(bitmap, (float) 2);
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality,fo );
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;		
	}
	
	public int saveThumbnail(String src,String dir){
		return bit2jpg(src, dir, 30);
	}

}
