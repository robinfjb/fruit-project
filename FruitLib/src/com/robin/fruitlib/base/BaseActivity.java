package com.robin.fruitlib.base;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.robin.fruitlib.R;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends SherlockFragmentActivity  {
	protected static final int RESULT_CODE_LOGIN = 0x01;
	protected Dialog mDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getSupportActionBar().hide();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment
	 * arguments.
	 */
	public static Bundle intentToFragmentArguments(Intent intent) {
		Bundle arguments = new Bundle();
		if (intent == null) {
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null) {
			arguments.putParcelable("_uri", data);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			arguments.putAll(intent.getExtras());
		}

		return arguments;
	}
	
	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments) {
		Intent intent = new Intent();
		if (arguments == null) {
			return intent;
		}

		final Uri data = arguments.getParcelable("_uri");
		if (data != null) {
			intent.setData(data);
		}

		intent.putExtras(arguments);
		intent.removeExtra("_uri");
		return intent;
	}
	
	/**
	 * 显示加载窗
	 */
	public void showProgressBar() {
		try {// to avoid BinderProxy is not valid error
			if(mDialog == null) {
				mDialog = new Dialog(this, R.style.CustomDialogThemeWithBg);
				View root = getLayoutInflater().inflate(R.layout.dialog_loading, null);
				mDialog.setContentView(root);
			}
			mDialog.show();
			mDialog.setCanceledOnTouchOutside(false);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 隐藏加载窗
	 */
	public void hideProgressBar() {
		try {// to avoid BinderProxy is not valid error
			if (mDialog != null) {
				mDialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
