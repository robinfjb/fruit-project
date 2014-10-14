package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sgone.fruituser.R;

import com.amap.api.maps2d.model.LatLng;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.pocketdigi.utils.FLameUtils;
import com.robin.fruitlib.bean.CommonShopBean;
import com.robin.fruitlib.bean.DeviceBean;
import com.robin.fruitlib.bean.TradeDetailBean;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.database.FruitDbHandler;
import com.robin.fruitlib.database.FruitUserDatabase;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.AudioRecorderHandler;
import com.robin.fruitlib.util.IAudioListener;
import com.robin.fruitlib.util.TimeUtil;
import com.robin.fruitlib.util.Utils;
import com.robin.fruituser.UserApplication;
import com.robin.fruituser.view.FragmentDefaultMain;
import com.robin.fruituser.view.FragmentLeftSlidingMenu;
import com.robin.fruituser.view.ResendDialog;
import com.umeng.fb.FeedbackAgent;

public class HomeActivity extends SlidingFragmentActivity implements OnClickListener, OnTouchListener{
	protected SlidingMenu leftRightSlidingMenu;
	private FragmentDefaultMain mContent;
	private ImageView ivTitleBtnLeft;
	private ImageView ivTitleBtnRight;
	private CanvasTransformer transformer;
	private View fullScreenView;
	private View rootView;
	private LayoutInflater inflater;
	private LinearLayout tipArea;
	private TextView tipsNum;
	private ImageView btnBuy;
	private TextView btnAddress;
	private TextView buyAddress;
	private LinearLayout addressBuyArea;
	private LinearLayout buyArea;
//	private ImageView btnAdd;
	private AudioRecorderHandler recordHandler;
//	private boolean isUploadToken;
//	private ImageView imgHome;
//	private ImageView imgCompany;
//	private ImageView imgFav;
	private ImageView imgRedDot;
	private String address;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		if(UserApplication.agent == null) {
			UserApplication.agent = new FeedbackAgent(this);
		}
		UserApplication.agent.sync();
		inflater = LayoutInflater.from(this);
		setContentView(R.layout.activity_main);
		initView();
//		initFullScreenView();
		initLeftRightSlidingMenu();
		recordHandler = AudioRecorderHandler.getInstance(this);
//		if (!Utils.hasBind(getApplicationContext())) {
			PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(HomeActivity.this, "api_key"));
//		}
//        PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(HomeActivity.this, "api_key"));
//        PushManager.enableLbs(HomeActivity.this);
        
        startTokenTRequest();
		startDeviceConfigRequest();
		startMyShopRequest();
		startGetHistory();
		
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
		super.onNewIntent(intent);
	}

	private void handleIntent(Intent intent) {
		ResendDialog downloadBuilder = null;
		if(intent != null) {
			boolean isFrom = intent.getBooleanExtra("is_notify_cancel", false);
			if(isFrom) {
				if(downloadBuilder == null) {
            		downloadBuilder = new ResendDialog(this);
        			downloadBuilder.setCancelable(false);
        			downloadBuilder.setCanceledOnTouchOutside(false);
        			downloadBuilder.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            	}
        		if(!downloadBuilder.isShowing())
        			downloadBuilder.show();
			}else {//商家接单
				boolean sellergetOrder = intent.getBooleanExtra("is_notify", false);
				if(sellergetOrder) {
					startActivity(new Intent(this, TradeActivity.class));
				}
			}
		}
	}


	@Override
	protected void onResume() {
		updateView();
		
		super.onResume();
	}

	private void initView() {
		ivTitleBtnLeft = (ImageView)this.findViewById(R.id.ivTitleBtnLeft);
		ivTitleBtnLeft.setOnClickListener(this);
		
		ivTitleBtnRight = (ImageView)this.findViewById(R.id.ivTitleBtnRight);
		ivTitleBtnRight.setOnClickListener(this);
		
		imgRedDot = (ImageView)this.findViewById(R.id.titleRedPoint);
		
		transformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen*0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
			}
		};
		
		btnBuy = (ImageView) findViewById(R.id.btn_buy);
		btnBuy.setOnTouchListener(this);
		btnAddress = (TextView) findViewById(R.id.btn_address);
		btnAddress.setOnClickListener(this);
		buyAddress = (TextView) findViewById(R.id.but_address);
		buyAddress.setOnClickListener(this);
		addressBuyArea = (LinearLayout) findViewById(R.id.address_buy_area);
		buyArea = (LinearLayout) findViewById(R.id.btn_buy_area);
		/*btnAdd = (ImageView) findViewById(R.id.btn_add);
		btnAdd.setOnClickListener(this);*/
	}
	
	private void updateView() {
		if(ivTitleBtnRight != null) {
			if(Utils.isUserLogin(this)) {
				ivTitleBtnRight.setVisibility(View.GONE);
			} else {
				ivTitleBtnRight.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void initLeftRightSlidingMenu() {
		mContent = new FragmentDefaultMain();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		setBehindContentView(R.layout.view_slide_menu);
		FragmentTransaction leftFragementTransaction = getSupportFragmentManager().beginTransaction();
		Fragment leftFrag = new FragmentLeftSlidingMenu();
		leftFragementTransaction.replace(R.id.main_left_fragment, leftFrag);
		leftFragementTransaction.commit();
		// customize the SlidingMenu
		leftRightSlidingMenu = getSlidingMenu();
		leftRightSlidingMenu.setMode(SlidingMenu.LEFT);// 设置是左滑还是右滑，还是左右都可以滑，我这里只做了左滑
		leftRightSlidingMenu.setBehindOffsetRes(R.dimen.slide_menu_offset);// 设置菜单宽度
		leftRightSlidingMenu.setFadeDegree(0.5f);// 设置淡入淡出的比例
		leftRightSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);//设置手势模式
		leftRightSlidingMenu.setShadowDrawable(R.drawable.shadow_slide_menu);// 设置左菜单阴影图片
		leftRightSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
		leftRightSlidingMenu.setBehindScrollScale(0.5f);// 设置滑动时拖拽效果
		leftRightSlidingMenu.setBehindCanvasTransformer(transformer);
		
		tipArea = (LinearLayout) findViewById(R.id.tips_area);
		tipArea.setVisibility(View.GONE);
		tipsNum = (TextView) findViewById(R.id.tips_msg);
	}
	
	private void startTokenTRequest() {
		QiniuTokenTask task = new QiniuTokenTask(this);
		task.execute();
	}
	
	private void startDeviceConfigRequest() {
		DeviceTask task = new DeviceTask(this);
		task.execute();
	}
	
	private void startMyShopRequest() {
		GetFavShopTask task = new GetFavShopTask(this);
		task.execute();
	}
	
	private void startGetHistory() {
		ArrayList<String> list = FruitPerference.getOrderList(HomeActivity.this);
		GetHistoryOrderTask task = new GetHistoryOrderTask(this,list);
		task.execute();
	}
	
	private static final int RESULT_CODE_LOGIN = 0x01;
	private static final int RESULT_CODE_FAV = 0x02;
	private static final int RESULT_CODE_ADDRESS = 0x03;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			leftRightSlidingMenu.showMenu();
			if(imgRedDot.getVisibility() == View.VISIBLE) {
				imgRedDot.setVisibility(View.GONE);
			}
			break;
		case R.id.ivTitleBtnRight:
			Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
			startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
			break;
		case R.id.btn_address:
			if(!Utils.isUserLogin(HomeActivity.this)) {
				Intent loginIntents = new Intent(HomeActivity.this, LoginActivity.class);
				startActivityForResult(loginIntents, RESULT_CODE_LOGIN);
				return;
			}
			Intent intentAddressBtn = new Intent(HomeActivity.this, AddressSelectActivity.class);
			startActivityForResult(intentAddressBtn, RESULT_CODE_ADDRESS);
			break;
		case R.id.but_address:
			Intent intentAddressTxt = new Intent(HomeActivity.this, AddressSelectActivity.class);
			startActivityForResult(intentAddressTxt, RESULT_CODE_ADDRESS);
			break;
		/*case R.id.icon_home:
			if(!TextUtils.isEmpty(FruitPerference.getHomeAddress(HomeActivity.this))) {
				double[] latLongH = FruitPerference.getHomeAddressLatLong(HomeActivity.this);
				mContent.getShopInfo(latLongH[0], latLongH[1]);
				UserApplication.currentLocation = new LatLng(latLongH[0], latLongH[1]);
				mContent.changeCamera(latLongH[0], latLongH[1]);
				fullScreenView.setVisibility(View.GONE);
				ivTitleBtnLeft.setVisibility(View.VISIBLE);
			} else {
				Intent favIntentHome = new Intent(HomeActivity.this, FavAddressActivity.class);
				favIntentHome.putExtra("type", "home");
				startActivityForResult(favIntentHome, RESULT_CODE_FAV);
			}
			break;
		case R.id.icon_company:
			if(!TextUtils.isEmpty(FruitPerference.getCompanyAddress(HomeActivity.this))) {
				double[] latLongH = FruitPerference.getCompanyAddressLatLong(HomeActivity.this);
				mContent.getShopInfo(latLongH[0], latLongH[1]);
				UserApplication.currentLocation = new LatLng(latLongH[0], latLongH[1]);
				mContent.changeCamera(latLongH[0], latLongH[1]);
				fullScreenView.setVisibility(View.GONE);
				ivTitleBtnLeft.setVisibility(View.VISIBLE);
			} else {
				Intent favIntentCompany = new Intent(HomeActivity.this, FavAddressActivity.class);
				favIntentCompany.putExtra("type", "company");
				startActivityForResult(favIntentCompany, RESULT_CODE_FAV);
			}
			break;
		case R.id.icon_fav:
			if(!TextUtils.isEmpty(FruitPerference.getFavAddress(HomeActivity.this))) {
				double[] latLongH = FruitPerference.getFavAddressLatLong(HomeActivity.this);
				mContent.getShopInfo(latLongH[0], latLongH[1]);
				UserApplication.currentLocation = new LatLng(latLongH[0], latLongH[1]);
				mContent.changeCamera(latLongH[0], latLongH[1]);
				fullScreenView.setVisibility(View.GONE);
				ivTitleBtnLeft.setVisibility(View.VISIBLE);
			} else {
				Intent favIntentFav = new Intent(HomeActivity.this, FavAddressActivity.class);
				favIntentFav.putExtra("type", "fav");
				startActivityForResult(favIntentFav, RESULT_CODE_FAV);
			}
			break;*/
		case R.id.icon_close:
			fullScreenView.setVisibility(View.GONE);
			ivTitleBtnLeft.setVisibility(View.VISIBLE);
			break;
		/*case R.id.btn_add:
			if(!Utils.isSellerLogin(HomeActivity.this)) {
				Intent loginIntent2 = new Intent(HomeActivity.this, LoginActivity.class);
				startActivityForResult(loginIntent2, RESULT_CODE_LOGIN);
			} else {
				fullScreenView.setVisibility(View.VISIBLE);
				ivTitleBtnLeft.setVisibility(View.GONE);
			}
			break;*/
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE_LOGIN:
			if(resultCode == RESULT_OK) {
				Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RESULT_CANCELED) {
				Toast.makeText(HomeActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
			}
			break;
		/*case RESULT_CODE_FAV:
			if(resultCode == RESULT_OK) {
				String type = data.getStringExtra("type");
				if(type.equals("fav")) {
					if(!TextUtils.isEmpty(FruitPerference.getFavAddress(HomeActivity.this))) {
						double[] latLongH = FruitPerference.getFavAddressLatLong(HomeActivity.this);
						mContent.getShopInfo(latLongH[0], latLongH[1]);
						UserApplication.currentLocation = new LatLng(latLongH[0], latLongH[1]);
						mContent.changeCamera(latLongH[0], latLongH[1]);
						fullScreenView.setVisibility(View.GONE);
					}
				}
			}
			break;*/
		case RESULT_CODE_ADDRESS:
			if(resultCode == RESULT_OK) {
				address = data.getStringExtra("address");
				double lat = data.getDoubleExtra("lat", 0.0d);
				double lon = data.getDoubleExtra("lon", 0.0d);
				UserApplication.buyLocation = new LatLng(lat, lon);
				addressBuyArea.setVisibility(View.GONE);
				buyArea.setVisibility(View.VISIBLE);
				buyAddress.setText(address + " >");
				mContent.changeCamera(lat, lon);
				mContent.addMarkerBuyLocation(lat, lon, address);
				mContent.getShopInfo(lat, lon);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v.equals(btnBuy)) {
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				if(!Utils.isUserLogin(HomeActivity.this)) {
					Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
					startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
					return true;
				}
				
				recordHandler.recordStart(new IAudioListener() {
					@Override
					public void start() {
						recordHandler.showVoiceDialog(HomeActivity.this);
					}
					
					@Override
					public void fail() {
					}
					
					@Override
					public void end() {
					}

					@Override
					public void success(Uri uri, String key) {
					}
				});
				break;
			case MotionEvent.ACTION_UP:
				recordHandler.recordEnd(new IAudioListener() {
					@Override
					public void start() {
					}
					
					@Override
					public void fail() {
					}
					
					@Override
					public void end() {
					}

					@Override
					public void success(Uri uri, String key) {
						/*FLameUtils lameUtils=new FLameUtils(1, 16000, 96);
						String url = uri.toString();
						String newUrl = url.substring(0, url.indexOf(key)) + key.substring(0, key.indexOf(".")) + ".mp3";
						lameUtils.raw2mp3(url, newUrl);*/
						
						int length = recordHandler.getAudioLength();
						Intent intent = new Intent(HomeActivity.this, OrderDetailActivity.class);
						intent.putExtra("address", address);
						intent.putExtra("uri", uri);
						intent.putExtra("key", key);
						intent.putExtra("length", length);
						startActivity(intent);
					}
				});
				break;
			}
		
		}
		return true;
	}
	
	private class QiniuTokenTask extends RGenericTask<String> {

		public QiniuTokenTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(HomeActivity.this);
			return api.uploadToken(FruitPerference.getUserMobile(ctx));
		}

		@Override
		protected void onSuccess(String result) {
			UserApplication.qiniuToken = result;
		}

		@Override
		protected void onAnyError(int code, String msg) {
		}

		@Override
		protected void onTaskBegin() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onTaskFinished() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class DeviceTask extends RGenericTask<DeviceBean> {

		public DeviceTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected DeviceBean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getDevice();
		}

		@Override
		protected void onSuccess(DeviceBean result) {
			if(result != null) {
				UserApplication.deviceBean = result;
				String time = FruitPerference.getDeviceTimeStamp(HomeActivity.this);
				if(TextUtils.isEmpty(time)) {
					FruitPerference.saveDeviceTimeStamp(HomeActivity.this, UserApplication.deviceBean.timeStamp);
				}
				if(!TextUtils.isEmpty(time) && !time.equals(result.timeStamp)) {
					imgRedDot.setVisibility(View.VISIBLE);
				} else {
					imgRedDot.setVisibility(View.GONE);
				}
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
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
	
	private class GetFavShopTask extends RGenericTask<List<CommonShopBean>> {

		public GetFavShopTask(Context ctx) {
			super(ctx);
		}

		@Override
		protected List<CommonShopBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getAllShop();
		}

		@Override
		protected void onSuccess(List<CommonShopBean> result) {
			FruitDbHandler handler = FruitDbHandler.instance();
			FruitUserDatabase database = new FruitUserDatabase(HomeActivity.this);
			for (CommonShopBean shop: result) {
				handler.saveFavShop(HomeActivity.this, database.getWritableDatabase(), shop.sId);
			}
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
	
	
	private class GetHistoryOrderTask extends RGenericTask<ArrayList<TradeDetailBean>> {
		private ArrayList<String> oids;
		
		public GetHistoryOrderTask(Context ctx, ArrayList<String> oids) {
			super(ctx);
			this.oids = oids;
		}

		@Override
		protected ArrayList<TradeDetailBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getHistoryOrders(oids);
		}

		@Override
		protected void onSuccess(ArrayList<TradeDetailBean> result) {
			if(result.size() > 0) {
				FruitPerference.removeOrderList(ctx);
				for (int i = 0; i < result.size(); i++) {
					TradeDetailBean tradeBean = result.get(i);
					if(tradeBean.stats.equals("7")) {
						Intent intent = new Intent();
						intent.putExtra("is_notify_cancel", true);
						handleIntent(intent);
						break;
					}
				}
			}
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
	
	
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
	protected Dialog mDialog = null;
	/**
	 * 显示加载窗
	 */
	public void showProgressBar() {
		try {// to avoid BinderProxy is not valid error
			if(mDialog == null) {
				mDialog = new Dialog(this, R.style.CustomDialogThemeWithBg);
				View root = getLayoutInflater().inflate(R.layout.dialog_loading, null);
				mDialog.setContentView(root);
			}
			mDialog.show();
			mDialog.setCanceledOnTouchOutside(false);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 隐藏加载窗
	 */
	public void hideProgressBar() {
		try {// to avoid BinderProxy is not valid error
			if (mDialog != null) {
				mDialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
