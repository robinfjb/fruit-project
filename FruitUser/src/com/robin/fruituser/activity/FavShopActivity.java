package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sgone.fruituser.R;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.CommonShopBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;

public class FavShopActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
//	private Button confirmBtn;
	private ListView mListNeighbor;
	private NeighborListAdapter adapterNeighbor;
	private List<CommonShopBean> listDataNeighbor = new ArrayList<CommonShopBean>();
	private ListView mListFav;
	private FavListAdapter adapterFav;
	private List<CommonShopBean> listDataFav = new ArrayList<CommonShopBean>();
	private CommonShopBean dataStr;
	private TextView allTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav_shop);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		/*confirmBtn = (Button) findViewById(R.id.confirm);
		confirmBtn.setOnClickListener(this);*/
		allTxt = (TextView) findViewById(R.id.all);
		allTxt.setOnClickListener(this);
		mListNeighbor = (ListView) findViewById(R.id.list_neigbor);
		adapterNeighbor = new NeighborListAdapter();
		mListNeighbor.setAdapter(adapterNeighbor);
		
		mListFav = (ListView) findViewById(R.id.list_fav);
		adapterFav = new FavListAdapter();
		mListFav.setAdapter(adapterFav);
		initData();
	}
	
	private void initData() {
		/*FruitDbHandler handler = FruitDbHandler.instance();
		FruitUserDatabase database = new FruitUserDatabase(this);
		listData.addAll(handler.getAddress(database.getWritableDatabase()));*/
		CommonShopBean beanFav = new CommonShopBean();
		beanFav.shopName = "没有搜藏记录";
		listDataNeighbor.add(beanFav);
		CommonShopBean beanMy = new CommonShopBean();
		beanMy.shopName = "没有购买记录";
		listDataFav.add(beanMy);
		adapterFav.notifyDataSetChanged();
		adapterNeighbor.notifyDataSetChanged();
		GetFavShopTask task = new GetFavShopTask(this);
		task.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		/*case R.id.confirm:
			if(TextUtils.isEmpty(dataStr.shopName)) {
				Toast.makeText(this, "请选择一个常用商店", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("fav_shop", dataStr.shopName);
			intent.putExtra("fav_shop_id", dataStr.sId);
			setResult(RESULT_OK, intent);
			finish();
			break;*/
		case R.id.all:
			dataStr = new CommonShopBean();
			dataStr.shopName = "全部";
			Intent intentAll = new Intent();
			intentAll.putExtra("fav_shop", dataStr.shopName);
			intentAll.putExtra("fav_shop_id", "");
			setResult(RESULT_OK, intentAll);
			finish();
			break;
		}	
	}

	private class NeighborListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return listDataNeighbor.size();
		}

		@Override
		public Object getItem(int position) {
			return listDataNeighbor.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View cView = LayoutInflater.from(FavShopActivity.this).inflate(R.layout.list_item_string, null);
			final CommonShopBean data = listDataNeighbor.get(position);
			TextView textKey = (TextView) cView.findViewById(R.id.text);
			textKey.setText(data.shopName);
			cView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cView.setBackgroundResource(R.color.gray_edit);
					dataStr = data;
					Intent intentAll = new Intent();
					intentAll.putExtra("fav_shop", dataStr.shopName);
					intentAll.putExtra("fav_shop_id", dataStr.sTel);
					setResult(RESULT_OK, intentAll);
					finish();
				}
			});
			return cView;
		}
	}
	
	private class FavListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return listDataFav.size();
		}
		@Override
		public Object getItem(int position) {
			return listDataFav.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View cView = LayoutInflater.from(FavShopActivity.this).inflate(R.layout.list_item_string, null);
			final CommonShopBean data = listDataFav.get(position);
			TextView textKey = (TextView) cView.findViewById(R.id.text);
			textKey.setText(data.shopName);
			cView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cView.setBackgroundResource(R.color.gray_edit);
					dataStr = data;
					Intent intentAll = new Intent();
					intentAll.putExtra("fav_shop", dataStr.shopName);
					intentAll.putExtra("fav_shop_id", dataStr.sTel);
					setResult(RESULT_OK, intentAll);
					finish();
				}
			});
			return cView;
		}
	}
	
	private class GetFavShopTask extends RGenericTask<Pair<List<CommonShopBean>, List<CommonShopBean>>> {

		public GetFavShopTask(Context ctx) {
			super(ctx);
		}

		@Override
		protected Pair<List<CommonShopBean>, List<CommonShopBean>> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getFavShop();
		}

		@Override
		protected void onSuccess(Pair<List<CommonShopBean>, List<CommonShopBean>> result) {
			listDataNeighbor.clear();
			listDataFav.clear();
			if(result.first.size() > 0) {
				listDataFav.addAll(result.first);
			} else {
				CommonShopBean beanMy = new CommonShopBean();
				beanMy.shopName = "没有购买记录";
				listDataFav.add(beanMy);
			}
			if(result.second.size() > 0) {
				listDataNeighbor.addAll(result.second);
			} else {
				CommonShopBean beanFav = new CommonShopBean();
				beanFav.shopName = "没有收藏记录";
				listDataNeighbor.add(beanFav);
			}
			adapterFav.notifyDataSetChanged();
			adapterNeighbor.notifyDataSetChanged();
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
}
