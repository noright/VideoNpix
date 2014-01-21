package com.JohnnyWorks.videoNpix;

public class KeyCodeTable {
	private char[] keycodetable={' ',' ',' ',' ',' ',' ',' ',
								 '0','1','2','3','4','5','6','7','8','9',
								 ' ',' ',' ','#','$','!',' ',' ',' ',' ',' ',' ',
								 'A','B','C','D','E','F','G','H','I','J','K','L','M','N',
								 'O','P','Q','R','S','T','U','V','W','X','Y','Z',
								 ' ',' ',' ',' ',' ',' ','\t',' ',' ',' ',' ','\t',' ',
								 ' ','-','=',' ',' ',' ',' ',' ',' ','@',' ',' ',' ',
								 '*',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};
	public int getTableLen(){
		return keycodetable.length;
	}
	public char getChar(int keyCode){
		return keycodetable[keyCode];
	}
	public String getEAN8(int keyCode,String res){

		res=res+getChar(keyCode);
		return res;		
	}
}
