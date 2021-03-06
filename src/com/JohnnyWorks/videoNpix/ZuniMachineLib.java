package com.JohnnyWorks.videoNpix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.lang.reflect.Method;

import android.util.Log;

import com.zunidata.zunidataapi.ZunidataEnvironment;

public class ZuniMachineLib {
	private static final String TAG = "videoNpix";
	public static final String[] modelName = { "7UT", "10UT", "10N", "19N", 
		"6-APPC", "7-APPC", "10-APPC", "FMT-7AT", "FMT-10AT", "7N", "MB211", "MB222","10-APPC-DS","7-APPC","FMT-10DS","FMT-19AT","19APPC","FMT-BX1","ABPC-500","FMT-BX2","ABPC-520","FMT-07ATO","OF-07","FMT-10ATO","OF-010","FMT-18ATO","OF-180","FMT-7RT","7RT"};
	public static final String cmdStart_systemui = "am startservice -n com.android.systemui/.SystemUIService";
	public static final String cmdKill_systemui = "killall com.android.systemui";

	public static final Boolean debug_mode = false;
	public static final boolean useInternalMem = false;
	public static final boolean opt_playSingleFile = false;


	public static Boolean IsZuniMachine() {
		try{
			Boolean has=false;
			Class a=Class.forName("android.os.SystemProperties");
			Method getprop=a.getMethod("get",String.class);
			String str=(String)getprop.invoke(a,"ro.product.manufacturer");
			System.out.println("==="+str);
			//has = str.contains("ZD");
			has= str.toUpperCase().contains("ZD");
			System.out.println("==="+has);
			if(has)return has;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (debug_mode)
			return true;

		for (int i = 0; i < modelName.length; i++) {
			if (android.os.Build.MODEL.contains(modelName[i]))
				return true;
		}
		Log.e(TAG, "MODEL not match!");
		return false;
	}

	public static String RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		if (command == null)
			command = "";
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			is = new DataInputStream(process.getInputStream());
			while (is.available() > 0) {
				String result = is.readUTF();
				Log.v(TAG, "command=" + command + "  result=" + result);
				return result;
			}
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return null;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void logToText(String msg, String dir) {
		String vPath = null;
		FileWriter fWriter = null;
		try {

			if (ZuniMachineLib.useInternalMem)
				vPath = GlobalString.logpath;
						
			else {
				vPath = GlobalString.logpath;
						
			}
			fWriter = new FileWriter(
					vPath
							+ "log_"
							+ new java.text.SimpleDateFormat("yyyy_MM_dd").format(new Date())
							+ ".txt", true);

			String logString = "["
					+ new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date()) + "]" + msg + "\n";
			Log.v(TAG, logString);
			fWriter.append(logString);
			fWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
