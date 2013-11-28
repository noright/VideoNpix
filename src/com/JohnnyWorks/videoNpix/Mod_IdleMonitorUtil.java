package com.JohnnyWorks.videoNpix;

import android.os.Handler;

/**
 * 使用者閒置偵測
 * 
 * @author Johnny
 * 
 */
public abstract class Mod_IdleMonitorUtil
{
	private static final int USER_IDLE_TIMEOUT = 8; // 預設值
	private int IdleTimeout = USER_IDLE_TIMEOUT;
	private int userIdleTimer = IdleTimeout; // 計時器
	private static boolean active = false;
	public boolean touchStatus;

	public static boolean isActive()
	{
		return active;
	}

	private Handler mHandler;

	/** 建構子 */
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
			// 畫面顯示計時器(需實做)
			timerTick(userIdleTimer);
			if (userIdleTimer > 0)
			{
				mHandler.postDelayed(idleMonitor, 1000);
			} else
			{
				// 到達閒置時間，啟動螢幕保護程式
				mHandler.removeCallbacks(idleMonitor);
				// 啟動螢幕保護程式的方法(需實做)
				startScreenSaverAct();
			}
		}

	};

	/** 重設閒置計時器 */
	public void resetTimer()
	{
		userIdleTimer = getTimeout();
		timerTick(userIdleTimer);
	}

	/** 重設閒置計時器 */
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

	/** 啟動閒置計時器 */
	public void startTimer()
	{
		if (isActive())
			return;
		active = true;
		mHandler.post(idleMonitor);
	}

	/** 停止閒置計時器 */
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
	/** 啟動螢幕保護程式的方法(需實做) */
	protected abstract void startScreenSaverAct();

	/** 畫面顯示計時器的方法(需實做) */
	protected abstract void timerTick(int counter);

	public int getTimeout()
	{
		return IdleTimeout;
	}

	public void setTimeout(int idleTimeout)
	{
		IdleTimeout = idleTimeout;
	}
	
//	public void setTouchStatus(boolean touchStatus) {
//		this.touchStatus = touchStatus;
//	}
//	
//	public boolean getTouchStatus() {
//		return touchStatus;
//	}
}
