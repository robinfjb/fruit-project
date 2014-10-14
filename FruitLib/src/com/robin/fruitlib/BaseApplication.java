package com.robin.fruitlib;

import com.baidu.frontia.FrontiaApplication;
import com.umeng.fb.FeedbackAgent;

import android.app.Application;

public class BaseApplication extends FrontiaApplication{
	 public static IGetActivityClass mIGetActivityClass;
	 public static FeedbackAgent agent;
	 public static String qiniuToken;
	 public static String qiniuTokenPrivate;
	 public static String channelId = "";
	 public static String userId = "";
	@Override
	public void onCreate() {
		super.onCreate();
		agent = new FeedbackAgent(this);
		CrashException customException = CrashException.getInstance();
        customException.init(getApplicationContext());
	}
}
