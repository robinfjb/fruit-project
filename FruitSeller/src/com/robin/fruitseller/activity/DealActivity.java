package com.robin.fruitseller.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.SellerTradeBean;
import com.robin.fruitlib.util.AudioRecorderHandler;
import com.robin.fruitlib.util.TimeUtil;
import cn.sgone.fruitseller.R;

public class DealActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private ImageView soundImg;
	private AudioRecorderHandler recordHandler;
	private SellerTradeBean data;
	private TextView timeTxt;
	private TextView addressTxt;
	private TextView phoneTxt;
	private RelativeLayout callArea;
	private TextView soundLengthTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		soundImg = (ImageView) findViewById(R.id.soundImg);
		soundImg.setOnClickListener(this);
		soundLengthTxt = (TextView) findViewById(R.id.voice_length);
		timeTxt = (TextView) findViewById(R.id.time);
		addressTxt = (TextView) findViewById(R.id.address);
		phoneTxt = (TextView) findViewById(R.id.phone);
		callArea = (RelativeLayout) findViewById(R.id.call_area);
		callArea.setOnClickListener(this);
		recordHandler = AudioRecorderHandler.getInstance(this);
		recordHandler.playFromRaw(R.raw.sound2);
		data = (SellerTradeBean) getIntent().getSerializableExtra("data");
		initData();
	}

	private void initData() {
		if(data != null) {
			timeTxt.setText(TimeUtil.timeStamp2DateOnlyHour(data.expressStart) 
					+ "-" + TimeUtil.timeStamp2DateOnlyHour(data.expressEnd));
			addressTxt.setText(data.address);
			phoneTxt.setText(data.cTel);
			soundLengthTxt.setText(data.audioLength + "\"");
		}
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
		case R.id.soundImg:
			recordHandler.playAsync(data.audioUrl + "/" + data.audioName);
			break;
		case R.id.call_area:
			String phone = phoneTxt.getText().toString();
			if(TextUtils.isEmpty(phone)) {
				Toast.makeText(DealActivity.this, getString(R.string.error_empty_phone), Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
			DealActivity.this.startActivity(intent);
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
