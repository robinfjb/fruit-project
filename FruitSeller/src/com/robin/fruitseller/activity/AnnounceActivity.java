package com.robin.fruitseller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.robin.fruitlib.base.BaseActivity;
import cn.sgone.fruitseller.R;

public class AnnounceActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private WebView mWebView;
	private String url = "http://www.sgone.cn/mshopnews.html";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accounce);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.loadUrl(url);
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
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
