package com.robin.fruitseller.activity;

import android.app.Activity;
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
import cn.sgone.fruitseller.R;

public class SetNumberActivity  extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private TextView saveTxt;
	private EditText editTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_phone);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		saveTxt = (TextView) findViewById(R.id.ivTitleRight);
		saveTxt.setOnClickListener(this);
		editTxt = (EditText) findViewById(R.id.name);
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
			String text = editTxt.getText().toString().trim();
			if(TextUtils.isEmpty(text)) {
				Toast.makeText(SetNumberActivity.this, getString(R.string.warn_empty_number), Toast.LENGTH_SHORT).show();
				return;
			}
			FruitPerference.saveShopPhone(SetNumberActivity.this, text);
			Intent intent = new Intent();
			intent.putExtra("result", text);
			setResult(Activity.RESULT_OK, intent);
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
