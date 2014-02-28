package com.JohnnyWorks.videoNpix;

import com.JohnnyWorks.videoNpix.R;
import com.JohnnyWorks.videoNpix.HideStatusBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class LoginActivity extends Activity {
	
	private SharedPreferences preferences;
	private Editor editor;
	RadioButton rButton1, rButton2, rButton3, rButton4, rButton5,rButton6,rButton7,rButton8,rButton9,rButton10,rButton11;
	Button button1, button2;
	boolean autostart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		onConfigurationChanged(getResources().getConfiguration());	
		if (!ZuniMachineLib.IsZuniMachine()) {
			finish();
			HideStatusBar.disable();
		}
		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		editor = preferences.edit();
		autostart=preferences.getBoolean("autostart", false);
		editor.clear().commit();		
		    rButton1 = (RadioButton) findViewById(R.id.radioButton1);
		    rButton2 = (RadioButton) findViewById(R.id.radioButton2);
		    rButton3 = (RadioButton) findViewById(R.id.radioButton3);
		    rButton4 = (RadioButton) findViewById(R.id.radioButton4);
		    rButton5 = (RadioButton) findViewById(R.id.radioButton5);
		    rButton6 = (RadioButton) findViewById(R.id.radioButton6);
		    rButton7 = (RadioButton) findViewById(R.id.radioButton7);
		    rButton8 = (RadioButton) findViewById(R.id.radioButton8);
		    rButton9 = (RadioButton) findViewById(R.id.radioButton9);
		    rButton10 = (RadioButton) findViewById(R.id.radioButton10);
		    rButton11 = (RadioButton) findViewById(R.id.radioButton11);
		    button1 = (Button) findViewById(R.id.button1);
		    button2 = (Button) findViewById(R.id.button2);
		    
		    rButton3.setChecked(true);
		    rButton5.setChecked(true);
		    rButton6.setChecked(true);
		    rButton11.setChecked(true);
		    if(autostart)rButton10.setChecked(true);
		    else rButton11.setChecked(true);
		    button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					HideStatusBar.disable();
				}
			});
		    
		    button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if (rButton1.isChecked()) {
			        	editor.putInt("pixnum", 2);
					    editor.commit();
			        } else if (rButton2.isChecked()) {
			        	editor.putInt("pixnum", 4);
					    editor.commit();
			        } else if(rButton3.isChecked()) {
			        	editor.putInt("pixnum", 6);
					    editor.commit();
			        } 
					
					if (rButton4.isChecked()) {
			        	editor.putBoolean("isRandomPlay", true);
					    editor.commit();
					} else if (rButton5.isChecked()) {
			        	editor.putBoolean("isRandomPlay", false);
					    editor.commit();
					}
					
					if (rButton6.isChecked()) {
			        	editor.putInt("time", 3);
					    editor.commit();
					} else if (rButton7.isChecked()) {
			        	editor.putInt("time", 10);
					    editor.commit();
					} else if (rButton8.isChecked()) {
			        	editor.putInt("time", 30);
					    editor.commit();
					} else if (rButton9.isChecked()) {
			        	editor.putInt("time", 60);
					    editor.commit();
					}
					if(rButton10.isChecked())autostart=true;
					else autostart=false;
					editor.putBoolean("autostart", autostart);
					editor.commit();
					Intent intent = new Intent(LoginActivity.this, MockScreen.class);
					startActivity(intent);
					finish();
					
				}
			});
			HideStatusBar.enable();
	}


	@Override
	public void onBackPressed() {
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
			setContentView(R.layout.login);
		super.onConfigurationChanged(newConfig);
	}


}
