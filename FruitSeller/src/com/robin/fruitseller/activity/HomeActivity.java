package com.robin.fruitseller.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sgone.fruitseller.R;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.robin.fruitlib.bean.SellerBean;
import com.robin.fruitlib.bean.SellerTradeBean;
import com.robin.fruitlib.bean.TradeDetailBean;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.AsyncTask2.Status;
import com.robin.fruitlib.util.AudioRecorderHandler;
import com.robin.fruitlib.util.TimeUtil;
import com.robin.fruitlib.util.Utils;
import com.robin.fruitseller.SellerApplication;
import com.robin.fruitseller.view.FragmentLeftSlidingMenu;
import com.robin.fruitseller.view.RingView;
import com.umeng.fb.FeedbackAgent;

public class HomeActivity extends SlidingFragmentActivity implements OnClickListener{
	public static final String HOME_ACTION = "cn.sgone.fruitseller.home.getorder";
	protected SlidingMenu leftRightSlidingMenu;
	private ImageView ivTitleBtnLeft;
	private ImageView ivTitleBtnRight;
	private CanvasTransformer transformer;
	private AudioRecorderHandler recordHandler;
	private PullToRefreshListView mList;
	private ArrayList<TradeDetailBean> listData;
	private ListAdapter adapter;
//	private RelativeLayout nodataLayout;
	private static final int REQUEST_CODE = 0x01;
//	private GetOrderTask task;
	private GetPushOrderTask orderTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		if(SellerApplication.agent == null) {
			SellerApplication.agent = new FeedbackAgent(this);
		}
		SellerApplication.agent.sync();
		setContentView(R.layout.activity_main);
		listData = new ArrayList<TradeDetailBean>();
		
		initView();
		initLeftRightSlidingMenu();
		recordHandler = AudioRecorderHandler.getInstance(this);
//		if (!Utils.hasBind(getApplicationContext())) {
			PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,
	        		Utils.getMetaValue(HomeActivity.this, "api_key"));
//		}
//		PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,
//        		Utils.getMetaValue(HomeActivity.this, "api_key"));
//        PushManager.enableLbs(HomeActivity.this);
		IntentFilter filter = new IntentFilter(HOME_ACTION);
		registerReceiver(orderReceiver, filter);

        if(Utils.isSellerLogin(this)) {
        	String[] phoneAndPwd = FruitPerference.getSellerMobileAndPwd(this);
        	LoginTask task = new LoginTask(this, phoneAndPwd[0], phoneAndPwd[1]);
    		task.execute();
        }
        handleIntent(getIntent());
        startTokenTRequest();
        refreshRun.start();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(getIntent());
		super.onNewIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		String oid = intent.getStringExtra("oId");
		if(!TextUtils.isEmpty(oid)) {
			SellerApplication.oId = "";
			/*if(task == null) {
				task = new GetOrderTask(this, oid);
				task.execute();
			} else if(task.stats == 0) {
				task = new GetOrderTask(this, oid);
				task.execute();
			}*/
			refreshHander.sendMessage(refreshHander.obtainMessage()); 
		}
		boolean is_notify_new_direct = intent.getBooleanExtra("is_notify_new_direct", false);
		if(is_notify_new_direct){
			recordHandler.playFromRaw(R.raw.sound);
		} else {
			boolean is_notify_cancel = intent.getBooleanExtra("is_notify_cancel", false);
			if(is_notify_cancel) {
				startActivity(new Intent(this, TradeInfoActivity.class));
			}
		}
	}
	
	BroadcastReceiver orderReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(HOME_ACTION)) {
				handleIntent(intent);
			}
		}
	};

	
	@Override
	protected void onDestroy() {
		unregisterReceiver(orderReceiver);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		 if(!Utils.isSellerLogin(this)) {
	        	Intent intent = new Intent(this, LoginActivity.class);
	        	startActivityForResult(intent, RESULT_CODE_LOGIN);
	     }
		 initData();
		super.onResume();
	}



	private void initView() {
		ivTitleBtnLeft = (ImageView)this.findViewById(R.id.ivTitleBtnLeft);
		ivTitleBtnLeft.setOnClickListener(this);
		
		ivTitleBtnRight = (ImageView)this.findViewById(R.id.ivTitleBtnRight);
		ivTitleBtnRight.setOnClickListener(this);
		
		transformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen*0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
			}
		};
		
//		nodataLayout = (RelativeLayout) findViewById(R.id.nodata);
		mList = (PullToRefreshListView) findViewById(R.id.list);
		mList.setMode(Mode.PULL_FROM_START);
		mList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshHander.sendMessage(refreshHander.obtainMessage());
			}
		});
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
	}
	
	private void initLeftRightSlidingMenu() {
		setBehindContentView(R.layout.view_slide_menu);
		FragmentTransaction leftFragementTransaction = getSupportFragmentManager().beginTransaction();
		Fragment leftFrag = new FragmentLeftSlidingMenu();
		leftFragementTransaction.replace(R.id.main_left_fragment, leftFrag);
		leftFragementTransaction.commit();
		// customize the SlidingMenu
		leftRightSlidingMenu = getSlidingMenu();
		leftRightSlidingMenu.setMode(SlidingMenu.LEFT);
		leftRightSlidingMenu.setBehindOffsetRes(R.dimen.slide_menu_offset);
		leftRightSlidingMenu.setFadeDegree(0.5f);
		leftRightSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		leftRightSlidingMenu.setShadowDrawable(R.drawable.shadow_slide_menu);
		leftRightSlidingMenu.setFadeEnabled(true);
		leftRightSlidingMenu.setBehindScrollScale(0.5f);
		leftRightSlidingMenu.setBehindCanvasTransformer(transformer);
	}
	
	private void initData() {
		/*SellerTradeBean bean = new SellerTradeBean();
		bean.address ="��ַ1";
		bean.stats = "1";
		bean.time = "2010-8-10";
		bean.voiceUrl = "";
		listData.add(bean);
		SellerTradeBean bean2 = new SellerTradeBean();
		bean2.address ="��ַ2";
		bean2.stats = "2";
		bean2.time = "2010-1-10";
		bean2.voiceUrl = "";
		listData.add(bean2);*/
		/*if(Utils.isSellerLogin(this)) {
			startGetDataRequest();
		}*/
		/*if(!TextUtils.isEmpty(SellerApplication.oId)) {
			if(task == null) {
				task = new GetOrderTask(this, SellerApplication.oId);
				task.execute();
			} else if(task.stats == 0) {
				task = new GetOrderTask(this, SellerApplication.oId);
				task.execute();
			}
		}*/
	}
	
	private void startTokenTRequest() {
		QiniuTokenTask task = new QiniuTokenTask(this);
		task.execute();
	}
	
	/*private void startGetDataRequest() {
		SellerTradeBeanTask task = new SellerTradeBeanTask(this);
		task.execute();
	}*/
	
	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(listData.size() == 0) {
				return 1;
			}
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			if(listData.size() == 0) {
				return null;
			}
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(listData.size() == 0) {
				return LayoutInflater.from(HomeActivity.this).inflate(R.layout.list_item_no_data, null);
			}
			final View cView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.list_item_seller_trade, null);
			final TradeDetailBean data = listData.get(position);
			TextView timeText = (TextView) cView.findViewById(R.id.time);
			TextView addressText = (TextView) cView.findViewById(R.id.address);
			TextView length = (TextView) cView.findViewById(R.id.voice_length);
			ImageView soundImg = (ImageView) cView.findViewById(R.id.soundImg);
			final RingView ringView = (RingView) cView.findViewById(R.id.ring);
			timeText.setText(TimeUtil.timeStamp2DateOnlyHour(data.expressStart) 
					+ "-" + TimeUtil.timeStamp2DateOnlyHour(data.expressEnd));
			addressText.setText(data.address);
			length.setText(data.audioLength + "\"");
			try {
				ringView.setData(Integer.parseInt(data.stats));
			} catch(NumberFormatException e) {
			}
			
			ringView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						if(!SellerApplication.status.equals("2")) {
							Toast.makeText(HomeActivity.this, "您尚未通过审核,不能抢单", Toast.LENGTH_SHORT).show();
							return true;
						}
						if(!data.stats.equals("1") ) {
							Toast.makeText(HomeActivity.this, "此订单已被抢", Toast.LENGTH_SHORT).show();
							return true;
						}
						QiangdanTask task = new QiangdanTask(HomeActivity.this, data.oId, position);
						task.execute();
						ringView.setRoundColor(0xff669933);
						return true;
					} else if(event.getAction() == MotionEvent.ACTION_UP) {
						ringView.setRoundColor(0xff8ac642);
						return true;
					}
					return false;
				}
			});
			soundImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("Home", "url:::" + data.audioUrl + "/" + data.audioName);
					recordHandler.playAsync(data.audioUrl + "/" + data.audioName);					
				}
			});
			return cView;
		}
	}
	
	private static final int RESULT_CODE_MONEY = 0x01;
	private static final int RESULT_CODE_LOGIN = 0x02;
	private static final int RESULT_CODE_QIANGDAN = 0x03;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			leftRightSlidingMenu.showMenu();
			break;
		case R.id.ivTitleBtnRight:
			Intent loginIntent = new Intent(HomeActivity.this, MoneyActivity.class);
			startActivityForResult(loginIntent, RESULT_CODE_MONEY);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		switch (requestCode) {
		case RESULT_CODE_MONEY:
			if(resultCode == RESULT_OK) {
			} else if(resultCode == RESULT_CANCELED) {
			}
			break;
		case RESULT_CODE_LOGIN:
			if(resultCode == RESULT_OK) {
			} else {
				finish();
				System.exit(0);
			}
			break;
		case RESULT_CODE_QIANGDAN:
			if(resultCode == RESULT_OK) {
				
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, arg2);
	}
	
	private class QiangdanTask extends RGenericTask<SellerTradeBean> {
		private String oid;
		private int position;
		public QiangdanTask(Context ctx, String oid, int position) {
			super(ctx);
			this.oid = oid;
			this.position = position;
		}

		@Override
		protected SellerTradeBean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.qiangdanRequest(oid);
		}

		@Override
		protected void onSuccess(SellerTradeBean result) {
			if(result != null) {
				listData.remove(position);
				adapter.notifyDataSetChanged();
				/*if(listData.size() == 0) {
					nodataLayout.setVisibility(View.VISIBLE);
				} else {
					nodataLayout.setVisibility(View.GONE);
				}*/
				Intent intent = new Intent(HomeActivity.this, DealActivity.class);
				intent.putExtra("data", result);
				startActivityForResult(intent, RESULT_CODE_QIANGDAN);
				HomeActivity.this.overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			} else {//显示已被抢
				TradeDetailBean bean = listData.get(position);
				bean.stats = "2";
				adapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			TradeDetailBean bean = listData.get(position);
			bean.stats = "2";
			adapter.notifyDataSetChanged();
//			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
			/*for (int i = 0; i < listData.size(); i++) {
				TradeDetailBean bean = listData.get(i);
				if(bean.oId.equals(oid)) {
					bean.stats = "2";
					break;
				}
			}
			adapter.notifyDataSetChanged();
			DealCountDownTimer timer = new DealCountDownTimer(5000, 1000, oid);
			timer.start();*/
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
	
	private class GetOrderTask extends RGenericTask<TradeDetailBean> {
		private String id;
		public GetOrderTask(Context ctx, String id) {
			super(ctx);
			this.id = id;
		}

		@Override
		protected TradeDetailBean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getTradeDetail(id);
		}

		@Override
		protected void onSuccess(TradeDetailBean result) {
			if(listData.size() >= 2) {
				listData.remove(0);
			}
			if(result != null) {
				listData.add(result);
				if(result.stats.equals("1")) {
					DealCountDownTimer timer = new DealCountDownTimer(60000, 1000, result.oId);
					timer.start();
				} else {
					DealCountDownTimer timer = new DealCountDownTimer(5000, 1000, result.oId);
					timer.start();
				}
				Log.d("Home", "url:::" + result.audioUrl + "/" + result.audioName);
				recordHandler.playAsync(result.audioUrl + "/" + result.audioName);
			}
			
			/*if(listData.size() == 0) {
				nodataLayout.setVisibility(View.VISIBLE);
			} else {
				nodataLayout.setVisibility(View.GONE);
			}*/
			adapter.notifyDataSetChanged();
			SellerApplication.oId = "";
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onTaskBegin() {
			stats = 1;
			showProgressBar();
		}

		@Override
		protected void onTaskFinished() {
			stats = 0;
			hideProgressBar();
		}
	}
	
	private class DealCountDownTimer extends CountDownTimer {
		private String oId;
		public DealCountDownTimer(long millisInFuture, long countDownInterval, String oId) {
			super(millisInFuture, countDownInterval);
			this.oId = oId;
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}

		@Override
		public void onFinish() {
			for (int i = 0; i < listData.size(); i++) {
				TradeDetailBean bean = listData.get(i);
				if(bean.oId.equals(oId)) {
					listData.remove(i);
					break;
				}
			}
			adapter.notifyDataSetChanged();
			/*if(listData.size() == 0) {
				nodataLayout.setVisibility(View.VISIBLE);
			} else {
				nodataLayout.setVisibility(View.GONE);
			}*/
		}
	}
	
	
	private class QiniuTokenTask extends RGenericTask<String> {
		public QiniuTokenTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}
		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(HomeActivity.this);
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
	
	
	private class LoginTask extends RGenericTask<SellerBean> {
		private String userName;
		private String pwd;

		public LoginTask(Context ctx, String userName, String pwd) {
			super(ctx);
			this.userName = userName;
			this.pwd = pwd;
		}

		@Override
		protected SellerBean getContent() throws HttpException {
			FruitApi api = new FruitApi(HomeActivity.this);
			return api.loginSeller(userName,Utils.md5(pwd));
		}

		@Override
		protected void onSuccess(SellerBean result) {
			if(!TextUtils.isEmpty(result.sId)) {
//				String backupDBPath = "file://" + Environment.getExternalStorageDirectory() + "/" + UrlConst.SELLER_CACHE_NAME + "/img/";
				FruitPerference.saveShoperImage(HomeActivity.this, result.shopUrl + "/" +result.shopImage);
				FruitPerference.saveZhizhaoImage(HomeActivity.this, result.shopUrl + "/" + result.shopLicence);
				SellerApplication.picUrl1 = result.shopUrl + "/" +result.shopImage;
				SellerApplication.picUrl2 = result.shopUrl + "/" + result.shopLicence;
				FruitPerference.saveSellerMobile(HomeActivity.this, userName, pwd);
				FruitPerference.saveShopName(HomeActivity.this, result.shopName);
				FruitPerference.saveShopAddress(HomeActivity.this, result.address);
				FruitPerference.saveShopPhone(HomeActivity.this, result.sTel);
				FruitPerference.saveShopStatus(HomeActivity.this, result.status);
				SellerApplication.status = result.status;
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
	
	private class GetPushOrderTask extends RGenericTask<ArrayList<TradeDetailBean>> {
		public GetPushOrderTask(Context ctx) {
			super(ctx);
		}
		@Override
		protected ArrayList<TradeDetailBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getPushShopOrderList();
		}

		@Override
		protected void onSuccess(ArrayList<TradeDetailBean> result) {
			if(result != null) {
				listData.clear();
				listData.addAll(result);
			}
			/*if(listData.size() == 0) {
				nodataLayout.setVisibility(View.VISIBLE);
			} else {
				nodataLayout.setVisibility(View.GONE);
			}*/
			adapter.notifyDataSetChanged();
			SellerApplication.oId = "";
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}
		@Override
		protected void onTaskBegin() {
			stats = 1;
			showProgressBar();
		}
		@Override
		protected void onTaskFinished() {
			stats = 0;
			hideProgressBar();
			mList.onRefreshComplete();
		}
	}
	
	Handler refreshHander = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(orderTask == null) {
				orderTask = new GetPushOrderTask(HomeActivity.this);
				orderTask.execute();
			} else if(orderTask.stats == 0 && orderTask.getStatus() != Status.RUNNING) {
				orderTask = new GetPushOrderTask(HomeActivity.this);
				orderTask.execute();
			}
			return false;
		}
	});
	
	Thread refreshRun = new Thread(new Runnable() {
		@Override
		public void run() {
			while(true){ 
                try { 
                	refreshHander.sendMessage(refreshHander.obtainMessage()); 
                    Thread.sleep(60000);
                } catch (InterruptedException e) { 
                } 
			} 
		}
	});
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), getString(R.string.quit), Toast.LENGTH_SHORT).show();                                
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
	 * ��ʾ���ش�
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
	 * ���ؼ��ش�
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
