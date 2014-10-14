package com.robin.fruitseller.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.loader.BitmapProperty;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.media.AlbumController;
import com.robin.fruitseller.media.AlbumSelectViewAdapter;
import com.robin.fruitseller.media.AlbumSelectViewAdapter.OnItemClickListener;

public class AlbumSelectActivity extends BaseActivity {
	private GridView gridView;
	private LinearLayout selectedImageLayout;
	private AlbumController controller;
	private HorizontalScrollView scrollView;
	private Button okButton;
	private ArrayList<String> mPhotos = new ArrayList<String>();
	private ArrayList<String> mSelectedPhotos = new ArrayList<String>();
	private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
	private AlbumSelectViewAdapter gridViewAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_select);
		gridView = (GridView) this.findViewById(R.id.grid_view);
		scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
		controller = new AlbumController(AlbumSelectActivity.this);
		okButton = (Button) this.findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedPhotos.size() > 0) {
					Intent intent = new Intent();
					intent.putStringArrayListExtra("result", mSelectedPhotos);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(AlbumSelectActivity.this, getString(R.string.error_atleast_one),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
		Intent intent = getIntent();
		String coverUrl = intent.getStringExtra("cover_url");
		String name = intent.getStringExtra("name");
		String number = intent.getStringExtra("number");

		mPhotos = controller.getPhotos(name);
		gridViewAdapter = new AlbumSelectViewAdapter(getApplicationContext(),
				mPhotos, mSelectedPhotos);
		gridView.setAdapter(gridViewAdapter);
		gridViewAdapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final ToggleButton toggleButton,
					int position, final String path, boolean isChecked) {
				if (mSelectedPhotos.size() >= 1) {
					toggleButton.setChecked(false);
					if (!removePath(path)) {
						Toast.makeText(AlbumSelectActivity.this, getString(R.string.error_only_one),
								Toast.LENGTH_SHORT).show();
					}
					return;
				}

				if (isChecked) {
					if (!hashMap.containsKey(path)) {
						ImageView imageView = (ImageView) LayoutInflater.from(
								AlbumSelectActivity.this).inflate(
								R.layout.activity_choose_imageview,
								selectedImageLayout, false);
						selectedImageLayout.addView(imageView);
						imageView.postDelayed(new Runnable() {
							@Override
							public void run() {
								int off = selectedImageLayout
										.getMeasuredWidth()
										- scrollView.getWidth();
								if (off > 0) {
									scrollView.smoothScrollTo(off, 0);
								}
							}
						}, 100);

						hashMap.put(path, imageView);
						mSelectedPhotos.add(path);

						FruitBitmapHandler hander = new FruitBitmapHandler(
								AlbumSelectActivity.this);
						hander.displayImage(imageView,
								"file://" + mPhotos.get(position), -1,
								BitmapProperty.TYPE_NORMAL,
								FruitBitmapHandler.RENDER_NORMAL);
						imageView
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										toggleButton.setChecked(false);
										removePath(path);
									}
								});
					}
				} else {
					removePath(path);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}

	private boolean removePath(String path) {
		if (hashMap.containsKey(path)) {
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			removeOneData(mSelectedPhotos, path);
			return true;
		} else {
			return false;
		}
	}

	private void removeOneData(ArrayList<String> arrayList, String s) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(s)) {
				arrayList.remove(i);
				return;
			}
		}
	}
}
