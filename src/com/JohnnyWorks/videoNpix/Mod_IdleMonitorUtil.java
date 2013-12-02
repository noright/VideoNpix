package com.JohnnyWorks.videoNpix;

import android.os.Handler;

/**
 * 
 * @author Johnny
 * 
 */
public abstract class Mod_IdleMonitorUtil
{
	private int IdleTimeout = GlobalString.time;
	private int userIdleTimer = IdleTimeout; 
	private static boolean active = false;
	public boolean touchStatus;

	public static boolean isActive()
	{
		return active;
	}

	private Handler mHandler;

	public Mod_IdleMonitorUtil()
	{
		mHandler = new Handler();
	}

	public Mod_IdleMonitorUtil(int timeOut)
	{
		setTimeout(timeOut);
		mHandler = new Handler();
	}

	private Runnable idleMonitor = new Runnable()
	{

		@Override
		public void run()
		{
			userIdleTimer = userIdleTimer - 1;

			timerTick(userIdleTimer);
			if (userIdleTimer > 0)
			{
				mHandler.postDelayed(idleMonitor, 1000);
			} else
			{
			
				mHandler.removeCallbacks(idleMonitor);
				startScreenSaverAct();
			}
		}

	};


	public void resetTimer()
	{
		userIdleTimer = getTimeout();
		timerTick(userIdleTimer);
	}


	public void resetTimer(int timeOut)
	{
		if (timeOut <= 0)
		{
			stopTimer();
			return;
		}
		userIdleTimer = getTimeout();
		timerTick(userIdleTimer);
	}


	public void startTimer()
	{
		if (isActive())
			return;
		active = true;
		mHandler.post(idleMonitor);
	}


	public void stopTimer()
	{
		if (!isActive())
			return;
		resetTimer();
		active = false;
		if (mHandler != null)
			mHandler.removeCallbacks(idleMonitor);
	}

	// ---------------------------------------------

	protected abstract void startScreenSaverAct();


	protected abstract void timerTick(int counter);

	public int getTimeout()
	{
		return IdleTimeout;
	}

	public void setTimeout(int idleTimeout)
	{
		IdleTimeout = idleTimeout;
	}

}
