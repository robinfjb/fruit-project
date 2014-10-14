package com.robin.fruituser.adapter;

import java.util.List;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.robin.fruitlib.adapter.DataAdapter;
import com.robin.fruitlib.bean.OrderBean;
import cn.sgone.fruituser.R;

public class SimpleThumbAdapter extends DataAdapter<OrderBean> {
	private Activity mCtx;
	private LayoutInflater mInflater;

	public SimpleThumbAdapter(Activity context, List<OrderBean> objects) {
		super(objects);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mCtx = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final OrderBean item = this.getItem(position);
		if (convertView == null) {
//			convertView = mInflater.inflate(R.layout.grid_item_thumb, null);
			// 计算item的高度
			DisplayMetrics dm = new DisplayMetrics();
			// 取得窗口属性
			mCtx.getWindowManager().getDefaultDisplay().getMetrics(dm);
			// item高度
			int itemHeight = (dm.widthPixels - 35) / 3;
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT, itemHeight);
			convertView.setLayoutParams(param);
			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
//			holder.thumb = (ImageView) convertView.findViewById(R.id.thumb);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}

	static class ViewHolder {
		ImageView thumb;
		TextView title;
	}

	public void append(List<OrderBean> items) {
		if (items == null || items.size() == 0)
			return;
		for (OrderBean t : dataArray) {
			int size = items.size();
			for (int i = 0; i < size; i++) {
				if (t.equals(items.get(i))) {
					items.remove(i);
					i--;
					size--;
				}
			}
		}
		dataArray.addAll(items);
		this.notifyDataSetChanged();
	}

	@Override
	public View getTheView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int position) {
		return true;
		// return false if position == position you want to disable
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}