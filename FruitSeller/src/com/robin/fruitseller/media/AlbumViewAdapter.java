package com.robin.fruitseller.media;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import cn.sgone.fruitseller.R;

public class AlbumViewAdapter extends BaseAdapter{
	Context context;
	private LayoutInflater mInflater;
	private List<AlbumBean> mAlbums;
	public AlbumViewAdapter(Context context) {
		this.context = context;
		mAlbums = new ArrayList<AlbumBean>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setAlbumsList(List<AlbumBean> albums) {
        mAlbums.clear();
        mAlbums.addAll(albums);
        notifyDataSetChanged();// 閫氱煡鍒锋柊listView銆�
    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAlbums.size();
	}

	@Override
	public Object getItem(int position) {
		 if (mAlbums.isEmpty() || position >= mAlbums.size()) {
	            return null;
	        }
	     return mAlbums.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.list_item_album, null);
            holder = new ViewHolder();
            holder.mAlbumCover = (ImageView) convertView.findViewById(R.id.album_cover);
            holder.mAlbumName = (TextView) convertView.findViewById(R.id.album_name);
            holder.mAlbumNum = (TextView) convertView.findViewById(R.id.album_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AlbumBean album = mAlbums.get(position);
        holder.mAlbumName.setText(album.mName);
        holder.mAlbumNum.setText(album.mNum);
        FruitBitmapHandler handler = new FruitBitmapHandler(context);
        handler.displayImage(holder.mAlbumCover, "file://" + album.mCoverUrl);
        return convertView;
	}
	
	public class ViewHolder {
	    ImageView mAlbumCover;
	    TextView mAlbumName;
	    TextView mAlbumNum;
	}

}
