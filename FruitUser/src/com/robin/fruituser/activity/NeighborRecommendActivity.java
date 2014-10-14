package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import u.aly.ad;

import android.app.Activity;
import android.content.Context;
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
import com.robin.fruitlib.bean.NeighborBean;
import com.robin.fruitlib.database.FruitDbHandler;
import com.robin.fruitlib.database.FruitUserDatabase;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruituser.R;
import com.robin.fruituser.UserApplication;

public class NeighborRecommendActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private PullToRefreshListView mList;
	private List<NeighborBean> listData = new ArrayList<NeighborBean>();
	private List<String> saveData = new ArrayList<String>();
	private ListAdapter adapter;
	private ImageView noWifi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neighbour_recommend);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		noWifi = (ImageView) findViewById(R.id.no_wifi);
		mList = (PullToRefreshListView) findViewById(R.id.list);
		mList.setMode(Mode.BOTH);
		mList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(NeighborRecommendActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				GetNeiborDataTask task = new GetNeiborDataTask(NeighborRecommendActivity.this);
				task.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(NeighborRecommendActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				GetNeiborDataTask task = new GetNeiborDataTask(NeighborRecommendActivity.this);
				task.execute();
			} 
		});
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
		initData();
	}
	
	private void initData() {
		FruitDbHandler handler = FruitDbHandler.instance();
		FruitUserDatabase database = new FruitUserDatabase(NeighborRecommendActivity.this);
		saveData.addAll(handler.getFavShop(database.getWritableDatabase()));
		GetNeiborDataTask task = new GetNeiborDataTask(this);
		task.execute();
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
	
	private class ListAdapter extends BaseAdapter {
		ViewHolder holder;
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
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			final NeighborBean data = listData.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(NeighborRecommendActivity.this).inflate(R.layout.list_item_neighbor_rec, null);
				holder.titleView = (TextView) convertView.findViewById(R.id.title);
				holder.addressView = (TextView) convertView.findViewById(R.id.address);
				holder.buyView = (TextView) convertView.findViewById(R.id.buy_num);
				holder.rejectView = (TextView) convertView.findViewById(R.id.reject_num);
				holder.heartView = (ImageView) convertView.findViewById(R.id.heart);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.titleView.setText(data.shopName);
			holder.addressView.setText(data.address);
			holder.buyView.setText("购买" + data.orderNum);
			holder.rejectView.setText("拒收" + data.cancelNum);
			if(saveData.contains(data.sId)) {
				holder.heartView.setImageResource(R.drawable.redheart);
			} else {
				holder.heartView.setImageResource(R.drawable.heart_s1);
			}
			holder.heartView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(saveData.contains(data.sId)) {
						deletefav(data.sTel, position);
					} else {
						dofav(data.sTel, position);
					}	
				}
			});
			return convertView;
		}
	}
	
	static class ViewHolder {
		TextView titleView;
		TextView addressView;
		TextView buyView;
		TextView rejectView;
		ImageView heartView;
	}
	
	private void deletefav(String sTel, int position) {
		UpdateNeiborTask task = new UpdateNeiborTask(this, 2, sTel, position);
		task.execute();
	}
	
	private void dofav(String sTel, int position) {
		UpdateNeiborTask task = new UpdateNeiborTask(this, 1, sTel, position);
		task.execute();
	}
	
	private class UpdateNeiborTask extends RGenericTask<Boolean> {
		private int type;
		String sTel;
		int position;
		public UpdateNeiborTask(Context ctx, int type, String sTel, int position) {
			super(ctx);
			this.type = type;
			this.sTel = sTel;
			this.position = position;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.updateFavShop(type, sTel);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				if(type == 1) {
					Toast.makeText(ctx, "已收藏到我的水果店", Toast.LENGTH_SHORT).show();
					String sid = listData.get(position).sId;
					saveData.add(sid);
					FruitDbHandler handler = FruitDbHandler.instance();
					FruitUserDatabase database = new FruitUserDatabase(NeighborRecommendActivity.this);
					handler.saveFavShop(NeighborRecommendActivity.this, database.getWritableDatabase(), sid);
					adapter.notifyDataSetChanged();
				}else if(type == 2) {
					Toast.makeText(ctx, "已删除收藏", Toast.LENGTH_SHORT).show();
					String sid = listData.get(position).sId;
					saveData.remove(sid);
					FruitDbHandler handler = FruitDbHandler.instance();
					FruitUserDatabase database = new FruitUserDatabase(NeighborRecommendActivity.this);
					handler.removeFavShopItem(database.getWritableDatabase(), sid);
					adapter.notifyDataSetChanged();
				}
			} else {
				Toast.makeText(ctx, "操作失败", Toast.LENGTH_SHORT).show();
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
	
	private class GetNeiborDataTask extends RGenericTask<ArrayList<NeighborBean>> {

		public GetNeiborDataTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ArrayList<NeighborBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			if(UserApplication.buyLocation != null) {
				return api.getNeighborData(UserApplication.buyLocation.latitude, UserApplication.buyLocation.longitude);
			} else {
				return api.getNeighborData(UserApplication.currentLocation.latitude, UserApplication.currentLocation.longitude);
			}
		}

		@Override
		protected void onSuccess(ArrayList<NeighborBean> result) {
			listData.clear();
			listData.addAll(result);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			noWifi.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onTaskBegin() {
			noWifi.setVisibility(View.GONE);
			showProgressBar();
		}

		@Override
		protected void onTaskFinished() {
			mList.onRefreshComplete();
			hideProgressBar();
		}
	}
}
