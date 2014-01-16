package com.JohnnyWorks.videoNpix;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class PlayList {
	ArrayList<String> mList;
	
	PlayList(){}
	
	PlayList(final String path){MkPlayList(path);}
	
	PlayList(final ArrayList<String> list){MkPlayList(list);}
	
	private void MkPlayList(final String path) {
		File file=new File(path);
		if(file.isDirectory()){
			mList=new ArrayList<String>();
			for (int i = 0; i < file.list().length; i++) {
				mList.add(file.list()[i]);
			}
			sort();
			System.out.println(mList);
		}
	}
	private void MkPlayList(final ArrayList<String> list) {
		
		mList=list;

	}
	
	public String getItem(int i){
		return mList.get(i);
	}
	public int count(){
		return mList.size();
	}
	
	private void sort(){
		Collections.sort(mList, new Comparator<String>() {
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});
	}
}
