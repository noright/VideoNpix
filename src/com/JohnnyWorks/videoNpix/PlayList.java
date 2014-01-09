package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.util.ArrayList;

public class PlayList {
	ArrayList<String> mList;
	
	PlayList(){}
	
	PlayList(final String path){MkPlayList(path);}
	
	PlayList(final ArrayList<String> list){MkPlayList(list);}
	
	
	
	
	private void MkPlayList(final String path) {
		File file=new File(path);
		if(file.isDirectory()){
			System.out.println(file.list().toString());
		}
	}
	private void MkPlayList(final ArrayList<String> list) {
		
		mList=list;

	}
}
