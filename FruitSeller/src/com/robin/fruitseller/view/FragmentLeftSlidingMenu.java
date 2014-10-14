package com.robin.fruitseller.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robin.fruitlib.base.BaseFragment;
import com.robin.fruitlib.loader.implement.FruitBitmapHandler;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruitseller.R;
import com.robin.fruitseller.SellerApplication;
import com.robin.fruitseller.activity.AccountActivity;
import com.robin.fruitseller.activity.AnnounceActivity;
import com.robin.fruitseller.activity.DealActivity;
import com.robin.fruitseller.activity.HelpActivity;
import com.robin.fruitseller.activity.LoginActivity;
import com.robin.fruitseller.activity.MoneyActivity;
import com.robin.fruitseller.activity.SettingActivity;
import com.robin.fruitseller.activity.TradeInfoActivity;
import com.umeng.fb.ConversationActivity;

public class FragmentLeftSlidingMenu extends BaseFragment implements OnClickListener{
	private View UserInfoView;
	private View dealView;
	private View accountView;
	private View announceView;
	private View helpView;
	private ImageView UserIconView;
	private ImageView ewmView;
	private TextView productBtn;
	private LinearLayout settingBtn;
     @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mContext = getActivity();
    	
    }
     
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	 View view = inflater.inflate(R.layout.fragment_slide_menu, container,false);
    	 UserIconView = (ImageView) view.findViewById(R.id.headImageView);
    	 UserIconView.setOnClickListener(this);
    	 UserInfoView = view.findViewById(R.id.userInfoArea);
    	 UserInfoView.setOnClickListener(this);
    	 dealView = view.findViewById(R.id.dealLayout);
    	 dealView.setOnClickListener(this);
    	 announceView = view.findViewById(R.id.announceLayout);
    	 announceView.setOnClickListener(this);
    	 accountView = view.findViewById(R.id.accountLayout);
    	 accountView.setOnClickListener(this);
    	 helpView = view.findViewById(R.id.helpLayout);
    	 helpView.setOnClickListener(this);
    	 productBtn = (TextView) view.findViewById(R.id.btn_product);
    	 productBtn.setOnClickListener(this);
    	 settingBtn = (LinearLayout) view.findViewById(R.id.btn_setting);
    	 settingBtn.setOnClickListener(this);
    	 ewmView = (ImageView) view.findViewById(R.id.account_ewm);
    	 
    	return view;
    }

	private void initData() {
		FruitBitmapHandler handler = new FruitBitmapHandler(getActivity());
		handler.displayImage(UserIconView, SellerApplication.picUrl1);
		if(TextUtils.isEmpty(SellerApplication.picUrl3)) {
			ewmView.setVisibility(View.GONE);
		} else {
			ewmView.setVisibility(View.VISIBLE);
		}
	}
	
	

	@Override
	public void onResume() {
		initData();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		switch (v.getId()) {
		case R.id.headImageView:
		case R.id.userInfoArea:
			if(!Utils.isSellerLogin(mContext)) {
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
			} else {
				startActivity(new Intent(mContext, AccountActivity.class));
				((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			}
			break;
		case R.id.announceLayout:
			startActivity(new Intent(mContext, AnnounceActivity.class));
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		    break;
		case R.id.accountLayout:
			if(!Utils.isSellerLogin(mContext)) {
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
			} else {
				startActivity(new Intent(mContext, MoneyActivity.class));
				((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			}
		    break;
		case R.id.dealLayout:
			if(!Utils.isSellerLogin(mContext)) {
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
			} else {
				startActivity(new Intent(mContext, TradeInfoActivity.class));
				((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			}
		    break;
		case R.id.helpLayout:
			startActivity(new Intent(mContext, HelpActivity.class));
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.btn_product:
			Intent intent = new Intent(getActivity(), ConversationActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.btn_setting:
			startActivity(new Intent(mContext, SettingActivity.class));
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		default:
			break;
		}	
	}
}
