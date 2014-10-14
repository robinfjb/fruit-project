package com.robin.fruitseller.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.loader.BitmapProperty;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.CameraUtil;
import com.robin.fruitlib.util.ImageUtils;
import com.robin.fruitlib.util.QiNiuUtils;
import com.robin.fruitlib.util.UrlConst;
import com.robin.fruitlib.util.QiNiuUtils.QiniuCallback;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.SellerApplication;

public class RegisterThreeActivity extends BaseActivity implements OnClickListener{
	private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private final static int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 201;
	private ImageView imageBig;
	private Button subBtn;
	private Uri photoUri;
	public String phone;
	public String address;
	public double lat;
	public double lon;
	public String shopName;
	public String shopImage;
	public String shopLicence;
	public String passwd;
	public String code;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_three);
		imageBig = (ImageView) findViewById(R.id.bigImg);
		subBtn = (Button) findViewById(R.id.next);
		imageBig.setOnClickListener(this);
		subBtn.setOnClickListener(this);
		
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");
		address = intent.getStringExtra("address");
		shopName = intent.getStringExtra("shopName");
		shopImage = intent.getStringExtra("shopImage");
		passwd = intent.getStringExtra("pwd");
		code = intent.getStringExtra("code");
		lat = intent.getDoubleExtra("lat", 0);
		lon = intent.getDoubleExtra("lon", 0);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.next:
			if(photoUri == null) {
				Toast.makeText(RegisterThreeActivity.this, getString(R.string.update_photo), Toast.LENGTH_LONG).show();
				return;
			}
			showProgressBar();
			QiNiuUtils utils = QiNiuUtils.getInstance(RegisterThreeActivity.this);
			String key = photoUri.getPath();
			key = key.substring(key.lastIndexOf("/") + 1);
			utils.doUploadPrivate(photoUri, key, new QiniuCallback() {
				@Override
				public void onSuccess(JSONObject resp) {
					hideProgressBar();
					shopLicence = resp.optString("key");
					RegisterTask task = new RegisterTask(RegisterThreeActivity.this);
					task.execute();
				}
				@Override
				public void onProcess(long current, long total) {}
				@Override
				public void onFailure(Exception ex) {
					Toast.makeText(RegisterThreeActivity.this, getString(R.string.error_upload), Toast.LENGTH_SHORT).show();
					hideProgressBar();
				}
			});
			break;
		case R.id.bigImg:
			AlertDialog selectDialog = new AlertDialog.Builder(RegisterThreeActivity.this)
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
		}
	}
	
	
	private String mCurrentPhotoPath;
	private File setUpPhotoFile() throws IOException {
		File f = CameraUtil.getInstance(this).createImageFileSz(phone);
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
		Intent intent = new Intent(RegisterThreeActivity.this, AlbumActivity.class);
		startActivityForResult(intent, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 List<String> photos = data.getStringArrayListExtra("result");
				 String photo = photos.get(0);
				 String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				 String imageFileName = "sz-" + phone + "-" + timeStamp + ".jpg";
				 String pathStr = ImageUtils.copy2Sd(RegisterThreeActivity.this, photo, imageFileName);
				 photoUri = Uri.parse(pathStr);
				 
				 int width = ImageUtils.dip2px(RegisterThreeActivity.this, 100);
				 int height = ImageUtils.dip2px(RegisterThreeActivity.this, 69);
				 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
				 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
				 ImageUtils.saveImage(pathStr, smallBitmap);
//				 imageBig.setImageBitmap(smallBitmap);
				 
				 FruitBitmapHandler hander = new FruitBitmapHandler(RegisterThreeActivity.this);
				 hander.displayImage(imageBig, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
				 SellerApplication.picUrl2 = "file://" + pathStr;
				 FruitPerference.saveZhizhaoImage(RegisterThreeActivity.this, "file://" + pathStr);
			 }
			return;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 photoUri = Uri.parse(mCurrentPhotoPath);
				 if(photoUri == null) {
					 Toast.makeText(RegisterThreeActivity.this, getString(R.string.error_sdcard), Toast.LENGTH_SHORT).show();
					 return;
				 }
				 String pathStrTemp = photoUri.getPath();
				 String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				 String imageFileName = "sz-" + phone + "-" + timeStamp + ".jpg";
				 String pathStr = ImageUtils.copy2Sd(RegisterThreeActivity.this, pathStrTemp, imageFileName);
				 photoUri = Uri.parse(pathStr);
				 
				 int width = ImageUtils.dip2px(RegisterThreeActivity.this, 100);
				 int height = ImageUtils.dip2px(RegisterThreeActivity.this, 69);
				 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
				 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
				 ImageUtils.saveImage(pathStr, smallBitmap);
//				 imageBig.setImageBitmap(smallBitmap);
				 
				 BitmapProperty property = new BitmapProperty();
				 property.dir = UrlConst.SELLER_CACHE_NAME;
				 property.expiringIn = -1l;
				 property.rotate = 0;
				 FruitBitmapHandler hander = new FruitBitmapHandler(RegisterThreeActivity.this, property);
				 hander.displayImage(imageBig, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
				 SellerApplication.picUrl2 = "file://" + pathStr;
				 FruitPerference.saveZhizhaoImage(RegisterThreeActivity.this, "file://" + pathStr);
			 }
			return;
		}
	}
	
	private class RegisterTask extends RGenericTask<String> {

		public RegisterTask(Context ctx) {
			super(ctx);
		}

		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.sellerRegister(phone, address, lat, lon, shopName,
					shopImage, shopLicence, passwd, code);
		}

		@Override
		protected void onSuccess(String result) {
			if(result.equals("1")) {
				Toast.makeText(ctx, ctx.getString(R.string.register_success), Toast.LENGTH_SHORT).show();
				FruitPerference.saveSellerMobile(ctx, phone, passwd);
				Intent intent = new Intent(ctx, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
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
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
