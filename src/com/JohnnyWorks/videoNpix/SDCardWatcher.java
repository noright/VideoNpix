package com.JohnnyWorks.videoNpix;

import com.zunidata.zunidataapi.ZunidataEnvironment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.widget.Toast;

public class SDCardWatcher
{
	private BroadcastReceiver mSDCardStateChangeListener;
	private SDRemovedListener mSdRemovedListener;
	private SDMountListener mSdMountListener;

	public void registerSDCardStateChangeListener(Activity act,
			SDRemovedListener mListener)
	{
		registerSDCardStateChangeListener(act, mListener, null);
	}

	public void registerSDCardStateChangeListener(Activity act,
			SDRemovedListener mListener, SDMountListener mListener2)
	{
		setSDRemovedListener(mListener);
		setSdMountListener(mListener2);
		// 判斷 SD Card 有無插入
		if (ZunidataEnvironment.getExternalStorageState().equals(
				Environment.MEDIA_REMOVED))
		{
			if (mSdRemovedListener != null)
				mSdRemovedListener.onSDRemoved();
			return;
		}
		if (ZunidataEnvironment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			if (mSdMountListener != null)
				mSdMountListener.onSDMounted();
			return;
		}

		mSDCardStateChangeListener = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context arg0, Intent arg1)
			{
				String action = arg1.getAction();
				if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_REMOVED)
						|| action
								.equalsIgnoreCase(Intent.ACTION_MEDIA_UNMOUNTED)
						|| action
								.equalsIgnoreCase(Intent.ACTION_MEDIA_BAD_REMOVAL)
						|| action.equalsIgnoreCase(Intent.ACTION_MEDIA_EJECT))
				{
					if (mSdRemovedListener != null)
						mSdRemovedListener.onSDRemoved();
				}

				if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED))
				{
					if (mSdMountListener != null)
						mSdMountListener.onSDMounted();
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addDataScheme("file");
		act.registerReceiver(mSDCardStateChangeListener, filter);
	}

	public void unRegisterSDCardStateChangeListener(Activity act)
	{
		if (mSDCardStateChangeListener != null)
		{
			act.unregisterReceiver(mSDCardStateChangeListener);
			mSDCardStateChangeListener = null;
		}
	}

	public SDRemovedListener getSDRemovedListener()
	{
		return mSdRemovedListener;
	}

	public void setSDRemovedListener(SDRemovedListener mSdRemovedListener)
	{
		this.mSdRemovedListener = mSdRemovedListener;
	}

	public SDMountListener getSdMountListener()
	{
		return mSdMountListener;
	}

	public void setSdMountListener(SDMountListener mSdMountListener)
	{
		this.mSdMountListener = mSdMountListener;
	}

}

interface SDRemovedListener
{
	public void onSDRemoved();
}

interface SDMountListener
{
	public void onSDMounted();
}
