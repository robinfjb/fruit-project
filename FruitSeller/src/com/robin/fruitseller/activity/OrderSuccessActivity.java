package com.robin.fruitseller.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.OrderDetailBean;
import com.robin.fruitlib.bean.SellerTradeBean;
import com.robin.fruitlib.bean.TradeBean;
import com.robin.fruitlib.bean.TradeDetailBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.AudioRecorderHandler;
import com.robin.fruitlib.util.TimeUtil;
import cn.sgone.fruitseller.R;

public class OrderSuccessActivity  extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private ImageView voicePlay;
	private TextView cancelTxt;
	private Button confirmBtn;
	private int index;
	SellerTradeBean data;
//	TradeDetailBean bean;
	private TextView timeTxt;
	private TextView addressTxt;
	private TextView phoneTxt;
	private ImageView phoneImg;
	private TextView audioLengthTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_success);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		voicePlay = (ImageView) findViewById(R.id.btn_voice_play);
		voicePlay.setOnClickListener(this);
		audioLengthTxt = (TextView) findViewById(R.id.voice_length);
		confirmBtn = (Button) findViewById(R.id.confirm);
		confirmBtn.setOnClickListener(this);
		cancelTxt = (TextView) findViewById(R.id.ivTitleRight);
		cancelTxt.setOnClickListener(this);
		timeTxt = (TextView) findViewById(R.id.time);
		addressTxt = (TextView) findViewById(R.id.address);
		phoneTxt = (TextView) findViewById(R.id.phone);
		phoneImg = (ImageView) findViewById(R.id.call_img);
		phoneImg.setOnClickListener(this);
		
		Intent intent = getIntent();
		data = (SellerTradeBean) intent.getSerializableExtra("data");
		index = intent.getIntExtra("index", -1);
		/*if(data != null && (data.status.equals("1") || data.status.equals("2"))) {
			confirmBtn.setVisibility(View.VISIBLE);
		} else {*/
			confirmBtn.setVisibility(View.GONE);
//		}
		if(data != null && data.status.equals("2")) {
			cancelTxt.setVisibility(View.VISIBLE);
		} else {
			cancelTxt.setVisibility(View.GONE);
		}
		initData();
	}
	
	private void initData() {
		/*GetOrderTask task = new GetOrderTask(OrderSuccessActivity.this, data.oId);
		task.execute();*/
		timeTxt.setText(TimeUtil.timeStamp2DateOnlyHour(data.expressStart) + "-" + TimeUtil.timeStamp2DateOnlyHour(data.expressEnd));
		addressTxt.setText(data.address);
		phoneTxt.setText(data.cTel);
		audioLengthTxt.setText(data.audioLength + "\"");
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
		case R.id.btn_voice_play:
			playSound(data.audioUrl + "/" + data.audioName);
			break;
		case R.id.confirm:
			/*SendOrderTask task = new SendOrderTask(OrderSuccessActivity.this, data.oId);
			task.execute();*/
			break;
		case R.id.call_img:
			String phone = phoneTxt.getText().toString();
			if(TextUtils.isEmpty(phone)) {
				Toast.makeText(OrderSuccessActivity.this, getString(R.string.error_empty_phone), Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
			OrderSuccessActivity.this.startActivity(intent);
			break;
		case R.id.ivTitleRight:
			CancelOrderTask cancelTask = new CancelOrderTask(OrderSuccessActivity.this, data.oId);
			cancelTask.execute();
			break;
		}	
	}
	
	private void playSound(String url) {
		AudioRecorderHandler handler = AudioRecorderHandler.getInstance(OrderSuccessActivity.this);
		handler.playAsync(url);
	}
	
	/*private class SendOrderTask extends RGenericTask<Boolean> {
		private String oId;
		public SendOrderTask(Context ctx, String oId) {
			super(ctx);
			this.oId = oId;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.confirmOrder(oId);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				Intent intent = new Intent();
				intent.putExtra("index", index);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(OrderSuccessActivity.this, "ȷ�϶���ʧ��", Toast.LENGTH_SHORT).show();
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
	}*/
	
	/*private class GetOrderTask extends RGenericTask<TradeDetailBean> {
		private String id;
		public GetOrderTask(Context ctx, String id) {
			super(ctx);
			this.id = id;
		}

		@Override
		protected TradeDetailBean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getTradeDetail(id);
		}

		@Override
		protected void onSuccess(TradeDetailBean result) {
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
	}*/
	
	private class CancelOrderTask extends RGenericTask<Boolean> {
		private String oid;

		public CancelOrderTask(Context ctx, String oid) {
			super(ctx);
			this.oid = oid;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.sellerCancelOrder(oid);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				Intent intent = new Intent();
				intent.putExtra("index", index);
				setResult(RESULT_OK, intent);
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
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
