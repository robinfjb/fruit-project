package com.robin.fruituser.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cn.sgone.fruituser.R;
import com.robin.fruituser.activity.TradeActivity;

public class ResendDialog extends Dialog implements View.OnClickListener{
	private Button resendBtn;

	public ResendDialog(Context context) {
		super(context, R.style.FullHeightDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_dialog_cancel);
		resendBtn = (Button) findViewById(R.id.resend_btn);
		resendBtn.setOnClickListener(this);
		ImageView closeImg = (ImageView) findViewById(R.id.close);
		closeImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.resend_btn:
			doResend();
			this.dismiss();
			break;
		case R.id.close:
			this.dismiss();
			break;

		default:
			break;
		}
	}
	
	private void doResend() {
		Intent intent = new Intent(getContext(), TradeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		getContext().startActivity(intent);
		/*SendOrderTask task = new SendOrderTask(getContext());
		task.execute();*/
	}
	
	/*private class SendOrderTask extends RGenericTask<SendReturnBean> {

		public SendOrderTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected SendReturnBean getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.sendOrder(UserApplication.shop.addX,UserApplication.shop.addY,
					UserApplication.shop.sid,UserApplication.shop.startTime,
					UserApplication.shop.endTime,UserApplication.shop.audioName,
					UserApplication.shop.address);
		}

		@Override
		protected void onSuccess(SendReturnBean result) {
			if(result != null) {
				Toast.makeText(getContext(), "订单已重新发送", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onTaskBegin() {}

		@Override
		protected void onTaskFinished() {
		}
		
	}*/
}
