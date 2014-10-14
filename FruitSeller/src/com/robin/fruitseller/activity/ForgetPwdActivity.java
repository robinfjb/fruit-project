package com.robin.fruitseller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruitseller.R;

public class ForgetPwdActivity extends BaseActivity implements OnClickListener {
	private ImageView backImg;
	private EditText phoneInput;
	private EditText codeInput;
	private EditText pwdInput;
	private TextView getCodeBtn;
	private TextView saveBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		phoneInput = (EditText) findViewById(R.id.login_phone);
		codeInput = (EditText) findViewById(R.id.login_code);
		pwdInput = (EditText) findViewById(R.id.login_pwd);
		getCodeBtn = (TextView) findViewById(R.id.get_password);
		getCodeBtn.setOnClickListener(this);
		saveBtn = (TextView) findViewById(R.id.ivTitleRight);
		saveBtn.setOnClickListener(this);
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
		case R.id.get_password:
			getPassCode();
			break;
		case R.id.ivTitleRight:
			String phone = phoneInput.getText().toString().trim();
			if(TextUtils.isEmpty(phone)) {
				Toast.makeText(ForgetPwdActivity.this, getString(R.string.warn_empty_phone), Toast.LENGTH_LONG).show();
				return;
			}
			String code = codeInput.getText().toString().trim();
			if(TextUtils.isEmpty(code)) {
				Toast.makeText(ForgetPwdActivity.this, getString(R.string.warn_empty_code), Toast.LENGTH_LONG).show();
				return;
			}
			String pwd = pwdInput.getText().toString().trim();
			if(TextUtils.isEmpty(pwd)) {
				Toast.makeText(ForgetPwdActivity.this, getString(R.string.warn_empty_pwd), Toast.LENGTH_LONG).show();
				return;
			}
			SendPasscodeTask task = new SendPasscodeTask(ForgetPwdActivity.this, phone,code, Utils.md5(pwd));
			task.execute();
			break;
		}
	}
	
	private void getPassCode() {
		String phone = phoneInput.getText().toString().trim();
		if(TextUtils.isEmpty(phone)) {
			Toast.makeText(ForgetPwdActivity.this, getString(R.string.warn_empty_phone), Toast.LENGTH_LONG).show();
			return;
		}
		GetPasscodeTask task = new GetPasscodeTask(this, phone);
		task.execute();
	}
	
	private class SendPasscodeTask extends RGenericTask<Boolean> {
		private String phone;
		private String code;
		private String pwd;
		public SendPasscodeTask(Context ctx, String phone, String code, String pwd) {
			super(ctx);
			this.phone = phone;
			this.code = code;
			this.pwd = pwd;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.forgetPassCode(phone, code, pwd);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				setResult(Activity.RESULT_OK);
				finish();
				overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
			}
		}
		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
		}
		@Override
		protected void onTaskBegin() {
			showProgressBar();	
		}
		@Override
		protected void onTaskFinished() {
			hideProgressBar();
		}
	}
	
	private class GetPasscodeTask extends RGenericTask<String> {
		private String phone;
		public GetPasscodeTask(Context ctx, String phone) {
			super(ctx);
			this.phone = phone;
		}

		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(ForgetPwdActivity.this);
			return api.getSellerPassCodeRenewPwd(phone);
		}

		@Override
		protected void onSuccess(String result) {
			Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
		}
		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
		}
		@Override
		protected void onTaskBegin() {
			showProgressBar();	
		}
		@Override
		protected void onTaskFinished() {
			hideProgressBar();
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}

}
