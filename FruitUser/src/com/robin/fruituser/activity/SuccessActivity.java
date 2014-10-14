package com.robin.fruituser.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.SendReturnBean;
import com.robin.fruitlib.bean.SendReturnBean.SendReturnSellerBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruituser.R;

import com.robin.fruituser.UserApplication;
import com.robin.fruituser.view.FragmentMapSuccess;

public class SuccessActivity extends BaseActivity implements OnClickListener{
	public static final String ACTION_ACCEPT_ORDER = "cn.sgone.fruituser.accept.order";
	private ImageView backImg;
	private Button btnBuy;
	private FragmentMapSuccess mainContent;
	private TextView munTxt;
	private TextView countTxt;
	private CountDownTimer mCountDownTimer;
	private static final int WAITING_MILLISECONDS = 200000;
	private static final int INTERVAL_MILLISECONDS = 1000;
	private ArrayList<SendReturnSellerBean> list;
	private SendReturnBean bean;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success);
		Intent intent = getIntent();
		bean = (SendReturnBean) intent.getSerializableExtra("data");
		list = bean.list;
		initView();
		initData();
		
		IntentFilter filter = new IntentFilter(ACTION_ACCEPT_ORDER);
		registerReceiver(acceptedOrderReceiver, filter);
		
		mCountDownTimer = new MyCountDownTimer(WAITING_MILLISECONDS, INTERVAL_MILLISECONDS);
		mCountDownTimer.start();
	}
	
	private void initView() {
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		btnBuy = (Button) findViewById(R.id.btn_buy);
		btnBuy.setOnClickListener(this);
		munTxt = (TextView) findViewById(R.id.tips_msg);
		countTxt = (TextView) findViewById(R.id.count_txt);
		mainContent = new FragmentMapSuccess();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainContent).commit();
	}
	
	private void initData() {
		munTxt.setText(String.valueOf(list.size()));
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		mainContent.addMarker(list);
		mainContent.changeCamera(UserApplication.buyLocation.latitude, UserApplication.buyLocation.longitude);
		/*mainContent.addMarkerBuyLocation(UserApplication.buyLocation.latitude, 
				UserApplication.buyLocation.longitude, UserApplication.shop.address);*/
	}

	@Override
	protected void onDestroy() {
		if(mCountDownTimer != null){
			mCountDownTimer.cancel();
		}
		unregisterReceiver(acceptedOrderReceiver);
		acceptedOrderReceiver = null;
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			final Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			SuccessActivity.this.startActivity(intent);
			SuccessActivity.this.finish();
			break;
		case R.id.btn_buy:
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("确定要取消订单？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CancelTask task = new CancelTask(SuccessActivity.this, String.valueOf(bean.oId));
							task.execute();
						}
					});
			builder.setNegativeButton("点错了",null);
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		final Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		SuccessActivity.this.startActivity(intent);
		SuccessActivity.this.finish();
		super.onBackPressed();
	}
	
	private class MyCountDownTimer extends CountDownTimer{
		
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void onTick(long millisUntilFinished) {
			String text = "请等待" + millisUntilFinished/1000 + "秒";
			countTxt.setText(text);
		}

		@Override
		public void onFinish() {
			this.cancel();
			final Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			SuccessActivity.this.startActivity(intent);
			SuccessActivity.this.finish();
		}
	}
	
	private class CancelTask extends RGenericTask<Boolean> {
		private String oId;
		public CancelTask(Context ctx, String oId) {
			super(ctx);
			this.oId = oId;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.cancelOrder(oId);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				Toast.makeText(SuccessActivity.this, "已取消订单", Toast.LENGTH_SHORT).show();
				final Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				SuccessActivity.this.startActivity(intent);
				SuccessActivity.this.finish();
			} else {
				Toast.makeText(SuccessActivity.this, "取消订单失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(SuccessActivity.this, msg, Toast.LENGTH_SHORT).show();
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
	
	private BroadcastReceiver acceptedOrderReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_ACCEPT_ORDER.equals(action)) {
				AlertDialog.Builder builder = new Builder(context);
				builder.setMessage("你的订单已有商家接单了");
				builder.setPositiveButton("详情",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("is_notify",true);
								SuccessActivity.this.startActivity(intent);
								SuccessActivity.this.finish();
							}
						});
				builder.setNegativeButton("确定",null);
				AlertDialog dialog = builder.create();
				dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();
			}
		}
	};
}
