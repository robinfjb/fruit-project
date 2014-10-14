package com.robin.fruituser.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sgone.fruituser.R;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.util.GeoUtils;

public class FavAddressActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private String type;
	private EditText edit;
	private ProgressDialog progDialog;
	private TextView confirm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(R.layout.activity_fav_address);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		edit = (EditText) findViewById(R.id.fav_address);
		confirm = (TextView) findViewById(R.id.confirm);
		confirm.setOnClickListener(this);
		
		progDialog = new ProgressDialog(this);
	}
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.confirm:
			final String text = edit.getText().toString().trim();
			if(TextUtils.isEmpty(text)) {
				Toast.makeText(FavAddressActivity.this, text, Toast.LENGTH_SHORT).show();
				return;
			}
			showDialog();
			GeoUtils utils = new GeoUtils(new OnGeocodeSearchListener() {
				
				@Override
				public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGeocodeSearched(GeocodeResult result, int rCode) {
					dismissDialog();
					if (rCode == 0) {
						if (result != null && result.getGeocodeAddressList() != null 
								&& result.getGeocodeAddressList().size() > 0) {
							GeocodeAddress address = result.getGeocodeAddressList().get(0);
							LatLonPoint point = address.getLatLonPoint();
							double lon = point.getLongitude();
							double lat = point.getLatitude();
							if(type.equals("home")) {
								FruitPerference.saveHomeAddress(FavAddressActivity.this, text, lat, lon);
							} else if(type.equals("company")) {
								FruitPerference.saveCompanyAddress(FavAddressActivity.this, text, lat, lon);
							} else if(type.equals("fav")) {
								FruitPerference.saveFavAddress(FavAddressActivity.this, text, lat, lon);
							}
							Intent intent = new Intent();
							intent.putExtra("type", type);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							Toast.makeText(FavAddressActivity.this, "此位置找不到经纬度，请换个知名度高的地址", Toast.LENGTH_LONG).show();
						}

					} else if (rCode == 27) {
						Toast.makeText(FavAddressActivity.this, "网络链接错误", Toast.LENGTH_LONG).show();
					} else if (rCode == 32) {
						Toast.makeText(FavAddressActivity.this, "key错误", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(FavAddressActivity.this, "未知错误", Toast.LENGTH_LONG).show();
					}
				}
			});
			utils.getLatlon(FavAddressActivity.this, text);
//			finish();
			break;
		default:
			break;
		}
	
	}
	
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
	}
	
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
}
