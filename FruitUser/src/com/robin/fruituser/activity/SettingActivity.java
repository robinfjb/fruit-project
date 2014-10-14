package com.robin.fruituser.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sgone.fruituser.R;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.Utils;
import com.robin.fruituser.UserApplication;

public class SettingActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private LinearLayout aboutArea;
	private RelativeLayout updateArea;
	private LinearLayout intorduceArea;
	private Button logoutBtn;
	private TextView versionTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		aboutArea = (LinearLayout) findViewById(R.id.about);
		updateArea = (RelativeLayout) findViewById(R.id.update);
		intorduceArea = (LinearLayout) findViewById(R.id.intorduce);
		logoutBtn = (Button) findViewById(R.id.logout);
		versionTxt = (TextView) findViewById(R.id.version_txt);
		aboutArea.setOnClickListener(this);
		updateArea.setOnClickListener(this);
		intorduceArea.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);
		if(!TextUtils.isEmpty(FruitPerference.getUserMobile(this))) {
			logoutBtn.setVisibility(View.VISIBLE);
		} else {
			logoutBtn.setVisibility(View.GONE);
		}
		versionTxt.setText(Utils.getVersionStr(this));
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			setResult(Activity.RESULT_CANCELED);
			finish();
			overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
			break;
		case R.id.about:
			startActivity(new Intent(SettingActivity.this, AboutActivity.class));
			((Activity) SettingActivity.this).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.update:
			if(UserApplication.deviceBean.version > getVersionName()) {
				Toast.makeText(SettingActivity.this, "有新版本", Toast.LENGTH_SHORT).show();
			}
			UpdateAppTask task = new UpdateAppTask(SettingActivity.this);
			task.execute();
			break;
		case R.id.intorduce:
			startActivity(new Intent(SettingActivity.this, FunctionActivity.class));
			((Activity) SettingActivity.this).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.logout:
			FruitPerference.removeUserMobile(SettingActivity.this);
			finish();
			overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
	
	AlertDialog installBuilder;
	private class UpdateAppTask extends RGenericTask<String> {
		public UpdateAppTask(Context ctx) {
			super(ctx);
		}
		@Override
		protected String getContent() throws HttpException {
			return null;
		}

		@Override
		protected void onSuccess(final String result) {
			if(!TextUtils.isEmpty(result)) {
				installBuilder = new AlertDialog.Builder(getApplicationContext())
				.setMessage("发现最新版本，是否去下载?")
				.setTitle("发现新版本")
				.setPositiveButton("下载",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									Uri uri = Uri.parse(result);
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(intent);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								installBuilder.dismiss();
							}
						}).create();
				installBuilder.show();
			} else {
				Toast.makeText(SettingActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		protected void onAnyError(int code, String msg) {}
		@Override
		protected void onTaskBegin() {}
		@Override
		protected void onTaskFinished() {}
	}
	
	private float getVersionName() {
           
           try {
        	// 获取packagemanager的实例
               PackageManager packageManager = getPackageManager();
               // getPackageName()是你当前类的包名，0代表是获取版本信息
               PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
               String version = packInfo.versionName;
        	  return Float.parseFloat(version);
           } catch(NumberFormatException e) {
        	   
           } catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           return 0.0f;
	}
}
