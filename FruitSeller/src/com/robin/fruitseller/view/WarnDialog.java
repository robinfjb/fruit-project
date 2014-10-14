package com.robin.fruitseller.view;

import cn.sgone.fruitseller.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class WarnDialog extends Dialog implements OnClickListener{
	
	public WarnDialog(Context context) {
		super(context, R.style.FullHeightDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_dialog_warn);
		ImageView closeImg = (ImageView) findViewById(R.id.close);
		closeImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.close:
			this.dismiss();
			break;
		default:
			break;
		}
	}
}
