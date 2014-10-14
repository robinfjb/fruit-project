package com.robin.fruitseller.media;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import cn.sgone.fruitseller.R;

public class AlbumSelectViewAdapter extends BaseAdapter implements OnClickListener{
	private LayoutInflater mInflater;
    private Context context;
    private ArrayList<String> mPhotos;
    private ArrayList<String> mSelectedPhotos;
    
    public AlbumSelectViewAdapter(Context ctx, ArrayList<String> mPhotos, ArrayList<String> mSelectedPhotos){
        this.context = ctx;
        this.mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPhotos = mPhotos;
        this.mSelectedPhotos = mSelectedPhotos;
    }
    
	@Override
	public int getCount() {
		int count = 0;
        if(mPhotos!=null){
            count = mPhotos.size();
        }
	    return count;
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;
	        if (convertView == null) {
	        	convertView = mInflater.inflate(R.layout.grid_item_album, parent, false);
	            holder = new ViewHolder();
	            holder.mPic = (ImageView) convertView.findViewById(R.id.image_view);
	            holder.mToggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        
	        FruitBitmapHandler handler = new FruitBitmapHandler(context);
	        handler.displayImage(holder.mPic, "file://" + mPhotos.get(position));
	        holder.mToggleButton.setTag(position);
	        holder.mToggleButton.setOnClickListener(this);

	        if (isInSelectedDataList(mPhotos.get(position))) {
	            holder.mToggleButton.setChecked(true);
	        } else {
	            holder.mToggleButton.setChecked(false);
	        }

	        return convertView;
	}
	@Override
	public void onClick(View v) {
		if (v instanceof ToggleButton) {
            ToggleButton toggleButton = (ToggleButton) v;
            int position = (Integer) toggleButton.getTag();
            if (mPhotos != null && mOnItemClickListener != null && position < mPhotos.size()) {
                mOnItemClickListener.onItemClick(toggleButton, position,
                        mPhotos.get(position), toggleButton.isChecked());
            }
        }
	}
	
	private boolean isInSelectedDataList(String selectedString) {
        for (int i = 0; i < mSelectedPhotos.size(); i++) {
            if (mSelectedPhotos.get(i).equals(selectedString)) {
                return true;
            }
        }
        return false;
    }
	
	private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
        public void onItemClick(ToggleButton view, int position, String path,
                                boolean isChecked);
    }
	
	public class ViewHolder {
        public ImageView mPic;
        public ToggleButton mToggleButton;
    }
}
