package com.example.barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.JohnnyWorks.videoNpix.MainActivity;
import com.JohnnyWorks.videoNpix.MockScreen;
import com.JohnnyWorks.videoNpix.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;



public class Main extends Activity {
	TextView tv;
	ScrollView svBarcode;
	Button button;
	DBHelper dbhelper;
	SQLiteDatabase db;
	String name[] = new String[100], price[] = new String[100],detail[] = new String[100];
	
	int num = 0;
	TextView textView;
//	ListView listView;
	Button bu;
	Menu menu00;
//	ArrayAdapter<String> adapter;
	List<String> data = new ArrayList<String>();
	SoundPool soundPool;
	int spId;
	AudioManager am;
	AlertDialog.Builder alert;
	Boolean isShowAlert = false;
	Boolean isFromPause = false;
	Boolean isFirstTime = false;
	ImageView imgv=null;
	String res = "";
	KeyCodeTable kt = null;
	int videoPos;
	
	int count=0;
	Timer timer=null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.JohnnyWorks.videoNpix.R.layout.activity_main0);

		if (getIntent().hasExtra("videoPos")) {
			videoPos = getIntent().getIntExtra("videoPos",0);
		}

		kt=new KeyCodeTable();
		dbhelper = new DBHelper(this);
		db = dbhelper.getReadableDatabase();
		textView =(TextView) findViewById(R.id.textView1);
		imgv=(ImageView) findViewById(R.id.imageView1);
        timer=new Timer();
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        spId = soundPool.load(this,R.raw.di, 1);
        
        if(getIntent().hasExtra("res"))res=getIntent().getStringExtra("res");
        
        alert = new AlertDialog.Builder(this);   

        getinfo(res);
        res="";
        timer.schedule(timerTask, 1000,1000);
	}



	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 155 || keyCode == 156)
			res = res + '\t';
		else if (keyCode == 66) {
			if(res=="")return true;
			getinfo(res);
			res="";
			System.out.println(res);
		} else if (kt.getChar(keyCode) == ' ')
			;
		else
			res = res + new KeyCodeTable().getChar(keyCode);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		detail = null;
		deleteDatabase("barcode.db");
		db.close();
		super.onDestroy();
	}
	Map<String, Integer> barcodemap=new HashMap<String, Integer>();
	
	private void getinfo(String barcode) {
		//System.out.println(barcode);
		if (num < name.length) {

			soundPool.play(spId, 1, 1, 1, 0, 1);

			Cursor cursor = db.rawQuery(
					"select * from products where barcode=?",
					new String[] { String.valueOf(barcode) });

			if (cursor.moveToFirst()) {
				name[num] = cursor.getString(cursor.getColumnIndex("name"));
				price[num] = cursor.getString(cursor.getColumnIndex("price"));
				detail[num] = getResources().getString(R.string.Product)+"『"
						+ cursor.getString(cursor.getColumnIndex("name"))
						+ "』\n" +getResources().getString(R.string.Barcode)+ barcode + "\n\n"+
						getResources().getString(R.string.Details)+"\n"
						+ cursor.getString(cursor.getColumnIndex("detail"));
			}

			cursor.close();

			if (name[num] == null || price[num] == null) {
				String ii=getResources().getString(R.string.NONO);
				name[num] = ii;
				detail[num] = ii;
				data.add(barcode + "　" + name[num]);
			} else {
				data.add(barcode + "『" + name[num] + "』　　" + price[num]);
			}
			System.out.println(detail[num]);
			System.out.println(num);
			textView.setText(detail[num]);
			System.out.println(barcode);
			if(barcode!="")
				if(new GoodsItem().getR(barcode)==0){					
					imgv.setBackgroundDrawable(getResources().getDrawable(R.drawable.noimg));
					imgv.setVisibility(8);
				}
				//	imgv.setBackgroundDrawable(getResources().getDrawable(R.drawable.noimg));
				else if (new GoodsItem().getR(barcode)==-1) {
					;
				}else{					
					imgv.setBackgroundDrawable(getResources().getDrawable(new GoodsItem().getR(barcode)));
					imgv.setVisibility(0);
				}
					
			count=0;
			num++;			

			
		} else {

			if (!isShowAlert) {
				isShowAlert = true;

				alert.setTitle("Prompt");
				alert.setMessage("\nThe number of products exceeds "
						+ name.length + ". Your history will be cleared.\n");

				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								data.clear();
								num = 0;
								for (int l = 0; l < name.length; l++) {
									name[l] = null;
									price[l] = null;
									detail[l] = null;
								}

								isShowAlert = false;
							}
						});

				alert.setCancelable(false);
				alert.show();
			}
		}
		
		
	}

	TimerTask timerTask = new TimerTask() {
		
		public void run() {
			count++;
			System.out.println("qq"+count);
			if(count==10){
				Intent intent1=new Intent(Main.this, MockScreen.class);
				intent1.putExtra("startFromPlayer","");
				intent1.putExtra("videoPos", videoPos);
				startActivity(intent1);
				

	            timer.cancel();
	            finish();
			}		
		}
	};
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:		
			Intent intent1=new Intent(Main.this, MockScreen.class);
			intent1.putExtra("startFromPlayer","");
			intent1.putExtra("videoPos", videoPos+1);
			startActivity(intent1);
			timer.cancel();
			finish();
			break;
		}
		return true;
	}
}
