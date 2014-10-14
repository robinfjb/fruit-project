package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.robin.fruitlib.bean.CommonShopBean;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.GeoUtils;
import com.robin.fruituser.view.SlideView;
import com.robin.fruituser.view.SlideView.OnSlideListener;

public class AccountActivity extends BaseActivity implements OnClickListener, OnSlideListener{
	private ImageView backImg;
	private ListView shopListView;
	private ListAdpter shopAdapter;
	private List<MessageItem> shopListData = new ArrayList<MessageItem>();
	private SlideView mLastSlideViewWithStatusOn;
	private TextView mobileTxt;
	private EditText homeEdit;
	private EditText companyEdit;
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		shopListView = (ListView) findViewById(R.id.list_shop);
		shopAdapter = new ListAdpter(shopListData, shopListener);
		shopListView.setAdapter(shopAdapter);
		mobileTxt = (TextView) findViewById(R.id.mobile);
		homeEdit = (EditText) findViewById(R.id.home_address);
		companyEdit = (EditText) findViewById(R.id.company_address);
		homeEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(final Editable s) {
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
						String ss = s.toString();
						if(TextUtils.isEmpty(ss)) {
							return;
						}
						GeoUtils utilsHome = new GeoUtils(new GeocodeSearch(ss, "home"));
						utilsHome.getLatlon(AccountActivity.this, ss);
//					}
//				}, 1000);
				
			}
		});
		companyEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(final Editable s) {
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
						String ss = s.toString();
						if(TextUtils.isEmpty(ss)) {
							return;
						}
						GeoUtils utilsHome = new GeoUtils(new GeocodeSearch(ss, "company"));
						utilsHome.getLatlon(AccountActivity.this, ss);
//					}
//				}, 1000);
			}
		});
		progDialog = new ProgressDialog(this);
		initData();
	}
	
	private class GeocodeSearch implements OnGeocodeSearchListener {
		private String addressStr;
		private String type;
		public GeocodeSearch(String address, String type) {
			this.addressStr = address;
			this.type = type;
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
					if(type.equals("home")) {
						FruitPerference.saveHomeAddress(AccountActivity.this, addressStr, lat, lon);
					} else if(type.equals("company")) {
						FruitPerference.saveCompanyAddress(AccountActivity.this, addressStr, lat, lon);
					}
				} else {
					Toast.makeText(AccountActivity.this, "此位置找不到经纬度，请换个知名度高的地址", Toast.LENGTH_LONG).show();
				}
			} else if (rCode == 27) {
				Toast.makeText(AccountActivity.this, "网络链接错误", Toast.LENGTH_LONG).show();
			} else if (rCode == 32) {
				Toast.makeText(AccountActivity.this, "key错误", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(AccountActivity.this, "未知错误", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		}
	}
	
	
	
	/*private DeleteListenr addListener  = new DeleteListenr() {
		@Override
		public void onDelete(int index) {
			MessageItem item = addListData.get(index);
			if(item.tag == 1) {
				FruitPerference.removeHomeAddress(AccountActivity.this);
			}else if(item.tag == 2) {
				FruitPerference.removeCompanyAddress(AccountActivity.this);
			}else if(item.tag == 3) {
				FruitPerference.removeFavAddress(AccountActivity.this);
			}
			addListData.remove(index);
			if(addListData.isEmpty()) {
				nodataAdd.setVisibility(View.VISIBLE);
			}
			addAdapter.notifyDataSetChanged();
		}
	};*/
	
	private DeleteListenr shopListener  = new DeleteListenr() {
		@Override
		public void onDelete(int index) {
			/*FruitDbHandler handler = FruitDbHandler.instance();
			FruitUserDatabase database = new FruitUserDatabase(AccountActivity.this);
			handler.removeFavShopItem(database.getWritableDatabase(), shopListData.get(index).text);*/
			deletefav(shopListData.get(index).sTel, index);
		}
	};
	
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
	
	private void initData() {
		mobileTxt.setText(FruitPerference.getUserMobile(this));
		
		String homeS = FruitPerference.getHomeAddress(this);
		if(!TextUtils.isEmpty(homeS)) {
			homeEdit.setText(homeS);
		}
		String companyS = FruitPerference.getCompanyAddress(this);
		if(!TextUtils.isEmpty(companyS)) {
			companyEdit.setText(companyS);
		}
		
		/*FruitDbHandler handler = FruitDbHandler.instance();
		FruitUserDatabase database = new FruitUserDatabase(this);
		shopListData.addAll(handler.getAddress(database.getWritableDatabase()));*/
		MessageItem itemFav = new MessageItem();
		itemFav.text = "您没常用水果店";
		shopListData.add(itemFav);
		// 从服务端获取
		GetFavShopTask task = new GetFavShopTask(this);
		task.execute();
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
	
	private class GetFavShopTask extends RGenericTask<List<CommonShopBean>> {

		public GetFavShopTask(Context ctx) {
			super(ctx);
		}

		@Override
		protected List<CommonShopBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getAllShop();
		}

		@Override
		protected void onSuccess(List<CommonShopBean> result) {
			shopListData.clear();
			for (CommonShopBean shop: result) {
				MessageItem itemFav = new MessageItem();
				itemFav.text = shop.shopName;
				itemFav.sTel = shop.sTel;
				shopListData.add(itemFav);
			}
			shopAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onAnyError(int code, String msg) {
			shopListData.clear();
			MessageItem itemFav = new MessageItem();
			itemFav.text = "您没常用水果店";
			shopListData.add(itemFav);
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			shopAdapter.notifyDataSetChanged();
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
	
	private void deletefav(String sTel, int position) {
		UpdateTask task = new UpdateTask(this, sTel, position);
		task.execute();
	}
	
	private class UpdateTask extends RGenericTask<Boolean> {
		private String sTel;
		private int position;
		public UpdateTask(Context ctx,String sTel, int position) {
			super(ctx);
			this.sTel = sTel;
			this.position = position;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.updateFavShop(2, sTel);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				Toast.makeText(ctx, "删除成功", Toast.LENGTH_SHORT).show();
				shopListData.remove(position);
				if(shopListData.isEmpty()) {
					MessageItem itemFav = new MessageItem();
					itemFav.text = "您没常用水果店";
					shopListData.add(itemFav);
				}
				shopAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(ctx, "删除失败", Toast.LENGTH_SHORT).show();
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
	
	interface DeleteListenr {
		public void onDelete(int index);
	}
	
	private class ListAdpter extends BaseAdapter {
		private List<MessageItem> mlist;
//		private Animation animation;  //删除时候的动画
		private DeleteListenr listener;
		private LayoutInflater mInflater;
		private ViewHolder holder;
		
		public ListAdpter(List<MessageItem> data, DeleteListenr listener) {
			mlist = data;
			this.listener = listener;
			mInflater = getLayoutInflater();
		}
		@Override
		public int getCount() {
			return mlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		public void clear() {
			mlist.clear();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_list_left_edit, null);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView.findViewById(R.id.txt);
				holder.deleteImg = (ImageView) convertView.findViewById(R.id.deleteImg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MessageItem item = mlist.get(position);
//			if(item.showDelete) {
//				holder.deleteImg.setVisibility(View.VISIBLE);
//			} else {
				holder.deleteImg.setVisibility(View.GONE);
//			}
			holder.textView.setText(item.text);
			
			holder.deleteImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteItem(position);
				}
			});
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < mlist.size(); i++) {
						mlist.get(i).showDelete = !mlist.get(i).showDelete;
					}
					shopAdapter.notifyDataSetChanged();
				}
			});
			/*convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder builder = new Builder(
							AccountActivity.this);
					builder.setMessage("确定删除？");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which) {
									deleteItem(position);
								}
							});
					builder.setNegativeButton("取消", null);
					builder.create().show();
					return true;
				}
			});*/
			return convertView;
		}
		
		public void deleteItem(final int position) {
			listener.onDelete(position);
		}
	}
	
	private static class ViewHolder {
		public TextView titleView;
		public TextView textView;
		public ImageView deleteImg;
	}
	
	public class MessageItem {
		public String text;
		public String sTel;
		public int tag;
		public boolean showDelete;
	}

	@Override
	public void onSlide(View view, int status) {
		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}
		
		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;
		}
	}
}
