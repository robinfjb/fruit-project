package com.robin.fruituser.view;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.robin.fruitlib.base.BaseActivity;
import com.robin.fruitlib.base.BaseFragment;
import com.robin.fruitlib.bean.ListData;
import com.robin.fruitlib.bean.OrderBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.task.RGenericTask;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruituser.R;
import com.robin.fruituser.activity.LoginActivity;
import com.robin.fruituser.adapter.SimpleThumbAdapter;
import com.umeng.analytics.MobclickAgent;

public class FragmentOrder extends BaseFragment{
	private ViewGroup mRootContainer;
	public static final String FLAG = "flag";
	/**
	 * @Fields gridlist : 显示商城list的gv
	 */
	private GridView gridlist;
	/**
	 * @Fields mHotMerchant : 热门商城的adapter
	 */
	private SimpleThumbAdapter mHotMerchant;
	private String mUrl;
	private String mFanli;
	private int mIsWap;
	private ArrayList<OrderBean> mHotShop;
	private View mProgressBar;
	private View emptyMsg;
	private OrderBean shopBean;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootContainer = (ViewGroup) inflater.inflate(R.layout.fragment_order_page, null);
		Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		initView();
		requestData();
		return mRootContainer;
	}

	/**
	 * @Title: initView
	 * @Description: 初始化
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void initView() {
		gridlist = (GridView) mRootContainer.findViewById(R.id.gridlist);
		mRootContainer.findViewById(R.id.emptyView).setVisibility(View.GONE);
		gridlist.setNumColumns(2);
		mHotMerchant = new SimpleThumbAdapter(getActivity(),new ArrayList<OrderBean>());
		mProgressBar = mRootContainer.findViewById(R.id.listLoadingBar);
		gridlist.setAdapter(mHotMerchant);
		// 隐藏系统默认选择效果
		gridlist.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (position >= mHotMerchant.getCount())
					return;
				shopBean = mHotMerchant.getItem(position);
				// TODO
			}
		});
	}
	
	private void requestData() {
		GetOrderTask task = new GetOrderTask(mContext);
		task.execute();
	}

	private class GetOrderTask extends RGenericTask<ListData<OrderBean>> {

		public GetOrderTask(Context ctx) {
			super(ctx);
		}

		@Override
		protected ListData<OrderBean> getContent() throws HttpException {
			return null;
		}

		@Override
		protected void onSuccess(ListData<OrderBean> taobaoCatalogs) {
			mHotMerchant.clear();
			mHotMerchant.append(mHotShop);
		}

		@Override
		protected void onAnyError(int code, String msg) {
		}

		@Override
		protected void onTaskBegin() {
		}

		@Override
		protected void onTaskFinished() {
			mProgressBar.setVisibility(View.GONE);
		}
	}
}
