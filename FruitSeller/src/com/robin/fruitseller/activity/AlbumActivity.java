package com.robin.fruitseller.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.robin.fruitlib.base.BaseActivity;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.media.AlbumBean;
import com.robin.fruitseller.media.AlbumController;
import com.robin.fruitseller.media.AlbumViewAdapter;

public class AlbumActivity extends BaseActivity {
	private ListView listView;
	private AlbumViewAdapter listViewAdapter;
	private ArrayList<AlbumBean> albums;
	private AlbumController controller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		controller = new AlbumController(AlbumActivity.this);

		listView = (ListView) this.findViewById(R.id.list_view);
		listViewAdapter = new AlbumViewAdapter(getApplicationContext());
		albums = controller.getAlbums();
		listViewAdapter.setAlbumsList(albums);
		listView.setAdapter(listViewAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlbumBean album = albums.get(position);
				String coverUrl = album.mCoverUrl;
				String name = album.mName;
				String number = album.mNum;
				Intent intent = new Intent(AlbumActivity.this, AlbumSelectActivity.class);
				intent.putExtra("cover_url", coverUrl);
				intent.putExtra("name", name);
				intent.putExtra("number", number);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}

	final int REQUEST_CODE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
			} else if (resultCode == RESULT_CANCELED) {
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
