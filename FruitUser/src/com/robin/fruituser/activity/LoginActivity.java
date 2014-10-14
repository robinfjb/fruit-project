package com.robin.fruituser.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.UserData;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruituser.R;

public class LoginActivity extends BaseActivity implements OnClickListener{
	private EditText phoneInput;
	private EditText codeInput;
	private CheckBox checkBox;
	private TextView txtContract;
	private TextView sendCodeBtn;
	private Button confirmBtn;
	private ImageView backImg;
	private CountDownTimer mCountDownTimer;
	private static final int WAITING_MILLISECONDS = 30000;
	private static final int INTERVAL_MILLISECONDS = 1000;
	private String phoneStr;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		phoneInput = (EditText) findViewById(R.id.login_phone);
		codeInput = (EditText) findViewById(R.id.login_code);
		checkBox = (CheckBox) findViewById(R.id.checkbox);
		txtContract = (TextView) findViewById(R.id.txt_contract);
		confirmBtn = (Button) findViewById(R.id.confirm);
		sendCodeBtn = (TextView) findViewById(R.id.get_password);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		confirmBtn.setOnClickListener(this);
		sendCodeBtn.setOnClickListener(this);
		checkBox.setOnClickListener(this);
		txtContract.setOnClickListener(this);
		backImg.setOnClickListener(this);
	}

	
	@Override
	protected void onDestroy() {
		if(mCountDownTimer != null){
			mCountDownTimer.cancel();
		}
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.confirm:
			phoneStr = phoneInput.getText().toString().trim();
			if(TextUtils.isEmpty(phoneStr)) {
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				return;
			}
			String passcode = codeInput.getText().toString().trim();
			if(TextUtils.isEmpty(passcode)) {
				Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_LONG).show();
				return;
			}
			if(!checkBox.isChecked()) {
				Toast.makeText(this, "请先勾选用户协议", Toast.LENGTH_LONG).show();
				return;
			}
			sendUserAndPwd(phoneStr, passcode);
			break;
		case R.id.get_password:
			phoneStr = phoneInput.getText().toString().trim();
			if(TextUtils.isEmpty(phoneStr)) {
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				return;
			}
			mCountDownTimer = new MyCountDownTimer(WAITING_MILLISECONDS, INTERVAL_MILLISECONDS);
			mCountDownTimer.start();
			getPwd();
			break;
		case R.id.txt_contract:
//			showContract();
			break;
		case R.id.ivTitleBtnLeft:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		default:
			break;
		}
	}
	
	private void getPwd() {
		GetPasscodeTask task = new GetPasscodeTask(LoginActivity.this);
		task.execute();
	}
	
	private void sendUserAndPwd(String userName, String password){
		/*FruitPerference.saveUserMobile(LoginActivity.this, phoneStr);
		setResult(Activity.RESULT_OK);
		finish();*/
		LoginTask task = new LoginTask(LoginActivity.this, userName, password);
		task.execute();
	}
	
	private void showContract() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", "file:///android_asset/contract.html");
		startActivity(intent);
	}
	
	private class LoginTask extends RGenericTask<UserData> {
		private String userName;
		private String pwd;

		public LoginTask(Context ctx, String userName, String pwd) {
			super(ctx);
			this.userName = userName;
			this.pwd = pwd;
		}

		@Override
		protected UserData getContent() throws HttpException {
			FruitApi api = new FruitApi(LoginActivity.this);
			return api.login(userName,pwd);
		}

		@Override
		protected void onSuccess(UserData result) {
			FruitPerference.saveUserMobile(LoginActivity.this, phoneStr);
			FruitPerference.saveUserData(LoginActivity.this, result);
			setResult(Activity.RESULT_OK);
			finish();
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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

		public GetPasscodeTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(LoginActivity.this);
			return api.getPassCode(phoneStr);
		}

		@Override
		protected void onSuccess(String result) {
			Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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
	
	private class MyCountDownTimer extends CountDownTimer{
		
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void onTick(long millisUntilFinished) {
			sendCodeBtn.setText("获取中" + millisUntilFinished / 1000);
			sendCodeBtn.setTextColor(Color.parseColor("#aaaaaa"));
			sendCodeBtn.setEnabled(false);
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void onFinish() {
			sendCodeBtn.setText("获取验证码");
			sendCodeBtn.setTextColor(Color.parseColor("#99cc00"));
			sendCodeBtn.setEnabled(true);
			this.cancel();
		}
		
	}

}
