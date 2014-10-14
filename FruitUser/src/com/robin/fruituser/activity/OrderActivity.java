package com.robin.fruituser.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robin.fruitlib.base.BaseActivity;
import cn.sgone.fruituser.R;
import com.robin.fruituser.adapter.MallLietAdapter;

public class OrderActivity extends BaseActivity {
	private ViewPager mViewPager;
	private MallLietAdapter mallLietAdapter;
	// test
	private ImageView ivBottomLine;
	private TextView tv_tab_hot, tv_tab_tg;
	private int currIndex = 0;
	private int position_one;
	private Resources resources;
	public static final String CATEGORY="category";
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fragment_order);
		resources = getResources();
		InitWidth();
		InitTextView();
		initView();
		initTab();
	}
	
	/**
	 * @Title: initView
	 * @Description: 控件初始化
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitTextView() {
		tv_tab_hot = (TextView) findViewById(R.id.tv_tab_all);
		tv_tab_tg = (TextView) findViewById(R.id.tv_tab_dis);
		tv_tab_hot.setOnClickListener(new MyOnClickListener(0));
		tv_tab_tg.setOnClickListener(new MyOnClickListener(1));
	}
	
	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		position_one = (int) (screenW / 3.0);
	}

	/**
	 * @Title: initData
	 * @Description: 数据初始化
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void initTab() {
		List<String> tabs = new ArrayList<String>();
		tabs.add("即买订单");
		tabs.add("团购订单");
		mallLietAdapter = new MallLietAdapter(getSupportFragmentManager(), tabs);
		mViewPager.setAdapter(mallLietAdapter);
		// viewpage 预加载
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			mViewPager.setOffscreenPageLimit(2);
		}
		// 设置默认选择页
		int index = 0;
		Intent intent = getIntent();
		if (intent != null) {
			try {
				index = intent.getIntExtra(CATEGORY,0);//getIntent().getExtras()
			} catch (Exception e) {
			}
		}
		switch (index) {
		case 0:
			mViewPager.setCurrentItem(0);
			ivBottomLine.setImageResource(R.drawable.brand_tap_bar_select_color);
			break;
		case 1:
			mViewPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}
	
	/**
	 * @ClassName: MyOnClickListener
	 * @Description: title点击监听
	 * @author xiang.shi
	 * @date 2013-8-13 下午6:34:17
	 * 
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	};
	
	/**
	 * @ClassName: MyOnPageChangeListener
	 * @Description: viewpage切换监听
	 * @author xiang.shi
	 * @date 2013-8-13 下午6:32:06
	 * 
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					tv_tab_tg.setTextColor(resources.getColor(R.color.bg_tab_text));
				}
				tv_tab_hot.setTextColor(resources.getColor(R.color.bg_tab_text_selected));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_one, 0, 0);
					tv_tab_hot.setTextColor(resources.getColor(R.color.bg_tab_text));
				}
				tv_tab_tg.setTextColor(resources.getColor(R.color.bg_tab_text_selected));
				break;
			default://case 0
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					tv_tab_tg.setTextColor(resources.getColor(R.color.bg_tab_text));
				}
				tv_tab_hot.setTextColor(resources.getColor(R.color.bg_tab_text_selected));
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					ivBottomLine.setImageResource(R.drawable.brand_tap_bar_select_color);
				}
			});
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
}
