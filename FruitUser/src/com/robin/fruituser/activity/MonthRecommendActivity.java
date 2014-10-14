package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
import com.robin.fruitlib.bean.RecommendBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruituser.R;

/**
 * Ã¿ÔÂÍÆ¼ö
 * @author robin
 *
 */
public class MonthRecommendActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private PullToRefreshListView pullList;
	private List<RecommendBean> listData = new ArrayList<RecommendBean>();
	private ListAdapter adapter;
	private ImageView noWifi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_month_recommend);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		noWifi = (ImageView) findViewById(R.id.no_wifi);
		pullList = (PullToRefreshListView) findViewById(R.id.list);
		pullList.setMode(Mode.BOTH);
		pullList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(MonthRecommendActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				GetMonthRecommendTask task = new GetMonthRecommendTask(MonthRecommendActivity.this);
				task.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(MonthRecommendActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				GetMonthRecommendTask task = new GetMonthRecommendTask(MonthRecommendActivity.this);
				task.execute();
			} 
		});
		adapter = new ListAdapter();
		pullList.setAdapter(adapter);
		initData();
	}
	
	private void initData() {
		GetMonthRecommendTask task = new GetMonthRecommendTask(MonthRecommendActivity.this);
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
			ViewHolder holder;
			final RecommendBean data = listData.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(MonthRecommendActivity.this).inflate(R.layout.list_item_recommend, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img_rec);
				holder.titleView = (TextView) convertView.findViewById(R.id.title_txt_rec);
				holder.contentView = (TextView) convertView.findViewById(R.id.content_txt_rec);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.titleView.setText(data.title);
			holder.contentView.setText(data.content);
			if(!TextUtils.isEmpty(data.imgUrl)){
				FruitBitmapHandler imgHandler = new FruitBitmapHandler(MonthRecommendActivity.this);
				imgHandler.displayImage(holder.img, data.imgHost + data.imgUrl);
//				int iDisplayWidth = MonthRecommendActivity.this.getResources().getDimensionPixelOffset(R.dimen.global_page_padding);
//				holder.img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, iDisplayWidth));
			} else {
				holder.img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 226));
				holder.img.setBackgroundResource(R.color.white);
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MonthRecommendActivity.this,
							MonthRecommendDetailActivity.class);
					intent.putExtra("title", data.title);
					intent.putExtra("img", data.imgHost + data.imgUrl);
					intent.putExtra("content", data.content);
					startActivity(intent);
					((Activity) MonthRecommendActivity.this).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
				}
			});
			return convertView;
		}
	}
	
	static class ViewHolder {
		TextView titleView;
		ImageView img;
		TextView contentView;
	}
	
	private class GetMonthRecommendTask extends RGenericTask<ArrayList<RecommendBean>> {

		public GetMonthRecommendTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ArrayList<RecommendBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getMonthRecData();
		}

		@Override
		protected void onSuccess(ArrayList<RecommendBean> result) {
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
			hideProgressBar();
			pullList.onRefreshComplete();
		}
		
	}
}
