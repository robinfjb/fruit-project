package com.robin.fruituser.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robin.fruitlib.base.BaseFragment;
import com.robin.fruitlib.bean.ShareBean;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.util.SocialUtils;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruituser.R;
import com.robin.fruituser.UserApplication;
import com.robin.fruituser.activity.AccountActivity;
import com.robin.fruituser.activity.HelpActivity;
import com.robin.fruituser.activity.HomeActivity;
import com.robin.fruituser.activity.LoginActivity;
import com.robin.fruituser.activity.MonthRecommendActivity;
import com.robin.fruituser.activity.NeighborRecommendActivity;
import com.robin.fruituser.activity.SettingActivity;
import com.robin.fruituser.activity.TradeActivity;
import com.umeng.fb.ConversationActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

public class FragmentLeftSlidingMenu extends BaseFragment implements OnClickListener{
	private View UserInfoView;
	private View neighbourView;
	private View monthRecommadView;
	private View dealView;
	private View shareView;
	private View helpView;
	private ImageView UserIconView;
	private ImageView imgRedDot;
	private TextView productBtn;
	private LinearLayout settingBtn;
	private TextView phoneTxt;
     @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mContext = getActivity();
    }
     
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	 View view = inflater.inflate(R.layout.fragment_slide_menu, container,false);
    	 UserIconView = (ImageView) view.findViewById(R.id.headImageView);
    	 UserIconView.setOnClickListener(this);
    	 UserInfoView = view.findViewById(R.id.userInfoArea);
    	 UserInfoView.setOnClickListener(this);
    	 neighbourView = view.findViewById(R.id.neighbourLayout);
    	 neighbourView.setOnClickListener(this);
    	 monthRecommadView = view.findViewById(R.id.monthrecLayout);
    	 monthRecommadView.setOnClickListener(this);
    	 dealView = view.findViewById(R.id.dealLayout);
    	 dealView.setOnClickListener(this);
    	 shareView = view.findViewById(R.id.shareLayout);
    	 shareView.setOnClickListener(this);
    	 helpView = view.findViewById(R.id.helpLayout);
    	 helpView.setOnClickListener(this);
    	 productBtn = (TextView) view.findViewById(R.id.btn_product);
    	 productBtn.setOnClickListener(this);
    	 settingBtn = (LinearLayout) view.findViewById(R.id.btn_setting);
    	 settingBtn.setOnClickListener(this);
    	 phoneTxt = (TextView) view.findViewById(R.id.fav_address);
    	 imgRedDot = (ImageView)view.findViewById(R.id.titleRedPoint);
    	 
    	 phoneTxt.setText(FruitPerference.getUserMobile(mContext));
 		 String time = FruitPerference.getDeviceTimeStamp(mContext);
 		 if(!time.equals(UserApplication.deviceBean.timeStamp)) {
 		 	imgRedDot.setVisibility(View.VISIBLE);
 		 } else {
 			imgRedDot.setVisibility(View.GONE);
 		 }
    	 return view;
    }

     

	@Override
	public void onResume() {
		phoneTxt.setText(FruitPerference.getUserMobile(mContext));
		 String time = FruitPerference.getDeviceTimeStamp(mContext);
		 if(!time.equals(UserApplication.deviceBean.timeStamp)) {
		 	imgRedDot.setVisibility(View.VISIBLE);
		 } else {
			imgRedDot.setVisibility(View.GONE);
		 }
		 if(Utils.isUserLogin(mContext)) {
			 UserIconView.setImageResource(R.drawable.meunheader);
		 } else {
			 UserIconView.setImageResource(R.drawable.meunheader_disable);
		 }
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		switch (v.getId()) {
		case R.id.headImageView:
		case R.id.userInfoArea:
			if(!Utils.isUserLogin(mContext)) {
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
			} else {
				startActivity(new Intent(mContext, AccountActivity.class));
				((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			}
			break;
		case R.id.neighbourLayout:
			startActivity(new Intent(mContext, NeighborRecommendActivity.class));
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		    break;
		case R.id.monthrecLayout:
			startActivity(new Intent(mContext, MonthRecommendActivity.class));
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		    break;
		case R.id.dealLayout:
			if(!Utils.isUserLogin(mContext)) {
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, RESULT_CODE_LOGIN);
			} else {
				if(imgRedDot.getVisibility() == View.VISIBLE) {
					imgRedDot.setVisibility(View.GONE);
					FruitPerference.saveDeviceTimeStamp(mContext, UserApplication.deviceBean.timeStamp);
				}
				startActivity(new Intent(mContext, TradeActivity.class));
				((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			}
		    break;
		case R.id.shareLayout:
			ShareBean data = new ShareBean();
			data.text = getString(R.string.share_content);
			data.title = getString(R.string.share_title);
			data.url = "http://www.sgone.cn/download.html";
			shareSDK(getActivity(), data);
			break;
		case R.id.helpLayout:
			startActivity(new Intent(mContext, HelpActivity.class));
			((Activity) mContext).overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			break;
		case R.id.btn_product:
//			UserApplication.agent.startFeedbackActivity();
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
	
	private void shareSDK(Activity context, ShareBean data) {
		final SocialUtils shareInstance = new SocialUtils(context);
		SnsPostListener listener = new SnsPostListener() {
			@Override
			public void onStart() {
			}
			
			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				int stats = 4;
				switch (arg1) {
				case StatusCode.ST_CODE_SUCCESSED:
					stats = 0;
					break;
				case StatusCode.ST_CODE_NO_AUTH:
				case StatusCode.ST_CODE_SDK_NO_OAUTH:
					stats = 2;
					break;
				case -2:
					stats = 1;
					break;
				case StatusCode.ST_CODE_ERROR:
					stats = 3;
					break;
				}
			}
		};

		shareInstance.openShareSDK(data,  listener);
	}
}
