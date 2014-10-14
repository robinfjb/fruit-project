package com.robin.fruitlib.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class DataAdapter<T> extends BaseAdapter {

	public ArrayList<T> dataArray;
	private boolean doBitmapRequest = true;

	public boolean isDoBitmapRequest() {
		return doBitmapRequest;
	}

	public void setDoBitmapRequest(boolean doBitmapRequest) {
		this.doBitmapRequest = doBitmapRequest;
	}

	public DataAdapter(List<T> objects) {
		// super(context, id);
		dataArray = (ArrayList<T>) objects;
		// here is the tricky stuff

	}

	public abstract View getTheView(int position, View convertView,
			ViewGroup parent);

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getTheView(position, convertView, parent);
	}

	@Override
	public int getCount() {
		return dataArray.size();
	}

	@Override
	public T getItem(int position) {
		return dataArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void add(T itemView) {
		if (!dataArray.contains(itemView))
			dataArray.add(itemView);
		this.notifyDataSetChanged();

	}

	public void append(List<T> items) {
		for(T t:dataArray){
			int size=items.size();
			for(int i=0;i<size;i++){
				if(t.equals(items.get(i))){
					items.remove(i);
					i--;
					size--;
				}
			}
		}
		dataArray.addAll(items);
		this.notifyDataSetChanged();
	}

	public void clear() {
		dataArray.clear();
		this.notifyDataSetChanged();

	}
	
	public void remove(T itemView) {
		if (dataArray.contains(itemView))
			dataArray.remove(itemView);
		this.notifyDataSetChanged();
	}

	public void remove(int index) {
		if (index < getCount())
			dataArray.remove(index);
		this.notifyDataSetChanged();
	}

}
