package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.robin.fruitlib.database.FruitDbHandler;
import com.robin.fruitlib.database.FruitUserDatabase;
import com.robin.fruitlib.util.GeoUtils;

public class AddressSelectActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private Button confirmBtn;
	private EditText editText;
	private ListView mList;
	private ListAdapter adapter;
	private List<String> listData = new ArrayList<String>();
	private static final String NO_HOME = "没有设置家地址";
	private static final String NO_COMPANY = "没有设置公司地址";
	private TextView homeEdit;
	private TextView companyEdit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_select);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		editText = (EditText) findViewById(R.id.address_input);
		confirmBtn = (Button) findViewById(R.id.confirm);
		confirmBtn.setOnClickListener(this);
		mList = (ListView) findViewById(R.id.list);
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
		homeEdit = (TextView) findViewById(R.id.home_address);
		companyEdit = (TextView) findViewById(R.id.company_address);
		homeEdit.setOnClickListener(this);
		companyEdit.setOnClickListener(this);
		progDialog = new ProgressDialog(this);
		
	}
	
	

	@Override
	public void onResume() {
		initData();
		super.onResume();
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initData() {
		FruitDbHandler handler = FruitDbHandler.instance();
		FruitUserDatabase database = new FruitUserDatabase(this);
		ArrayList<String> listD = handler.getAddress(database.getWritableDatabase());
		listData.addAll(listD);
		
		String homeS = FruitPerference.getHomeAddress(this);
		if(!TextUtils.isEmpty(homeS)) {
			homeEdit.setText(homeS);
			homeEdit.setTextColor(getResources().getColor(R.color.gray));
		} else {
			homeEdit.setText(NO_HOME);
			homeEdit.setTextColor(getResources().getColor(R.color.gray_hint));
		}
		
		String companyS = FruitPerference.getCompanyAddress(this);
		if(!TextUtils.isEmpty(companyS)) {
			companyEdit.setText(companyS);
			companyEdit.setTextColor(getResources().getColor(R.color.gray));
		} else {
			companyEdit.setText(NO_COMPANY);
			companyEdit.setTextColor(getResources().getColor(R.color.gray_hint));
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.confirm:
			confirmAddress();
			break;
		case R.id.home_address:
			String home = homeEdit.getText().toString();
			if(home.equals(NO_HOME)) {
				Intent favIntentHome = new Intent(AddressSelectActivity.this, FavAddressActivity.class);
				favIntentHome.putExtra("type", "home");
				startActivityForResult(favIntentHome, RESULT_CODE_FAV);
			} else {
				editText.setText(home);
			}
			break;
		case R.id.company_address:
			String company = companyEdit.getText().toString();
			 if(company.equals(NO_COMPANY)) {
				Intent favIntentCompany = new Intent(AddressSelectActivity.this, FavAddressActivity.class);
				favIntentCompany.putExtra("type", "company");
				startActivityForResult(favIntentCompany, RESULT_CODE_FAV);
			} else {
				editText.setText(company);
			}
			break;
		}	
	}
	
	private void confirmAddress() {
		final String text = editText.getText().toString().trim();
		if(TextUtils.isEmpty(text)) {
			Toast.makeText(this, "请输入送货地址", Toast.LENGTH_SHORT).show();
			return;
		}
		showDialog();
		GeoUtils utils = new GeoUtils(new OnGeocodeSearchListener() {
			@Override
			public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
			}
			
			@Override
			public void onGeocodeSearched(GeocodeResult result, int rCode) {
				dismissDialog();
				if (rCode == 0) {
					if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
						GeocodeAddress address = result.getGeocodeAddressList().get(0);
						LatLonPoint point = address.getLatLonPoint();
						double lon = point.getLongitude();
						double lat = point.getLatitude();
						if(!homeEdit.getText().toString().equals(text) 
								&& !companyEdit.getText().toString().equals(text)) {
							FruitDbHandler handler = FruitDbHandler.instance();
							FruitUserDatabase database = new FruitUserDatabase(AddressSelectActivity.this);
							handler.saveAddress(AddressSelectActivity.this, database.getWritableDatabase(), text);
						}
						Log.d("AddressSelectActivity", "lat:" + lat + "|" + "lon:" + lon);
						Intent intent = new Intent();
						intent.putExtra("address", text);
						intent.putExtra("lat", lat);
						intent.putExtra("lon", lon);
						setResult(Activity.RESULT_OK, intent);
						finish();
					} else {
						Toast.makeText(AddressSelectActivity.this, "此位置找不到经纬度，请换个知名度高的地址", Toast.LENGTH_LONG).show();
					}
				} else if (rCode == 27) {
					Toast.makeText(AddressSelectActivity.this, "网络链接错误", Toast.LENGTH_LONG).show();
				} else if (rCode == 32) {
					Toast.makeText(AddressSelectActivity.this, "key错误", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(AddressSelectActivity.this, "未知错误", Toast.LENGTH_LONG).show();
				}
			}
		});
		utils.getLatlon(AddressSelectActivity.this, text);
	}
	
	private static final int RESULT_CODE_FAV = 0x02;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE_FAV:
			if(resultCode == RESULT_OK) {
				String type = data.getStringExtra("type");
				if(type.equals("home")) {
					String addressHome = FruitPerference.getHomeAddress(AddressSelectActivity.this);
					homeEdit.setText(addressHome);
				} else if(type.equals("company")) {
					String companyHome = FruitPerference.getCompanyAddress(AddressSelectActivity.this);
					companyEdit.setText(companyHome);
				}
			}
			break;
		}
	}
	
	private class ListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return listData.size();
		}
		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(AddressSelectActivity.this).inflate(R.layout.list_item_string, null);
			final String data = listData.get(position);
			TextView textKey = (TextView) convertView.findViewById(R.id.text);
			textKey.setText(data);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editText.setText(data);
				}
			});
			return convertView;
		}
	}
	
	private ProgressDialog progDialog;
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
