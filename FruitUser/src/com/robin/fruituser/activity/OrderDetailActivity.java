package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.OrderDetailBean;
import com.robin.fruitlib.bean.SendReturnBean;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.AudioRecorderHandler;
import com.robin.fruitlib.util.QiNiuUtils;
import com.robin.fruitlib.util.QiNiuUtils.QiniuCallback;
import com.robin.fruitlib.util.TimeUtil;
import cn.sgone.fruituser.R;
import com.robin.fruituser.UserApplication;

public class OrderDetailActivity extends BaseActivity implements OnClickListener, SensorEventListener{
	private ImageView backImg;
//	private ImageView heartImg;
	private ImageView voicePlay;
	private Button confirmBtn;
	private ListView mList;
	private ListAdapter adapter;
	private List<OrderDetailBean> listData = new ArrayList<OrderDetailBean>();
	private static final int START_TIME_DIALOG_ID = 0;
	private static final int END_TIME_DIALOG_ID = 1;
	private Uri uri;
	private String key;
	private boolean isQiniuSuccess;
	private AudioManager audioManager;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private String shopId = "";
	private String startDate;
	private String endDate;
	private String audioName;
	private String addressStr;
	private TextView addressTxt;
	private int length;
	private TextView audioLengthTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		Intent intent = getIntent();
		uri = intent.getParcelableExtra("uri");
		key = intent.getStringExtra("key");
		addressStr = intent.getStringExtra("address");
		length = intent.getIntExtra("length", 0);
		
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		/*heartImg = (ImageView) findViewById(R.id.ivTitleBtnRight);
		heartImg.setOnClickListener(this);*/
		voicePlay = (ImageView) findViewById(R.id.btn_voice_play);
		voicePlay.setOnClickListener(this);
		confirmBtn = (Button) findViewById(R.id.confirm);
		confirmBtn.setOnClickListener(this);
		addressTxt =  (TextView) findViewById(R.id.address);
		audioLengthTxt =  (TextView) findViewById(R.id.voice_length);
		if(length > 0) {
			audioLengthTxt.setText(length + "\"");
		}
		mList = (ListView) findViewById(R.id.list);
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
		initData();
		doVoiceUpload(false);
		
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}
	
	
	
	@Override
	public void onResume() {
		mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}



	@Override
	public void onPause() {
		mSensorManager.unregisterListener(this);
		super.onPause();
	}



	private void doVoiceUpload(final boolean toast) {
		if(TextUtils.isEmpty(UserApplication.qiniuToken)) {
			startTokenTRequest(toast);
		} else {
			QiNiuUtils utils = QiNiuUtils.getInstance(OrderDetailActivity.this);
			showProgressBar();
			utils.doUpload(uri, key, new QiniuCallback() {
				@Override
				public void onSuccess(JSONObject resp) {
					hideProgressBar();
					audioName = resp.optString("key");
					isQiniuSuccess = true;
					if(toast) {
						SendOrderTask task = new SendOrderTask(OrderDetailActivity.this);
						task.execute();
					}
				}
				@Override
				public void onProcess(long current, long total) {}
				@Override
				public void onFailure(Exception ex) {
					hideProgressBar();
					if(toast) {
						Toast.makeText(OrderDetailActivity.this, "上传失败，请退出应用重试", Toast.LENGTH_SHORT).show();
						hideProgressBar();
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void initData() {
		OrderDetailBean beanRange = new OrderDetailBean();
		beanRange.key = "发单范围";
		beanRange.content = "全部";
		listData.add(beanRange);
		OrderDetailBean beanStartTime = new OrderDetailBean();
		beanStartTime.key = "送货开始时间";
		beanStartTime.content = TimeUtil.getNowTimeHHMM();
    	startDate = String.valueOf(new Date().getTime() / 1000);
		listData.add(beanStartTime);
		OrderDetailBean beanEndTime = new OrderDetailBean();
		beanEndTime.key = "送货结束时间";
		beanEndTime.content = TimeUtil.getDelayTimeHHMM(3600);
		endDate = String.valueOf(new Date().getTime() / 1000 + 3600);
		listData.add(beanEndTime);

		addressTxt.setText(addressStr);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.btn_voice_play:
			playSound();
			break;
		case R.id.confirm:
			confirmOrder();
			break;
		case R.id.ivTitleBtnRight:
			Intent intent = new Intent(this, FavShopActivity.class);
			startActivityForResult(intent, 1);
			break;
		}	
	}
	
	private void playSound() {
		AudioRecorderHandler handler = AudioRecorderHandler.getInstance(OrderDetailActivity.this);
		handler.play();
	}
	
	private void confirmOrder() {
		if(isQiniuSuccess) {
			OrderDetailBean startBean = listData.get(1);
			OrderDetailBean endBean = listData.get(2);
			String[] startsStr = startBean.content.split(":");
			String[] endsStr = endBean.content.split(":");
			int startTime = Integer.parseInt(startsStr[0]) * 100 + Integer.parseInt(startsStr[1]);
			int endTime = Integer.parseInt(endsStr[0]) * 100 + Integer.parseInt(endsStr[1]);
			if(startTime >= endTime) {
				Toast.makeText(this, "请选择正确的时间段", Toast.LENGTH_SHORT).show();
				return;
			}
			SendOrderTask task = new SendOrderTask(OrderDetailActivity.this);
			task.execute();
			/*hideProgressBar();
			Intent intent = new Intent(this, SuccessActivity.class);
			startActivity(intent);*/
		} else {
			doVoiceUpload(true);
		}
	}
	
	private void startTokenTRequest(boolean toast) {
		QiniuTokenTask task = new QiniuTokenTask(this,toast);
		task.execute();
	}
	
	@Override  
    protected Dialog onCreateDialog(int id) {    
        switch (id) {  
        case START_TIME_DIALOG_ID:
        	Date dateS = new Date();
            return new TimePickerDialog(this, mTimeSetListenerStart, dateS.getHours(), dateS.getMinutes(), true); 
        case END_TIME_DIALOG_ID:  
        	Date dateE = new Date();
            return new TimePickerDialog(this, mTimeSetListenerEnd, dateE.getHours() + 1, dateE.getMinutes(), true); 
        }  
        return null;   
    }  
	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//		if(arg0 == 0) {//地址选择
//			if(arg1 == RESULT_OK) {
//				String address = arg2.getStringExtra("address");
//				this.address = address;
//				OrderDetailBean data = listData.get(1);
//				data.content = address;
//				adapter.notifyDataSetChanged();
//			}
//		} else 
		if(arg0 == 1) {//店家选择
			if(arg1 == RESULT_OK) {
				String favShop = arg2.getStringExtra("fav_shop");
				shopId = arg2.getStringExtra("fav_shop_id");
				OrderDetailBean data = listData.get(0);
				data.content = favShop;
				adapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}


	private TimePickerDialog.OnTimeSetListener mTimeSetListenerStart;        
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd; 
	
	private class ListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.list_item_order_detail, null);
			OrderDetailBean data = listData.get(position);
			
			final TextView textKey = (TextView) convertView.findViewById(R.id.key_txt);
			textKey.setText(data.key);
			final TextView contentText = (TextView) convertView.findViewById(R.id.content_txt);
			contentText.setText(data.content);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					switch (position) {
					case 0://选择商家
						Intent intents = new Intent(OrderDetailActivity.this, FavShopActivity.class);
						startActivityForResult(intents, 1);
						break;
					/*case 1://选择地址
						Intent intent = new Intent(OrderDetailActivity.this, AddressSelectActivity.class);
						startActivityForResult(intent, 0);
						break;*/
					case 1:
						mTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {  
					        @Override  
					        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					        	String hourS;
					        	String minuteS;
					        	if(hourOfDay < 10) {
					        		hourS = "0" + String.valueOf(hourOfDay);
					        	} else {
					        		hourS = String.valueOf(hourOfDay);
					        	}
					        	if(minute < 10) {
					        		minuteS = "0" + String.valueOf(minute);
					        	} else {
					        		minuteS = String.valueOf(minute);
					        	}
					        	Date cdate = new Date();
					        	startDate = String.valueOf(new Date(cdate.getYear(), cdate.getMonth(), cdate.getDay(), hourOfDay, minute).getTime() / 1000);
					        	OrderDetailBean data = listData.get(1);
					        	data.content = hourS + ":" + minuteS;
					        	adapter.notifyDataSetChanged();
					        }  
					    };
						showDialog(START_TIME_DIALOG_ID);
						break;
					case 2:
						mTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {  
					        @Override  
					        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					        	String hourS;
					        	String minuteS;
					        	if(hourOfDay < 10) {
					        		hourS = "0" + String.valueOf(hourOfDay);
					        	} else {
					        		hourS = String.valueOf(hourOfDay);
					        	}
					        	if(minute < 10) {
					        		minuteS = "0" + String.valueOf(minute);
					        	} else {
					        		minuteS = String.valueOf(minute);
					        	}
					        	Date cdate = new Date();
					        	endDate = String.valueOf(new Date(cdate.getYear(), cdate.getMonth(), cdate.getDay(), hourOfDay, minute).getTime() / 1000);
					        	OrderDetailBean data = listData.get(2);
					        	data.content = hourS + ":" + minuteS;
					        	adapter.notifyDataSetChanged();
					        }
					    }; 
						showDialog(END_TIME_DIALOG_ID);
						break;
					default:
						break;
					}
				}
			});
			return convertView;
		} 
		
	}
	
	
	private class SendOrderTask extends RGenericTask<SendReturnBean> {

		public SendOrderTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected SendReturnBean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			String lat = "";
			String lon = "";
			if(UserApplication.buyLocation != null) {
				lat = String.valueOf(UserApplication.buyLocation.latitude);
				lon = String.valueOf(UserApplication.buyLocation.longitude);
			}
			UserApplication.shop.addX = lat;
			UserApplication.shop.addY = lon;
			UserApplication.shop.sid = shopId;
			UserApplication.shop.startTime = startDate;
			UserApplication.shop.endTime = endDate;
			UserApplication.shop.audioName = audioName;
			UserApplication.shop.address = addressStr;
			return api.sendOrder(lat,lon,shopId,startDate,endDate,audioName,addressStr,String.valueOf(length));
		}

		@Override
		protected void onSuccess(SendReturnBean result) {
			if(result != null) {
				FruitPerference.saveOrderList(ctx, "" + result.oId);
				Intent intent = new Intent(OrderDetailActivity.this, SuccessActivity.class);
				intent.putExtra("data", result);
				startActivity(intent);
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(OrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
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
	
	private class QiniuTokenTask extends RGenericTask<String> {
		private boolean toast;
		public QiniuTokenTask(Context ctx, boolean toast) {
			super(ctx);
			this.toast = toast;
		}

		@Override
		protected String getContent() throws HttpException {
			FruitApi api = new FruitApi(OrderDetailActivity.this);
			return api.uploadToken(FruitPerference.getUserMobile(ctx));
		}

		@Override
		protected void onSuccess(String result) {
			if(TextUtils.isEmpty(result)) {
				if(toast) {
					Toast.makeText(OrderDetailActivity.this, "上传失败，请退出应用重试", Toast.LENGTH_SHORT).show();
					hideProgressBar();
				}
				return;
			}
			UserApplication.qiniuToken = result;
			QiNiuUtils utils = QiNiuUtils.getInstance(OrderDetailActivity.this);
			String newKey = key.substring(0, key.indexOf("."));
			utils.doUpload(uri, key, new QiniuCallback() {
				
				@Override
				public void onSuccess(JSONObject resp) {
					audioName = resp.optString("key");
					isQiniuSuccess = true;
					if(toast) {
						SendOrderTask task = new SendOrderTask(OrderDetailActivity.this);
						task.execute();
					}
				}
				@Override
				public void onProcess(long current, long total) {}
				@Override
				public void onFailure(Exception ex) {
					if(toast) {
						Toast.makeText(OrderDetailActivity.this, "上传失败，请退出应用重试", Toast.LENGTH_SHORT).show();
						hideProgressBar();
					}
				}
			});
		}

		@Override
		protected void onAnyError(int code, String msg) {
			if(toast) {
				Toast.makeText(OrderDetailActivity.this, "上传失败，请退出应用重试", Toast.LENGTH_SHORT).show();
				hideProgressBar();
			}
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
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float range = event.values[0];
	    if (range == mSensor.getMaximumRange()) {
	         audioManager.setMode(AudioManager.MODE_NORMAL);
	    } else {
	         audioManager.setMode(AudioManager.MODE_IN_CALL);
	    }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
