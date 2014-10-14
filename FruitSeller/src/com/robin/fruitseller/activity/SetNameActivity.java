package com.robin.fruitseller.activity;

import java.util.ArrayList;

import u.aly.A;
import android.app.Activity;
import android.content.Context;
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
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruitseller.R;

public class SetNameActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private TextView saveTxt;
	private EditText editTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_name);
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
				Toast.makeText(SetNameActivity.this, getString(R.string.warn_empty_name), Toast.LENGTH_SHORT).show();
				return;
			}
			RenewNameTask task = new RenewNameTask(this, text);
			task.execute();
			break;
		}
	}
	
	private class RenewNameTask extends RGenericTask<Boolean> {
		private String name;
		public RenewNameTask(Context ctx, String name) {
			super(ctx);
			this.name = name;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			ArrayList<String> keys = new ArrayList<String>();
			keys.add("shopName");
			ArrayList<String> values = new ArrayList<String>();
			values.add(name);
			return api.renewShopInfo(keys, values);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				FruitPerference.saveShopName(SetNameActivity.this, name);
				Intent intent = new Intent();
				intent.putExtra("result", name);
				setResult(Activity.RESULT_OK, intent);
				finish();
				overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}
		@Override
		protected void onTaskBegin() {
			hideProgressBar();
		}
		@Override
		protected void onTaskFinished() {
			showProgressBar();
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
