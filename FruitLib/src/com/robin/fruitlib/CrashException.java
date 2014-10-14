package com.robin.fruitlib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.robin.fruitlib.R;

import com.umeng.analytics.MobclickAgent;

public class CrashException implements UncaughtExceptionHandler {
	private Context mContext;

	private Thread.UncaughtExceptionHandler defaultExceptionHandler;

	private static CrashException customException;
	
	private String msg;
	
	private CrashException() {
	}

	public static CrashException getInstance() {
		if (customException == null) {
			customException = new CrashException();
		}
		return customException;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		if (handleException(exception) && defaultExceptionHandler != null) {
			defaultExceptionHandler.uncaughtException(thread, exception);
			Log.e("uncaughtException--->CrashException", exception == null ? "null" : exception.getMessage());
		}
	}
	
	/**
	 * init parameter
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * notify excetion to user
	 * <br>add exception into log and print on strace
	 * @param ex
	 * @return
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return true;
		}
		msg = ex.getLocalizedMessage();
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				handler.sendEmptyMessage(0);
				StackTraceElement[] stacks = ex.getStackTrace();
				if(msg == null) {
					msg = "null";
				}
				for (StackTraceElement stack : stacks) {
					Log.e("CrashException", stack.getClassName() + ":" + stack.getMethodName() + "(" + stack.getLineNumber() + ")");
				}
				Writer w = new StringWriter();
				ex.printStackTrace(new PrintWriter(w));
				MobclickAgent.reportError(mContext, w.toString());
				Looper.loop();
			}

		}.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		killProcess();
		return true;
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			Toast.makeText(mContext, mContext.getResources(). getString(R.string.crash_msg), Toast.LENGTH_LONG).show();
			return false;
		}
	});
	/**
	 * kill the process
	 * <br>stop service and all activity
	 */
	private void killProcess() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(startMain);
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
