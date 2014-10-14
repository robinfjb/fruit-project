package com.robin.fruitseller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.SellerBean;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.UrlConst;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.SellerApplication;

public class LoginActivity extends BaseActivity implements OnClickListener {
	private TextView registerTxt;
	private TextView forgetPwdTxt;
	private Button loginBtn;
	private EditText phoneInput;
	private EditText codeInput;
	private String phoneStr;
	private String passcode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		registerTxt = (TextView) findViewById(R.id.register);
		registerTxt.setOnClickListener(this);
		forgetPwdTxt = (TextView) findViewById(R.id.forget_pwd);
		forgetPwdTxt.setOnClickListener(this);
		loginBtn = (Button) findViewById(R.id.confirm);
		loginBtn.setOnClickListener(this);
		phoneInput = (EditText) findViewById(R.id.login_phone);
		codeInput = (EditText) findViewById(R.id.login_code);
		
	}

	private final static int INTENT_REGISTER = 1;
	private final static int INTENT_FORGET = 2;
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.register:
			Intent intentReg = new Intent(LoginActivity.this, RegisterOneActivity.class);
			startActivityForResult(intentReg, INTENT_REGISTER);
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.forget_pwd:
			Intent intentFor = new Intent(LoginActivity.this, ForgetPwdActivity.class);
			startActivityForResult(intentFor, INTENT_FORGET);
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.confirm:
			phoneStr = phoneInput.getText().toString().trim();
			if(TextUtils.isEmpty(phoneStr)) {
				Toast.makeText(this, getString(R.string.warn_empty_phone), Toast.LENGTH_LONG).show();
				return;
			}
			passcode = codeInput.getText().toString().trim();
			if(TextUtils.isEmpty(passcode)) {
				Toast.makeText(this, getString(R.string.warn_empty_code), Toast.LENGTH_LONG).show();
				return;
			}
			sendUserAndPwd(phoneStr, passcode);
			break;
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == INTENT_FORGET) {
			if(resultCode == RESULT_OK) {
				Toast.makeText(this, getString(R.string.success_forget_pwd), Toast.LENGTH_SHORT).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	private void sendUserAndPwd(String userName, String password){
		/*FruitPerference.saveSellerMobile(LoginActivity.this, phoneStr);
		setResult(Activity.RESULT_OK);
		finish();*/
		
		LoginTask task = new LoginTask(LoginActivity.this, userName, password);
		task.execute();
	}
	
	
	
	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}



	private class LoginTask extends RGenericTask<SellerBean> {
		private String userName;
		private String pwd;

		public LoginTask(Context ctx, String userName, String pwd) {
			super(ctx);
			this.userName = userName;
			this.pwd = pwd;
		}

		@Override
		protected SellerBean getContent() throws HttpException {
			FruitApi api = new FruitApi(LoginActivity.this);
			return api.loginSeller(userName,Utils.md5(pwd));
		}

		@Override
		protected void onSuccess(SellerBean result) {
			if(!TextUtils.isEmpty(result.sId)) {
//				String backupDBPath = "file://" + Environment.getExternalStorageDirectory() + "/" + UrlConst.SELLER_CACHE_NAME + "/img/";
				FruitPerference.saveShoperImage(LoginActivity.this, result.shopUrl + "/" +result.shopImage);
				FruitPerference.saveZhizhaoImage(LoginActivity.this, result.shopUrl + "/" + result.shopLicence);
				SellerApplication.picUrl1 = result.shopUrl + "/" +result.shopImage;
				SellerApplication.picUrl2 = result.shopUrl + "/" + result.shopLicence;
				FruitPerference.saveSellerMobile(LoginActivity.this, phoneStr, passcode);
				FruitPerference.saveShopName(LoginActivity.this, result.shopName);
				FruitPerference.saveShopAddress(LoginActivity.this, result.address);
				FruitPerference.saveShopPhone(LoginActivity.this, result.sTel);
				FruitPerference.saveShopStatus(LoginActivity.this, result.status);
				FruitPerference.saveShopPwd(LoginActivity.this, pwd);
				SellerApplication.status = result.status;
				setResult(Activity.RESULT_OK);
				finish();
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
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
}
