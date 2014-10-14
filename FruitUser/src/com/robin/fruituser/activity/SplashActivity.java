package com.robin.fruituser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.SendReturnBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;

import cn.sgone.fruituser.R;

public class SplashActivity extends BaseActivity {
	private MyCountDownTimer mCountDownTimer;
	private static final int WAITING_MILLISECONDS = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		mCountDownTimer = new MyCountDownTimer(WAITING_MILLISECONDS,WAITING_MILLISECONDS);
		mCountDownTimer.start();
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private class MyCountDownTimer extends CountDownTimer {

		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}

		@Override
		public void onFinish() {
			Intent intent = new Intent(SplashActivity.this, UserGuideActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
			/*new Thread(new Runnable() {
				
				@Override
				public void run() {
					FruitApi api = new FruitApi(SplashActivity.this);
					try {
						SendReturnBean bean = api.sendOrder("31.215617", "121.552552", "", "1411224614", "1411228214", "13916965249-140920225009.mp3",
								"≤‚ ‘µÿ÷∑", "2");
						Log.d("AddressSelectActivity", bean.toString());
					} catch (HttpException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();*/
		}
	}
}
