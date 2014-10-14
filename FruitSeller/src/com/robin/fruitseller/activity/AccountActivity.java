package com.robin.fruitseller.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.robin.fruitlib.util.UrlConst;
import com.robin.fruitlib.util.QiNiuUtils.QiniuCallback;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.SellerApplication;

public class AccountActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private TextView nameTxt;
	private TextView phoneTxt;
	private TextView addressTxt;
	private RelativeLayout headArea;
	private RelativeLayout nameArea;
	private RelativeLayout phoneArea;
	private RelativeLayout pwdArea;
	private RelativeLayout addressArea;
	private RelativeLayout zhizhaoArea;
	private ImageView headImg;
	private ImageView zhizhaoImg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		nameTxt = (TextView) findViewById(R.id.shop_name);
		phoneTxt = (TextView) findViewById(R.id.phone);
		addressTxt = (TextView) findViewById(R.id.address);
		headArea = (RelativeLayout) findViewById(R.id.head_icon_area);
		nameArea = (RelativeLayout) findViewById(R.id.name_area);
		phoneArea = (RelativeLayout) findViewById(R.id.phone_area);
		pwdArea = (RelativeLayout) findViewById(R.id.pwd_area);
		addressArea = (RelativeLayout) findViewById(R.id.address_area);
		zhizhaoArea = (RelativeLayout) findViewById(R.id.zhizhao_icon_area);
		headImg = (ImageView) findViewById(R.id.head_icon);
		zhizhaoImg = (ImageView) findViewById(R.id.zhizhao_icon);
		headArea.setOnClickListener(this);
		nameArea.setOnClickListener(this);
		pwdArea.setOnClickListener(this);
		addressArea.setOnClickListener(this);
		zhizhaoArea.setOnClickListener(this);
		
		initData();
	}

	private void initData() {
		nameTxt.setText(FruitPerference.getShopName(this));
		phoneTxt.setText(FruitPerference.getShopPhone(this));
		addressTxt.setText(FruitPerference.getShopAddress(this));
		
		FruitBitmapHandler hander1 = new FruitBitmapHandler(AccountActivity.this);
		hander1.displayImage(headImg, SellerApplication.picUrl1, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
		FruitBitmapHandler hander2 = new FruitBitmapHandler(AccountActivity.this);
		hander2.displayImage(zhizhaoImg, SellerApplication.picUrl2, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
	}

	private static final int REQUEST_HEAD_SET = 0x01;
	private static final int REQUEST_NAME_SET = 0x02;
	private static final int REQUEST_PHONE_SET = 0x03;
	private static final int REQUEST_PWD_SET = 0x04;
	private static final int REQUEST_ADDRESS_SET = 0x05;
	private static final int REQUEST_ZHIZHAO_SET = 0x06;
	private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final static int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			setResult(Activity.RESULT_CANCELED);
			finish();
			overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
			break;
		case R.id.head_icon_area:
			AlertDialog selectDialog = new AlertDialog.Builder(AccountActivity.this)
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
		case R.id.name_area:
			Intent nameIntent = new Intent(AccountActivity.this, SetNameActivity.class);
			startActivityForResult(nameIntent, REQUEST_NAME_SET);
			break;
		case R.id.phone_area:
			Intent phoneIntent = new Intent(AccountActivity.this, SetNumberActivity.class);
			startActivityForResult(phoneIntent, REQUEST_PHONE_SET);
			break;
		case R.id.pwd_area:
			Intent pwdIntent = new Intent(AccountActivity.this, SetPwdActivity.class);
			startActivityForResult(pwdIntent, REQUEST_PWD_SET);
			break;
		case R.id.address_area:
			Intent addressIntent = new Intent(AccountActivity.this, SetAddressActivity.class);
			startActivityForResult(addressIntent, REQUEST_ADDRESS_SET);
			break;
		case R.id.zhizhao_icon_area:
			break;
		}
	}
	
	//==============================camera=====================================================
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
		Intent intent = new Intent(AccountActivity.this, AlbumActivity.class);
		startActivityForResult(intent, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int reqestCode, int resultCode, Intent data) {
		switch (reqestCode) {
		case REQUEST_NAME_SET:
			if(resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				nameTxt.setText(result);
			}
			break;
		case REQUEST_PHONE_SET:
			if(resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				phoneTxt.setText(result);
			}
			break;
		case REQUEST_PWD_SET:
	
			break;
		case REQUEST_ADDRESS_SET:
			if(resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				addressTxt.setText(result);
			}
			break;

		case REQUEST_HEAD_SET:
			break;
		case REQUEST_ZHIZHAO_SET:
			break;
		case CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 List<String> photos = data.getStringArrayListExtra("result");
				 String photo = photos.get(0);
				 
				 String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				 String imageFileName = "sa-" + FruitPerference.getSellerMobile(this) + "-" + timeStamp + ".jpg";
				 String pathStr = ImageUtils.copy2Sd(AccountActivity.this, photo, imageFileName);
				 photoUri = Uri.parse(pathStr);
				 
				String key = pathStr.substring(pathStr.lastIndexOf("/") + 1);
			 	showProgressBar();
				QiNiuUtils utils = QiNiuUtils.getInstance(AccountActivity.this);
				utils.doUploadPrivate(photoUri, key, new QiniuCallback() {
					@Override
					public void onSuccess(JSONObject resp) {
						hideProgressBar();
						String shopImg = resp.optString("key");
						 RenewShopImgTask task = new RenewShopImgTask(AccountActivity.this, shopImg, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
						 task.execute();
					}
					@Override
					public void onProcess(long current, long total) {}
					@Override
					public void onFailure(Exception ex) {
						Toast.makeText(AccountActivity.this, getString(R.string.error_upload), Toast.LENGTH_SHORT).show();
						hideProgressBar();
					}
				});
					
				 
			 }
			 break;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			 if (resultCode == RESULT_OK) {
				 photoUri = Uri.parse(mCurrentPhotoPath);
				 if(photoUri == null) {
					 Toast.makeText(this, getString(R.string.error_sdcard), Toast.LENGTH_SHORT).show();
					 return;
				 }
				 
				 String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				 String imageFileName = "sa-" + FruitPerference.getSellerMobile(this) + "-" + timeStamp + ".jpg";
				 String pathStr = ImageUtils.copy2Sd(AccountActivity.this, mCurrentPhotoPath, imageFileName);
				 photoUri = Uri.parse(pathStr);

				 String key = pathStr.substring(pathStr.lastIndexOf("/") + 1);
				 
				 showProgressBar();
					QiNiuUtils utils = QiNiuUtils.getInstance(AccountActivity.this);
					utils.doUploadPrivate(photoUri, key, new QiniuCallback() {
						@Override
						public void onSuccess(JSONObject resp) {
							hideProgressBar();
							String shopImg = resp.optString("key");
							RenewShopImgTask task = new RenewShopImgTask(AccountActivity.this, shopImg, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
							 task.execute();
						}
						@Override
						public void onProcess(long current, long total) {}
						@Override
						public void onFailure(Exception ex) {
							Toast.makeText(AccountActivity.this, getString(R.string.error_upload), Toast.LENGTH_SHORT).show();
							hideProgressBar();
						}
					});
				 
			 }
			 break;
		}
		super.onActivityResult(reqestCode, resultCode, data);
	}

	private class RenewShopImgTask extends RGenericTask<Boolean> {
		private String name;
		private int type;
		public RenewShopImgTask(Context ctx, String name, int type) {
			super(ctx);
			this.name = name;
			this.type = type;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			ArrayList<String> keys = new ArrayList<String>();
			keys.add("shopImage");
			ArrayList<String> values = new ArrayList<String>();
			values.add(name);
			return api.renewShopInfo(keys, values);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				if(type == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
					 String pathStr = photoUri.getPath();
					 
					 int width = ImageUtils.dip2px(AccountActivity.this, 50);
					 int height = ImageUtils.dip2px(AccountActivity.this, 40);
					 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
					 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
					 ImageUtils.saveImage(pathStr, smallBitmap);
//					 headImg.setImageBitmap(smallBitmap);
					 BitmapProperty property = new BitmapProperty();
					 property.dir = UrlConst.SELLER_CACHE_NAME;
					 property.expiringIn = -1l;
					 property.rotate = 0;
					 FruitBitmapHandler hander = new FruitBitmapHandler(AccountActivity.this, property);
					 hander.displayImage(headImg, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
					 SellerApplication.picUrl1 = "file://" + pathStr;
					 FruitPerference.saveShoperImage(AccountActivity.this, "file://" + pathStr);
				} else if(type == CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE) {
					String pathStr = photoUri.getPath();
					
					 int width = ImageUtils.dip2px(AccountActivity.this, 50);
					 int height = ImageUtils.dip2px(AccountActivity.this, 40);
					 Bitmap smallBitmap = ImageUtils.getSmallBitmap(pathStr, width, height);
					 smallBitmap = ImageUtils.rotateImage90Degree(pathStr, smallBitmap);
					 ImageUtils.saveImage(pathStr, smallBitmap);
//					 headImg.setImageBitmap(smallBitmap);
					 
					 FruitBitmapHandler hander = new FruitBitmapHandler(AccountActivity.this);
					 hander.displayImage(headImg, "file://" + pathStr, -1, BitmapProperty.TYPE_NORMAL, FruitBitmapHandler.RENDER_NORMAL);
					 SellerApplication.picUrl1 = "file://" + pathStr;
					 FruitPerference.saveShoperImage(AccountActivity.this, "file://" + pathStr);
				}
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}
		@Override
		protected void onTaskBegin() {
//			hideProgressBar();
		}
		@Override
		protected void onTaskFinished() {
//			showProgressBar();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.page_enter, R.anim.page_exit);
		super.onBackPressed();
	}
}
