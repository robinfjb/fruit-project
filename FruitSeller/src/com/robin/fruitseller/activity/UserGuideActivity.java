package com.robin.fruitseller.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruitseller.R;

public class UserGuideActivity extends BaseActivity implements OnPageChangeListener{
	private int imageId[] = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3, R.drawable.guide_4, R.drawable.guide_5};
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private Intent mainIntent;
	private Button startBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainIntent = new Intent(UserGuideActivity.this, HomeActivity.class);
		int version = Utils.getVersion(UserGuideActivity.this);
		if (version != 0) {
			if (FruitPerference.isFirstSeller(UserGuideActivity.this) == version) {
				startActivity(mainIntent);
				UserGuideActivity.this.finish();
				return;
			}
		}
		
		setContentView(R.layout.activity_user_guide);
		views = new ArrayList<View>();
		startBtn = (Button) findViewById(R.id.start_btn);
		FruitPerference.updateFirstSeller(this, version);
		
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < imageId.length; i++) {
			View iv = new View(this);
			iv.setLayoutParams(mParams);
			iv.setBackgroundResource(imageId[i]);
			views.add(iv);
		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
	}
	
	private class ViewPagerAdapter extends PagerAdapter {
		private List<View> views;
		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}
	
	int stats;
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		stats = arg0;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		int lastReal = vp.getAdapter().getCount() - 1;
		if (arg0 >= lastReal) {
			startBtn.setVisibility(View.VISIBLE);
			startBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(mainIntent);
					UserGuideActivity.this.finish();
				}
			});
		} else {
			startBtn.setVisibility(View.GONE);
			startBtn.setOnClickListener(null);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			UserGuideActivity.this.finish();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
