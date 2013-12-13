package com.JohnnyWorks.videoNpix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootUpReceiver extends BroadcastReceiver {
	SharedPreferences settings;
	@Override
	public void onReceive(Context context, Intent intent) {
		settings=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		if(settings.getBoolean("autostart", true)){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent i = new Intent(context, MockScreen.class);
			i.putExtra("DelayStart", true);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}

}
