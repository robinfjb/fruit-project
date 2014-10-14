package com.robin.fruitseller.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruitseller.R;

public class SetPwdActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private TextView saveTxt;
	private EditText oldPwdEdit;
	private EditText newPwdEdit;
	private EditText confirmPwdEdit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_pwd);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		saveTxt = (TextView) findViewById(R.id.ivTitleRight);
		saveTxt.setOnClickListener(this);
		oldPwdEdit = (EditText) findViewById(R.id.old_pwd);
		newPwdEdit = (EditText) findViewById(R.id.new_pwd);
		confirmPwdEdit = (EditText) findViewById(R.id.confirm_pwd);
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
		case R.id.ivTitleRight:
			String textOld = oldPwdEdit.getText().toString().trim();
			String textNew = newPwdEdit.getText().toString().trim();
			String textCon = confirmPwdEdit.getText().toString().trim();
			if(TextUtils.isEmpty(textOld) || TextUtils.isEmpty(textNew) || TextUtils.isEmpty(textCon)) {
				Toast.makeText(SetPwdActivity.this, getString(R.string.warn_empty_pwd_old), Toast.LENGTH_SHORT).show();
				return;
			}
			if(!textOld.equals(FruitPerference.getShopPwd(SetPwdActivity.this))) {
				Toast.makeText(SetPwdActivity.this, getString(R.string.warn_pwd_old), Toast.LENGTH_SHORT).show();
				return;
			}
			if(!textNew.equals(textCon)) {
				Toast.makeText(SetPwdActivity.this, getString(R.string.warn_empty_pwd_new), Toast.LENGTH_SHORT).show();
				return;
			}
			if(textNew.equals(textOld)) {
				Toast.makeText(SetPwdActivity.this, getString(R.string.warn_empty_pwd_new_old), Toast.LENGTH_SHORT).show();
				return;
			}
			RenewPwdTask task = new RenewPwdTask(this, textNew);
			task.execute();
			break;
		}
	}
	
	private class RenewPwdTask extends RGenericTask<Boolean> {
		private String name;
		public RenewPwdTask(Context ctx, String name) {
			super(ctx);
			this.name = name;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			ArrayList<String> keys = new ArrayList<String>();
			keys.add("passwd");
			ArrayList<String> values = new ArrayList<String>();
			values.add(Utils.md5(name));
			return api.renewShopInfo(keys, values);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				FruitPerference.saveShopPwd(SetPwdActivity.this, name);
				setResult(Activity.RESULT_OK);
				finish();
				overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}
		@Override
		protected void onTaskBegin() {
			hideProgressBar();
		}
		@Override
		protected void onTaskFinished() {
			showProgressBar();
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
