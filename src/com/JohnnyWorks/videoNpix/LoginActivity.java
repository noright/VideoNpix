package com.JohnnyWorks.videoNpix;

import com.JohnnyWorks.videoNpix.R;
import com.JohnnyWorks.videoNpix.HideStatusBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
	RadioButton rButton1, rButton2, rButton3, rButton4, rButton5;
	Button button1, button2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);
		
		if (!ZuniMachineLib.IsZuniMachine()) {
			finish();
			HideStatusBar.disable();
		}
		
		preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		editor = preferences.edit();
		editor.clear().commit();
		
		    
		    rButton1 = (RadioButton) findViewById(R.id.radioButton1);
		    rButton2 = (RadioButton) findViewById(R.id.radioButton2);
		    rButton3 = (RadioButton) findViewById(R.id.radioButton3);
		    rButton4 = (RadioButton) findViewById(R.id.radioButton4);
		    rButton5 = (RadioButton) findViewById(R.id.radioButton5);
		    button1 = (Button) findViewById(R.id.button1);
		    button2 = (Button) findViewById(R.id.button2);
		    
		    rButton1.setChecked(true);
		    rButton4.setChecked(true);
		    
		    
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
					
					Intent intent = new Intent(LoginActivity.this, MockScreen.class);
					startActivity(intent);
					finish();
					
				}
			});
			
//		} else {
//			Intent intent = new Intent(LoginActivity.this, Start3.class);
//			startActivity(intent);
//			finish();
//		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		HideStatusBar.enable();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public void onBackPressed() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	


}
