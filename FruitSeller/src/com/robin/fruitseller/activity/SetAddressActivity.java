package com.robin.fruitseller.activity;

import java.util.ArrayList;

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

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.GeoUtils;
import cn.sgone.fruitseller.R;

public class SetAddressActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private TextView saveTxt;
	private EditText editTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_address);
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
				Toast.makeText(SetAddressActivity.this, getString(R.string.warn_empty_address), Toast.LENGTH_SHORT).show();
				return;
			}
			GeoUtils utilsHome = new GeoUtils(new GeocodeSearch(text));
			utilsHome.getLatlon(SetAddressActivity.this, text);
			break;
		}
	}
	
	private class GeocodeSearch implements OnGeocodeSearchListener {
		private String addressStr;
		public GeocodeSearch(String address) {
			this.addressStr = address;
		}
		@Override
		public void onGeocodeSearched(GeocodeResult result, int rCode) {
			hideProgressBar();
			if (rCode == 0) {
				if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
					GeocodeAddress address = result.getGeocodeAddressList().get(0);
					LatLonPoint point = address.getLatLonPoint();
					double lon = point.getLongitude();
					double lat = point.getLatitude();
					RenewAddressTask task = new RenewAddressTask(SetAddressActivity.this, addressStr, lat, lon);
					task.execute();
				} else {
					Toast.makeText(SetAddressActivity.this, getString(R.string.error_latlon), Toast.LENGTH_LONG).show();
				}
			} else if (rCode == 27) {
				Toast.makeText(SetAddressActivity.this, getString(R.string.error_27), Toast.LENGTH_LONG).show();
			} else if (rCode == 32) {
				Toast.makeText(SetAddressActivity.this, getString(R.string.error_32), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(SetAddressActivity.this, getString(R.string.error_unknow), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		}
	}
	
	private class RenewAddressTask extends RGenericTask<Boolean> {
		private String name;
		private String latStr;
		private String lonStr;
		public RenewAddressTask(Context ctx, String name, double lat, double lon) {
			super(ctx);
			this.name = name;
			latStr = String.valueOf(lat);
			lonStr = String.valueOf(lon);
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			ArrayList<String> keys = new ArrayList<String>();
			keys.add("address");
			keys.add("addX");
			keys.add("addY");
			ArrayList<String> values = new ArrayList<String>();
			values.add(name);
			values.add(latStr);
			values.add(lonStr);
			return api.renewShopInfo(keys, values);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				FruitPerference.saveShopAddress(SetAddressActivity.this, name);
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
