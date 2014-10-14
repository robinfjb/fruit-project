package com.robin.fruituser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import cn.sgone.fruituser.R;

public class MonthRecommendDetailActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private TextView contentView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_month_reommend_detail);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String imgUrl = intent.getStringExtra("img");
		String content = intent.getStringExtra("content");
		((TextView) findViewById(R.id.title)).setText(title);
		((TextView) findViewById(R.id.ivTitleName)).setText(title);
		ImageView imgView = (ImageView) findViewById(R.id.imgView);
		contentView = (TextView) findViewById(R.id.content);
		contentView.setText(content);
		FruitBitmapHandler handler = new FruitBitmapHandler(this);
		handler.displayImage(imgView, imgUrl);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			setResult(RESULT_CANCELED);
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
}
