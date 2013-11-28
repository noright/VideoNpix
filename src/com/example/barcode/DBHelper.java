package com.example.barcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.JohnnyWorks.videoNpix.GlobalString;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "barcode.db";
	public static final int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "products";

	public static final String ID = "_id";
	public static final String BARCODE = "barcode";
	public static final String NAME = "name";
	public static final String PRICE = "price";
	public static final String DETAIL = "detail";
	SQLiteDatabase ddb;
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
												+ ID +" integer primary key autoincrement,"
												+ BARCODE +" text,"
												+ NAME +" text,"
												+ PRICE +" text,"
												+ DETAIL +" text);";
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ddb=db;
		db.execSQL(CREATE_TABLE);
		String path=GlobalString.dbPath;
		if(new File(path).exists()){
			readfile(path);
		}
		else{
			
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787302177869\", \"Linux Development Kit: The most effective way to project development\", \"58\", \"Author：John Fusco\nPublisher: Tsinghua University Press\nPrice：58元\")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9789866072000\", \"Google Android SDK development examples\", \"NT950\", \"Author: She Zhilong, Chen Yu Xun, Zheng Ming-jie, Chen Xiaofeng\nPublisher: Wyatt knows culture \n Price:NT950\")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787121100000\", \"iPhone SDK 3 Developer Guide\", \"65\", \"Author：Bill Dudney Chris Adamson\nPublisher: Electronic Industry Press \n Price：65元\")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " + 
					"(null, \"9787111347446\", \"Linux kernel design Art: Graphic Linux operating system architecture design and implementation of the principle \",\" 79\",\" Author: New design team \n Press: Machinery Industry Press \n Price: 79\")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " + 
					"(null, \"9787111357629\", \"Depth understanding of the Android \", \" 69 \", \" Author: dengfan flat \n Press: Machinery Industry Press \n Price: 69 \")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " + 
					"(null, \"9787111267768\", \"Python study manual \", \" 89\", \" Author: Mark Lutz forward, Hou Jing translated \n Press: Machinery Industry Press \n Price: 89\")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9789861819174\", \"iPhone programming examples for classic \", \" NT480 \", \" Author: Paul Deitel waiting Eav and translations \n Publisher: Acer Feng Corp. \n Price：NT480\")"); 
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787564115197\", \"LINUX system Programming design \",\" 56 yuan \", \" Author: ROBERT LOVE forward to, O'REILLY TAIWAN translation of \n Press: Southeast university Publishing House \n Price: 56 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " + 
					"(null, \"9789574428618\", \"Brand secret \", \" NT580 \", \" Author: World Branding Committee with, Chen Yi Lin translated \n Press: Flag Publishing Limited \n Price: NT580\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787111276869\", \"Objective-C2.0 programming (the original book version 2) \", \" 66 yuan \", \" Author: (United States) Stephen G. Kochan with, Zhang Bo, Huang Xiangqin translated \n Press: Mechanical Industry Press society \n Price: 66 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787302059998\", \"VB.NET and SQL Server 2000 Advanced Programming - create efficient data tier \", \" 59 yuan \", \" Author: Tony Brain Denise Gosnell waiting Compro translation \n Publisher: Tsinghua University Press \n Price: 59 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787115142009\", \"Windows 9X/Me/NT/2000/XP/2003 DOS command line technology Guinness \", \" 68 yuan \", \" Author: Liu Xiaohui waiting \n Press: People's Posts and Telecommunications Press \n Price: 68 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787111323570\", \"Linux kernel A Complete Reference Manual \", \" 79 yuan \", \" Author: Qiu iron, Zhou Yu, Deng Yingying \n Press: Machinery Industry Press \n Price: 79 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787508338637\", \"Linux device drivers \", \" 69 yuan \", \" Author: (United States) Ke Bote (Corbet, J.), etc., WEI Yong, Geng Yue Zhong Shuyi translation \n Press: China Electric Power Press \n Price: 69 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787564115203\", \"LINUX Networking Cookbook (Chinese version) \", \" 88 yuan \", \" Author: CARLA SCHRODER with, Feng Liang translated \n Press: Southeast University Press \n Price: 88 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787121035753\", \"Programming ASP.NET Chinese version \", \" 99 yuan \", \" Author: (United States) Libo Ti, (America) Heviz with, Qu Jie, Zhao Lidong, Zhang Hao translation \n Publisher: Electronic Industry Publishing society \n Price: 99 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787111337270\", \"Android technology insider system volume \", \" 69 yuan \", \" Author: Yang Fengsheng \n Press: Machinery Industry Press \n Price: 69 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787115156655\", \"Flash8 foundation and examples succinctly \", \" 39 yuan \", \" Author: Takeoff Technology \n Press: People's Posts and Telecommunications Press \n Price: 39 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787115298027\", \"Android depth exploration (Volume 1) HAL and driver development \", \" 99 yuan \", \" Author: Li Ning \n Press: People's Posts and Telecommunications Press \n Price: 99 yuan\")");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " +
					"(null, \"9787508356464\", \"Head First HTML with CSS.XHTML (Chinese version) \", \" 79 yuan \", \" Author: (United States) Freeman, etc., WEI Yong, Lin Wang, Zhang Xiaokun translation \n Press: China Electric Power Press \n Price: 79 yuan\")");
		}
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public int readfile(String path){
		
		File file=new File(path);
		if(!(file.exists()))return -1;
		try {
			BufferedReader in=new BufferedReader(new FileReader(file));
			String line;
			while((line=in.readLine())!=null){
				String[] a=line.split(":");
				System.out.println(new GoodsItem().setGoods(a[0],
						a[1], a[2], a[3]).toString());
				ddb.execSQL(new GoodsItem().setGoods(a[0],
						a[1], a[2], a[3]).toString());
			}
			in.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (SQLException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		return 0;
		
		
	}
	

}
