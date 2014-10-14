package com.robin.fruitseller;

import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.data.FruitPerference;

public class SellerApplication extends BaseApplication{
	public static String picUrl1 = ""; //ͷ��
	public static String picUrl2 = ""; //ִ��
	public static String picUrl3 = ""; //ִ��
	public static String status = "";
	public static String oId = "";
	 @Override
	    public void onCreate() {
			SellerApplication.picUrl1 = FruitPerference.getShoperImage(this);
			SellerApplication.picUrl2 = FruitPerference.getZhizhaoImage(this);
			SellerApplication.picUrl3 = FruitPerference.getEwmImage(this);
			String[] strs = FruitPerference.getBaiduUid(this);
			BaseApplication.userId = strs[0];
			BaseApplication.channelId = strs[1];
	        super.onCreate();
	    }
}
