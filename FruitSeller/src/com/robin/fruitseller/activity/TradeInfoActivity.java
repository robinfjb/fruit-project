package com.robin.fruitseller.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.SellerTradeBean;
import com.robin.fruitlib.bean.TradeBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.TimeUtil;
import cn.sgone.fruitseller.R;

public class TradeInfoActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private PullToRefreshListView mList;
	private ArrayList<SellerTradeBean> listData = new ArrayList<SellerTradeBean>();
	private ListAdapter adapter;
	private ImageView noWifi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_info);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		noWifi = (ImageView) findViewById(R.id.no_wifi);
		mList = (PullToRefreshListView) findViewById(R.id.list);
		mList.setMode(Mode.BOTH);
		mList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(TradeInfoActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				GetTradeDataTask task = new GetTradeDataTask(TradeInfoActivity.this);
				task.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(TradeInfoActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				GetTradeDataTask task = new GetTradeDataTask(TradeInfoActivity.this);
				task.execute();
			} 
		});
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
		initData();
	}

	private void initData() {
		GetTradeDataTask task = new GetTradeDataTask(this);
		task.execute();
		/*ArrayList<TradeBean> result = new ArrayList<TradeBean>();
		TradeBean bean = new TradeBean();
		bean.orderTime = "12872872";
		bean.address = "sssssssss";
		bean.acceptTime="2013-12-11";
		bean.stats = "1";
		result.add(bean);
		TradeBean bean2 = new TradeBean();
		bean2.orderTime = "12872872";
		bean2.address = "sssssssss";
		bean2.acceptTime="2013-12-11";
		bean2.stats = "3";
		result.add(bean2);
		listData.clear();
		listData.addAll(result);
		adapter.notifyDataSetChanged();*/
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
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg0 == 1 && arg1 == RESULT_OK) {
			int index = arg2.getIntExtra("index", -1);
			if(index > -1 && index < listData.size() - 1) {
				SellerTradeBean bean = listData.get(index);
				bean.status = "4";
				adapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final SellerTradeBean data = listData.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(TradeInfoActivity.this).inflate(R.layout.list_item_trade_info, null);
				holder.timeView = (TextView) convertView.findViewById(R.id.time);
				holder.statsView = (TextView) convertView.findViewById(R.id.stats);
				holder.dateView = (TextView) convertView.findViewById(R.id.date);
				holder.addressView = (TextView) convertView.findViewById(R.id.address);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.dateView.setText(TimeUtil.timeStamp2Date(data.orderTime, "yyyy-MM-dd"));
			holder.timeView.setText(TimeUtil.timeStamp2DateOnlyHour(data.expressStart) + "-" + TimeUtil.timeStamp2DateOnlyHour(data.expressEnd));
			holder.addressView.setText(data.address);
			if(data.status.equals("1")) {
				holder.statsView.setText(getString(R.string.order_1));
				holder.statsView.setTextColor(Color.parseColor("#ff0000"));
			} else if(data.status.equals("2")){
				holder.statsView.setText(getString(R.string.order_2));
				holder.statsView.setTextColor(Color.parseColor("#ff0000"));
			} else if(data.status.equals("3")){
				holder.statsView.setText(getString(R.string.order_3));
				holder.statsView.setTextColor(Color.parseColor("#333333"));
			} else if(data.status.equals("4")){
				holder.statsView.setText(getString(R.string.order_4));
				holder.statsView.setTextColor(Color.parseColor("#333333"));
			} else if(data.status.equals("5")){
				holder.statsView.setText(getString(R.string.order_5));
				holder.statsView.setTextColor(Color.parseColor("#333333"));
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TradeInfoActivity.this, OrderSuccessActivity.class);
					intent.putExtra("data", data);
					intent.putExtra("index", position);
					startActivityForResult(intent, 1);
				}
			});
			return convertView;
		}
	}
	
	static class ViewHolder {
		TextView timeView;
		TextView statsView;
		TextView dateView;
		TextView addressView;
	}
	
	private class GetTradeDataTask extends RGenericTask<ArrayList<SellerTradeBean>> {

		public GetTradeDataTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ArrayList<SellerTradeBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(TradeInfoActivity.this);
			return api.getShopList();
		}

		@Override
		protected void onSuccess(ArrayList<SellerTradeBean> result) {
			listData.clear();
			listData.addAll(result);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(TradeInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
			noWifi.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onTaskBegin() {
			showProgressBar();
			noWifi.setVisibility(View.GONE);
		}

		@Override
		protected void onTaskFinished() {
			hideProgressBar();
			mList.onRefreshComplete();
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
