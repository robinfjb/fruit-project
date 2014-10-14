package com.robin.fruituser.activity;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.TradeBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.TimeUtil;
import cn.sgone.fruituser.R;

public class TradeActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private PullToRefreshListView mList;
	private List<TradeBean> listData = new ArrayList<TradeBean>();
	private ListAdapter adapter;
	private ImageView noWifi;
	private String oid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		noWifi = (ImageView) findViewById(R.id.no_wifi);
		mList = (PullToRefreshListView) findViewById(R.id.list);
		mList.setMode(Mode.BOTH);
		mList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(TradeActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				GetTradeDataTask task = new GetTradeDataTask(TradeActivity.this, oid);
				task.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(TradeActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				GetTradeDataTask task = new GetTradeDataTask(TradeActivity.this, oid);
				task.execute();
			} 
		});
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
		initData();
	}
	
	private void initData() {
		GetTradeDataTask task = new GetTradeDataTask(this, oid);
		task.execute();
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
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
		if(arg1 == RESULT_OK) {
			int index = arg2.getIntExtra("index", -1);
			String type = arg2.getStringExtra("type");
			if(index > -1 && index < listData.size() - 1) {
				TradeBean bean = listData.get(index);
				if(type.equals("confirm")) {
					bean.stats = "3";
				} else if(type.equals("refuse")) {
					bean.stats = "5";
				}
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
			final TradeBean data = listData.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(TradeActivity.this).inflate(R.layout.list_item_trade, null);
				holder.timeView = (TextView) convertView.findViewById(R.id.time);
				holder.statsView = (TextView) convertView.findViewById(R.id.stats);
				holder.nameView = (TextView) convertView.findViewById(R.id.shop);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.timeView.setText(TimeUtil.timeStamp2Date(data.orderTime, "yyyy-MM-dd"));
			if(data.stats.equals("0")) {
				holder.statsView.setText("订单未完成");
				holder.statsView.setTextColor(Color.parseColor("#ff0000"));
			} else if(data.stats.equals("1")) {
				holder.statsView.setText("订单未完成");
				holder.statsView.setTextColor(Color.parseColor("#ff0000"));
			} else if(data.stats.equals("2")){
				holder.statsView.setText("商家已接单");
				holder.statsView.setTextColor(Color.parseColor("#00ff00"));
			} else if(data.stats.equals("3")){
				holder.statsView.setText("订单已完成");
				holder.statsView.setTextColor(Color.parseColor("#00ff00"));
			} else if(data.stats.equals("4")){
				holder.statsView.setText("订单已取消");
				holder.statsView.setTextColor(Color.parseColor("#333333"));
			} else if(data.stats.equals("5")){
				holder.statsView.setText("订单已取消");
				holder.statsView.setTextColor(Color.parseColor("#333333"));
			} else if(data.stats.equals("6")){
				holder.statsView.setText("订单已取消");
				holder.statsView.setTextColor(Color.parseColor("#333333"));
			}
			holder.nameView.setText(data.shopName);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TradeActivity.this, OrderSuccessActivity.class);
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
		TextView nameView;
	}
	
	private class GetTradeDataTask extends RGenericTask<ArrayList<TradeBean>> {
		private String oidd;

		public GetTradeDataTask(Context ctx, String oid) {
			super(ctx);
			this.oidd = oid;
		}

		@Override
		protected ArrayList<TradeBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(TradeActivity.this);
			return api.getTradeListData(oidd);
		}

		@Override
		protected void onSuccess(ArrayList<TradeBean> result) {
			listData.addAll(result);
			oid = result.get(result.size() - 1).oId;
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(TradeActivity.this, msg, Toast.LENGTH_SHORT).show();
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
}