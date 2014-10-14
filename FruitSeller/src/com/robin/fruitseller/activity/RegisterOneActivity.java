package com.robin.fruitseller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruitseller.R;

public class RegisterOneActivity extends BaseActivity implements OnClickListener{
	private Button loginBtn;
	private EditText phoneInput;
	private EditText codeInput;
	private EditText pwdInput;
	private TextView getCodeBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_one);
		loginBtn = (Button) findViewById(R.id.next);
		phoneInput = (EditText) findViewById(R.id.login_phone);
		codeInput = (EditText) findViewById(R.id.login_code);
		pwdInput = (EditText) findViewById(R.id.login_pwd);
		getCodeBtn = (TextView) findViewById(R.id.get_password);
		loginBtn.setOnClickListener(this);
		getCodeBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.get_password:
			getPassCode();
			break;
		case R.id.next:
			String phone = phoneInput.getText().toString().trim();
			if(TextUtils.isEmpty(phone)) {
				Toast.makeText(RegisterOneActivity.this, getString(R.string.warn_empty_phone), Toast.LENGTH_LONG).show();
				return;
			}
			String code = codeInput.getText().toString().trim();
			if(TextUtils.isEmpty(code)) {
				Toast.makeText(RegisterOneActivity.this, getString(R.string.warn_empty_code), Toast.LENGTH_LONG).show();
				return;
			}
			String pwd = pwdInput.getText().toString().trim();
			if(TextUtils.isEmpty(pwd)) {
				Toast.makeText(RegisterOneActivity.this, getString(R.string.warn_empty_pwd), Toast.LENGTH_LONG).show();
				return;
			}
			SendPasscodeTask task = new SendPasscodeTask(RegisterOneActivity.this, phone,code, pwd);
			task.execute();
			/*Intent intent = new Intent(RegisterOneActivity.this, RegisterTwoActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);*/
			break;
		}
	}
	
	private void getPassCode() {
		String phone = phoneInput.getText().toString().trim();
		if(TextUtils.isEmpty(phone)) {
			Toast.makeText(RegisterOneActivity.this, getString(R.string.error_empty_phone), Toast.LENGTH_LONG).show();
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
			return api.submitPassCode(phone, code);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				FruitPerference.saveShopPwd(RegisterOneActivity.this, pwd);
				Intent intent = new Intent(RegisterOneActivity.this, RegisterTwoActivity.class);
				intent.putExtra("phone", phone);
				intent.putExtra("code", code);
				intent.putExtra("pwd", pwd);
				startActivity(intent);
				overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
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
			FruitApi api = new FruitApi(RegisterOneActivity.this);
			return api.getSellerPassCode(phone);
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
