package com.JohnnyWorks.videoNpix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 轉交給BootupInit處理
//		ZuniMachineLib.logToText("Receive boot signal, starting MockScr...");
		Intent i = new Intent(context, MockScreen.class);
		i.putExtra("DelayStart", true);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		// String packageStr = "com.skype.raider";
		// String activityStr = "com.skype.raider.Main";
		//
		// Intent intent1 = new Intent(Intent.ACTION_MAIN, null);
		// intent1.addCategory(Intent.CATEGORY_LAUNCHER);
		// intent1.setComponent(new ComponentName(packageStr, activityStr));
		// intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intent1);

	}

}
