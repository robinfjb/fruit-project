package com.robin.fruituser.adapter;

import java.util.List;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruituser.view.FragmentOrder;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @ClassName: MallLietAdapter
 * @Description: �̳Ƿ��� viewpage adapter
 * @author shixiang
 * @date 2013-7-11 ����10:17:30
 * 
 */
public class MallLietAdapter extends FragmentPagerAdapter {
	/**
	 * @Fields tabs : ��ǩ��
	 */
	private List<String> tabs;
	/**
	 * @Fields mallHotFragment :�����̼�
	 */
	private FragmentOrder mallHotFragment;
	/**
	 * @Fields mallTraFragment : �����̼�
	 */
	private FragmentOrder mallTraFragment;


	public MallLietAdapter(FragmentManager fm, List<String> tabs) {
		super(fm);
		this.tabs = tabs;
	}
	
	@Override
	public FragmentOrder getItem(int arg0) {
		if (arg0 == 0) {
			mallHotFragment = new FragmentOrder();
			mallHotFragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent().putExtra(FragmentOrder.FLAG, 0)));
			return mallHotFragment;
		} else {
			mallTraFragment = new FragmentOrder();
			mallTraFragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent().putExtra(FragmentOrder.FLAG, 1)));
			return mallTraFragment;
		}
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabs.get(position);
	}
}
