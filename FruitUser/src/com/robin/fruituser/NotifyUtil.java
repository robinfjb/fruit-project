package com.robin.fruituser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cn.sgone.fruituser.R;

public class NotifyUtil {
	public static void notifyUserCancel(Context mContext, String title, String content) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setClass(mContext, com.robin.fruituser.activity.HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("is_notify_cancel",true);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification mNotification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
		mNotification.defaults = Notification.DEFAULT_ALL;
		mNotification.tickerText = content;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.setLatestEventInfo(context, title, content, contentIntent);
		notificationManager.notify(R.drawable.ic_launcher + 1, mNotification);
	}
	
	public static void notifyUser(Context mContext, String title, String content) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setClass(mContext, com.robin.fruituser.activity.HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("is_notify",true);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification mNotification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
		mNotification.sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.buy);
//		mNotification.defaults = Notification.DEFAULT_ALL;
		mNotification.tickerText = content;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.setLatestEventInfo(context, title, content, contentIntent);
		notificationManager.notify(R.drawable.ic_launcher, mNotification);
	}
}
