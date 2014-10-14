package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.bean.OrderDetailBean;
import com.robin.fruitlib.bean.TradeBean;
import com.robin.fruitlib.bean.TradeDetailBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.AudioRecorderHandler;
import com.robin.fruitlib.util.TimeUtil;

import cn.sgone.fruituser.R;

public class OrderSuccessActivity extends BaseActivity implements OnClickListener{
	private ImageView backImg;
	private ImageView voicePlay;
	private Button confirmBtn;
	private ListView mList;
	private ListAdapter adapter;
	private List<OrderDetailBean> listData = new ArrayList<OrderDetailBean>();
	private static final int START_TIME_DIALOG_ID = 0;
	private static final int END_TIME_DIALOG_ID = 1;
	private SpannableString sps;
	private int index;
	private TextView audioLengthTxt;
	TradeBean data;
	TradeDetailBean bean;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_success);
		backImg = (ImageView) findViewById(R.id.ivTitleBtnLeft);
		backImg.setOnClickListener(this);
		voicePlay = (ImageView) findViewById(R.id.btn_voice_play);
		voicePlay.setOnClickListener(this);
		confirmBtn = (Button) findViewById(R.id.confirm);
		confirmBtn.setOnClickListener(this);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.warn_area);
		Intent intent = getIntent();
		data = (TradeBean) intent.getSerializableExtra("data");
		index = intent.getIntExtra("index", -1);
		if(data != null && (data.stats.equals("2"))) {
			layout.setVisibility(View.VISIBLE);
			confirmBtn.setVisibility(View.VISIBLE);
		} else {
			layout.setVisibility(View.GONE);
			confirmBtn.setVisibility(View.GONE);
		}
		sps = new SpannableString("请您在收货时检查商家送来的水果是否符合您的要求再进行结算！如有问题可以拒绝收货");
		sps.setSpan(new ForegroundColorSpan(Color.BLUE), sps.length()-4, sps.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		((TextView)findViewById(R.id.warn_str)).setText(sps);
		((TextView)findViewById(R.id.warn_str)).setOnClickListener(this);
		audioLengthTxt =  (TextView) findViewById(R.id.voice_length);
		int length = 0;
		try{
			length = Integer.parseInt(data.audioLength);
		}catch(NumberFormatException e){
		}
		if(length > 0){
			audioLengthTxt.setText(length + "\"");
		}
		mList = (ListView) findViewById(R.id.list);
		adapter = new ListAdapter();
		mList.setAdapter(adapter);
		initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void initData() {
		GetOrderTask task = new GetOrderTask(OrderSuccessActivity.this, data.oId);
		task.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.btn_voice_play:
			playSound(bean.audioUrl + "/" + bean.audioName);
			break;
		case R.id.confirm:
			SendOrderTask task = new SendOrderTask(OrderSuccessActivity.this, data.oId);
			task.execute();
			break;
		case R.id.warn_str:
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("确定要拒绝收货？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							RefuseOrderTask task = new RefuseOrderTask(OrderSuccessActivity.this, data.oId);
							task.execute();
						}
					});
			builder.setNegativeButton("点错了",null);
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		}	
	}
	
	private void playSound(String url) {
		AudioRecorderHandler handler = AudioRecorderHandler.getInstance(OrderSuccessActivity.this);
		handler.play(url);
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
		if(arg1 == RESULT_OK) {
			String address = arg2.getStringExtra("address");
			OrderDetailBean data = listData.get(0);
			data.content = address;
			adapter.notifyDataSetChanged();
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
			convertView = LayoutInflater.from(OrderSuccessActivity.this).inflate(R.layout.list_item_order_success, null);
			OrderDetailBean data = listData.get(position);
			
			final TextView textKey = (TextView) convertView.findViewById(R.id.key_txt);
			textKey.setText(data.key);
			final TextView contentText = (TextView) convertView.findViewById(R.id.content_txt);
			contentText.setText(data.content);
			return convertView;
		} 
		
	}
	
	private class SendOrderTask extends RGenericTask<Boolean> {
		private String oId;
		public SendOrderTask(Context ctx, String oId) {
			super(ctx);
			this.oId = oId;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.confirmOrder(oId);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				Intent intent = new Intent();
				intent.putExtra("index", index);
				intent.putExtra("type", "confirm");
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(OrderSuccessActivity.this, "确认收货失败", Toast.LENGTH_SHORT).show();
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
	
	private class RefuseOrderTask extends RGenericTask<Boolean> {
		private String oId;
		public RefuseOrderTask(Context ctx, String oId) {
			super(ctx);
			this.oId = oId;
		}

		@Override
		protected Boolean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.refuseOrder(oId);
		}

		@Override
		protected void onSuccess(Boolean result) {
			if(result) {
				Intent intent = new Intent();
				intent.putExtra("index", index);
				intent.putExtra("type", "refuse");
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(OrderSuccessActivity.this, "拒绝收货失败", Toast.LENGTH_SHORT).show();
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
			bean = result;
			OrderDetailBean beanAddress = new OrderDetailBean();
			beanAddress.key = "接单商家";
			beanAddress.content = result.shopName;
			listData.add(beanAddress);
			OrderDetailBean beanStartTime = new OrderDetailBean();
			beanStartTime.key = "送货时间";
			beanStartTime.content = TimeUtil.timeStamp2DateOnlyHour(data.expressStart) 
					+ "-" + TimeUtil.timeStamp2DateOnlyHour(data.expressEnd);
			listData.add(beanStartTime);
			OrderDetailBean beanEndTime = new OrderDetailBean();
			beanEndTime.key = "商家地址";
			beanEndTime.content = result.address;
			listData.add(beanEndTime);
			adapter.notifyDataSetChanged();
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
}
