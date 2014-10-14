package com.robin.fruitseller.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.loader.BitmapProperty;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.CameraUtil;
import com.robin.fruitlib.util.GeoUtils;
import com.robin.fruitlib.util.ImageUtils;
import com.robin.fruitlib.util.QiNiuUtils;
import com.robin.fruitlib.util.QiNiuUtils.QiniuCallback;
import com.robin.fruitlib.util.UrlConst;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.SellerApplication;

public class RegisterTwoActivity extends BaseActivity implements OnClickListener{
	private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final static int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	private EditText nameInput;
	private EditText addressInput;
	private RelativeLayout uploadlayout;
	private Button nextBtn;
	private Uri photoUri;
	private ImageView imageSmall;
	private String phone;
	private String code;
	private String pwd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_two);
		nameInput = (EditText) findViewById(R.id.name);
		addressInput = (EditText) findViewById(R.id.address);
		uploadlayout = (RelativeLayout) findViewById(R.id.uploadImgArea);
		nextBtn = (Button) findViewById(R.id.next);
		imageSmall = (ImageView) findViewById(R.id.img_small);
		uploadlayout.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");
		code = intent.getStringExtra("code");
		pwd = intent.getStringExtra("pwd");
		
		if(TextUtils.isEmpty(SellerApplication.qiniuTokenPrivate)) {
			QiniuTokenTask task = new QiniuTokenTask(this);
			task.execute();
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
		case R.id.uploadImgArea:
			AlertDialog selectDialog = new AlertDialog.Builder(RegisterTwoActivity.this)
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
		case R.id.next:
			final String name = nameInput.getText().toString().trim();
			if(TextUtils.isEmpty(name)) {
				Toast.makeText(RegisterTwoActivity.this, getString(R.string.warn_empty_phone), Toast.LENGTH_LONG).show();
				return;
			}
			final String address = addressInput.getText().toString().trim();
			if(TextUtils.isEmpty(address)) {
				Toast.makeText(RegisterTwoActivity.this, getString(R.string.warn_empty_address), Toast.LENGTH_LONG).show();
				return;
			}
			if(photoUri == null) {
				Toast.makeText(RegisterTwoActivity.this, getString(R.string.warn_empty_photo), Toast.LENGTH_LONG).show();
				return;
			}
			showProgressBar();
			QiNiuUtils utils = QiNiuUtils.getInstance(RegisterTwoActivity.this);
			String key = photoUri.getPath();
			key = key.substring(key.lastIndexOf("/") + 1);
			utils.doUploadPrivate(photoUri, key, new QiniuCallback() {
				@Override
				public void onSuccess(JSONObject resp) {
					hideProgressBar();
					String shopImg = resp.optString("key");
					GeoUtils utilsHome = new GeoUtils(new GeocodeSearch(address, name, shopImg));
					utilsHome.getLatlon(RegisterTwoActivity.this, address);
				}
				@Override
				public void onProcess(long current, long total) {}
				@Override
				public void onFailure(Exception ex) {
					Toast.makeText(RegisterTwoActivity.this, getString(R.string.error_upload), Toast.LENGTH_SHORT).show();
					hideProgressBar();
				}
			});
			
			break;
		}
	}
	
	private class GeocodeSearch implements OnGeocodeSearchListener {
		private String addressStr;
		private String shopName;
		private String shopImage;
		public GeocodeSearch(String address, String shopName, String shopImage) {
			this.addressStr = address;
			this.shopName = shopName;
			this.shopImage = shopImage;
		}
		@Override
		public void onGeocodeSearched(GeocodeResult result, int rCode) {
			hideProgressBar();
			if (rCode == 0) {
				if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
					GeocodeAddress address = result.getGeocodeAddressList().get(0);
					LatLonPoint point = address.getLatLonPoint();
					double lon = point.getLongitude();
					double lat = point.getLatitude();
					Intent intent = new Intent(RegisterTwoActivity.this, RegisterThreeActivity.class);
					intent.putExtra("lon", lon);
					intent.putExtra("lat", lat);
					intent.putExtra("phone", phone);
					intent.putExtra("code", code);
					intent.putExtra("pwd", pwd);
					intent.putExtra("address", addressStr);
					intent.putExtra("shopName", shopName);
					intent.putExtra("shopImage", shopImage);
					startActivity(intent);
					overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
				} else {
					Toast.makeText(RegisterTwoActivity.this, getString(R.string.error_latlon), Toast.LENGTH_LONG).show();
				}
			} else if (rCode == 27) {
				Toast.makeText(RegisterTwoActivity.this, getString(R.string.error_27), Toast.LENGTH_LONG).show();
			} else if (rCode == 32) {
				Toast.makeText(RegisterTwoActivity.this, getString(R.string.error_32), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(RegisterTwoActivity.this, getString(R.string.error_unknow), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		}
	}
	
	private String mCurrentPhotoPath;
	private File setUpPhotoFile() throws IOException {
		File f = CameraUtil.getInstance(this).createImageFileSa(phone);
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
		Intent intent = new Intent(RegisterTwoActivity.this, AlbumActivity.class);
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
				 String imageFileName = "sa-" + phone + "-" + timeStamp + ".jpg";
				 String pathStr = ImageUtils.copy2Sd(RegisterTwoActivity.this, photo, imageFileName);
				 photoUri = Uri.parse(pathStr);
				 
				 int width = ImageUtils.dip2px(RegisterTwoActivity.this, 70);
				 int height = ImageUtils.dip2px(RegisterTwoActivity.this, 70);
				 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
				 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
				 ImageUtils.saveImage(pathStr, smallBitmap);
//				 imageSmall.setImageBitmap(smallBitmap);
				 
				 FruitBitmapHandler hander = new FruitBitmapHandler(RegisterTwoActivity.this);
				 hander.displayImage(imageSmall, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
				 SellerApplication.picUrl1 = "file://" + pathStr;
				 FruitPerference.saveShoperImage(RegisterTwoActivity.this, "file://" + pathStr);
			 }
			return;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 photoUri = Uri.parse(mCurrentPhotoPath);
				 if(photoUri == null) {
					 Toast.makeText(RegisterTwoActivity.this, "", Toast.LENGTH_SHORT).show();
					 return;
				 }
				 String pathStrTemp = photoUri.getPath();
				 
				 String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				 String imageFileName = "sa-" + phone + "-" + timeStamp + ".jpg";
				 String pathStr = ImageUtils.copy2Sd(RegisterTwoActivity.this, pathStrTemp, imageFileName);
				 photoUri = Uri.parse(pathStr);
				 
				 int width = ImageUtils.dip2px(RegisterTwoActivity.this, 70);
				 int height = ImageUtils.dip2px(RegisterTwoActivity.this, 70);
				 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
				 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
				 ImageUtils.saveImage(pathStr, smallBitmap);
//				 imageSmall.setImageBitmap(smallBitmap);
				 BitmapProperty property = new BitmapProperty();
				 property.dir = UrlConst.SELLER_CACHE_NAME;
				 property.expiringIn = -1l;
				 property.rotate = 270;
				 FruitBitmapHandler hander = new FruitBitmapHandler(RegisterTwoActivity.this, property);
				 hander.displayImage(imageSmall, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
				 SellerApplication.picUrl1 = "file://" + pathStr;
				 FruitPerference.saveShoperImage(RegisterTwoActivity.this, "file://" + pathStr);
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
	
	private class QiniuTokenTask extends RGenericTask<String> {
		public QiniuTokenTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}
		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(RegisterTwoActivity.this);
			return api.uploadPrivateToken(FruitPerference.getSellerMobile(ctx));
		}
		@Override
		protected void onSuccess(String result) {
			SellerApplication.qiniuTokenPrivate = result;
		}
		@Override
		protected void onAnyError(int code, String msg) {
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
}
