package com.robin.fruitseller.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.loader.BitmapProperty;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import com.robin.fruitlib.util.CameraUtil;
import com.robin.fruitlib.util.ImageUtils;
import com.robin.fruitlib.util.UrlConst;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.SellerApplication;
import com.robin.fruitseller.view.ScanImageView;

public class MoneyActivity extends BaseActivity implements OnClickListener{
	private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private final static int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 201;
	private ImageView backImg;
	private ScanImageView scanImg;
	private TextView txtBtn;
	private TextView helpTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		scanImg = (ScanImageView) findViewById(R.id.image);
		txtBtn = (TextView) findViewById(R.id.txt_btn);
		txtBtn.setOnClickListener(this);
		helpTxt = (TextView) findViewById(R.id.help);
		helpTxt.setOnClickListener(this);
	}
	
	
	
	@Override
	public void onResume() {
		if(!TextUtils.isEmpty(SellerApplication.picUrl3)) {
			 FruitBitmapHandler hander = new FruitBitmapHandler(MoneyActivity.this);
			 hander.displayImage(scanImg, SellerApplication.picUrl3, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
		}
		super.onResume();
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
		case R.id.txt_btn:
			AlertDialog selectDialog = new AlertDialog.Builder(MoneyActivity.this)
	        .setItems(new String[]{getString(R.string.photo_take),getString(R.string.photo_album)}, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	if(which == 0) {
	            		goCamera();
	            	} else if(which == 1) {
	            		goPhotoAlbum();
	            	}
	            }
	        }).create();
			selectDialog.show();
			break;
		case R.id.help:
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra("url", "http://www.sgone.cn/mgetcode.html");
			startActivity(intent);
			break;
		}
	}

	private String mCurrentPhotoPath;
	private Uri photoUri;
	private File setUpPhotoFile() throws IOException {
		File f = CameraUtil.getInstance(this).createImageFileSa(FruitPerference.getSellerMobile(this));
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}
	
	private void goCamera(){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	private void goPhotoAlbum(){
		Intent intent = new Intent(MoneyActivity.this, AlbumActivity.class);
		startActivityForResult(intent, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 List<String> photos = data.getStringArrayListExtra("result");
				 String photo = photos.get(0);
				 photoUri = Uri.parse(photo);
				 
				 int width = ImageUtils.dip2px(MoneyActivity.this, 90);
				 int height = ImageUtils.dip2px(MoneyActivity.this, 90);
				 Bitmap smallBitmap = ImageUtils.getSmallBitmap(photo, width, height);
				 smallBitmap = ImageUtils.rotateImage90Degree(photo, smallBitmap);
				 ImageUtils.saveImage(photo, smallBitmap);
//				 scanImg.setImageBitmap(smallBitmap);
				 
				 FruitBitmapHandler hander = new FruitBitmapHandler(MoneyActivity.this);
				 hander.displayImage(scanImg, "file://" + photo, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
				 SellerApplication.picUrl3 = "file://" + photo;
				 FruitPerference.saveEwmImage(MoneyActivity.this, "file://" + photo);
			 }
			return;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 photoUri = Uri.parse(mCurrentPhotoPath);
				 if(photoUri == null) {
					 Toast.makeText(MoneyActivity.this, getString(R.string.error_sdcard), Toast.LENGTH_SHORT).show();
					 return;
				 }
				 String pathStr = photoUri.getPath();
				 
				 int width = ImageUtils.dip2px(MoneyActivity.this, 90);
				 int height = ImageUtils.dip2px(MoneyActivity.this, 90);
				 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
				 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
				 ImageUtils.saveImage(pathStr, smallBitmap);
//				 scanImg.setImageBitmap(smallBitmap);
				 
				 BitmapProperty property = new BitmapProperty();
				 property.dir = UrlConst.SELLER_CACHE_NAME;
				 property.expiringIn = -1l;
				 property.rotate = 0;
				 FruitBitmapHandler hander = new FruitBitmapHandler(MoneyActivity.this, property);
				 hander.displayImage(scanImg, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
				 SellerApplication.picUrl3 = "file://" + pathStr;
				 FruitPerference.saveEwmImage(MoneyActivity.this, "file://" + pathStr);
			 }
			return;
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
