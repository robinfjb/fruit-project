package com.robin.fruitlib.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.robin.fruitlib.bean.UserData;

public class FruitPerference {
	private static final String SP_APP_DATA = "com.robin.android.appdata";
	private static final String SP_USER_DATA = "com.robin.android.userdata";
	private static final String SP_USER_LOGIN_DATA = "com.robin.android.login.userdata";
	private static final String SP_FAV_DATA = "com.robin.android.favdata";
	
	private static final String APP_TIME_STAMP = "app_time_stamp";
	private static final String NEW_FIRST_USE_FLAG = "new_first_use_flag";

	private static final String USE_MOBILE_FLAG = "user_mobile_flag";
	private static final String FAV_DATA_HOME = "fav_home";
	private static final String FAV_DATA_COMPANY = "fav_company";
	private static final String FAV_DATA_FAV = "fav_fav";
	private static final String FAV_DATA_HOME_LONG = "fav_home_long";
	private static final String FAV_DATA_COMPANY_LONG = "fav_company_long";
	private static final String FAV_DATA_FAV_LONG = "fav_fav_long";
	private static final String FAV_DATA_HOME_LAT = "fav_home_lat";
	private static final String FAV_DATA_COMPANY_LAT = "fav_company_lat";
	private static final String FAV_DATA_FAV_LAT = "fav_fav_lat";
	
	private static final String USER_DATA_CID = "user_data_cid";
	private static final String USER_DATA_TOKEN = "user_data_token";
	private static final String USER_DATA_ORDER_NUM = "user_data_order_num";
	private static final String USER_DATA_CANCEL_NUM = "user_data_cancel_num";
	private static final String USER_DATA_REFUSE_NUM = "user_data_refuse_num";
	private static final String USER_DATA_DEVICE_TYPE = "user_data_device_type";
	private static final String USER_DATA_DEVICE_TOKEN = "user_data_device_token";
	private static final String USER_DATA_CHANNEL_ID = "user_data_channel_id";
	
	public static int isFirstUse(Context context) {
		final SharedPreferences storage = context.getSharedPreferences(SP_APP_DATA, Context.MODE_PRIVATE);
		return storage.getInt(NEW_FIRST_USE_FLAG, 0);
	}
	
	public static void updateFirstUse(Context context, int version) {
		final SharedPreferences storage = context.getSharedPreferences(SP_APP_DATA, Context.MODE_PRIVATE);
		storage.edit().putInt(NEW_FIRST_USE_FLAG, version).commit();
	}
	
	public static String getDeviceTimeStamp(Context context) {
		final SharedPreferences storage = context.getSharedPreferences(SP_APP_DATA, Context.MODE_PRIVATE);
		return storage.getString(APP_TIME_STAMP, "");
	}
	
	public static void saveDeviceTimeStamp(Context context, String time) {
		final SharedPreferences storage = context.getSharedPreferences(SP_APP_DATA, Context.MODE_PRIVATE);
		storage.edit().putString(APP_TIME_STAMP, time).commit();
	}
	
	public static void saveUserData(Context context, UserData data){
		final SharedPreferences storage = context.getSharedPreferences(SP_USER_LOGIN_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(USER_DATA_CID, data.cId);
		editor.putString(USER_DATA_TOKEN, data.uToken);
		editor.putString(USER_DATA_ORDER_NUM, data.orderNum);
		editor.putString(USER_DATA_CANCEL_NUM, data.cancelNum);
		editor.putString(USER_DATA_REFUSE_NUM, data.refuseNum);
		editor.putString(USER_DATA_DEVICE_TYPE, data.deviceType);
		editor.putString(USER_DATA_DEVICE_TOKEN, data.deviceToken);
		editor.putString(USER_DATA_CHANNEL_ID, data.channelId);
		editor.commit();
	}
	
	public static UserData getUserData(Context context) {
		UserData data = new UserData();
		final SharedPreferences storage = context.getSharedPreferences(SP_USER_LOGIN_DATA, Context.MODE_PRIVATE);
		data.cId = storage.getString(USER_DATA_CID, "");
		data.uToken = storage.getString(USER_DATA_TOKEN, "");
		data.orderNum = storage.getString(USER_DATA_ORDER_NUM, "");
		data.cancelNum = storage.getString(USER_DATA_CANCEL_NUM, "");
		data.refuseNum = storage.getString(USER_DATA_REFUSE_NUM, "");
		data.deviceType = storage.getString(USER_DATA_DEVICE_TYPE, "");
		data.deviceToken = storage.getString(USER_DATA_DEVICE_TOKEN, "");
		data.channelId = storage.getString(USER_DATA_CHANNEL_ID, "");
		return data;
	}
	
	public static void saveUserMobile(Context context, String phone){
		final SharedPreferences storage = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(USE_MOBILE_FLAG, phone);
		editor.commit();
	}
	
	public static String getUserMobile(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(USE_MOBILE_FLAG, "");
	}
	
	public static void removeUserMobile(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		oAuthPref.edit().remove(USE_MOBILE_FLAG).commit();
	}
	
	public static void saveHomeAddress(Context context,String home, double lat, double longt) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		Editor editor = oAuthPref.edit();
		editor.putString(FAV_DATA_HOME, home);
		editor.putString(FAV_DATA_HOME_LONG, String.valueOf(longt));
		editor.putString(FAV_DATA_HOME_LAT, String.valueOf(lat));
		editor.commit();
	}
	
	public static String getHomeAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(FAV_DATA_HOME, "");
	}
	
	/**
	 * lat,longt
	 * @param context
	 * @return
	 */
	public static double[] getHomeAddressLatLong(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		String longT = oAuthPref.getString(FAV_DATA_HOME_LONG, "");
		String lat = oAuthPref.getString(FAV_DATA_HOME_LAT, "");
		try {
			return new double[]{Double.parseDouble(lat),Double.parseDouble(longT)};
		} catch (Exception e) {
		}
		return null;
	}
	
	public static void removeHomeAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		oAuthPref.edit().remove(FAV_DATA_HOME).commit();
		oAuthPref.edit().remove(FAV_DATA_HOME_LONG).commit();
		oAuthPref.edit().remove(FAV_DATA_HOME_LAT).commit();
	}
	
	public static void saveCompanyAddress(Context context,String company, double lat, double longt) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		Editor editor = oAuthPref.edit();
		editor.putString(FAV_DATA_COMPANY, company);
		editor.putString(FAV_DATA_COMPANY_LONG, String.valueOf(longt));
		editor.putString(FAV_DATA_COMPANY_LAT, String.valueOf(lat));
		editor.commit();
	}
	
	public static String getCompanyAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(FAV_DATA_COMPANY, "");
	}
	
	/**
	 * lat,longt
	 * @param context
	 * @return
	 */
	public static double[] getCompanyAddressLatLong(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		String longT = oAuthPref.getString(FAV_DATA_COMPANY_LONG, "");
		String lat = oAuthPref.getString(FAV_DATA_COMPANY_LAT, "");
		try {
			return new double[]{Double.parseDouble(lat),Double.parseDouble(longT)};
		} catch (Exception e) {
		}
		return null;
	}
	
	public static void removeCompanyAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		oAuthPref.edit().remove(FAV_DATA_COMPANY).commit();
		oAuthPref.edit().remove(FAV_DATA_COMPANY_LAT).commit();
		oAuthPref.edit().remove(FAV_DATA_COMPANY_LONG).commit();
	}
	
	public static void saveFavAddress(Context context,String fav, double lat, double longt) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		Editor editor = oAuthPref.edit();
		editor.putString(FAV_DATA_FAV, fav);
		editor.putString(FAV_DATA_FAV_LONG, String.valueOf(longt));
		editor.putString(FAV_DATA_FAV_LAT, String.valueOf(lat));
		editor.commit();
	}
	
	public static String getFavAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(FAV_DATA_FAV, "");
	}
	
	/**
	 * lat,longt
	 * @param context
	 * @return
	 */
	public static double[] getFavAddressLatLong(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		String longT = oAuthPref.getString(FAV_DATA_FAV_LONG, "");
		String lat = oAuthPref.getString(FAV_DATA_FAV_LAT, "");
		try {
			return new double[]{Double.parseDouble(lat),Double.parseDouble(longT)};
		} catch (Exception e) {
		}
		return null;
	}
	
	public static void removeFavAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_FAV_DATA, Context.MODE_PRIVATE);
		oAuthPref.edit().remove(FAV_DATA_FAV).commit();
		oAuthPref.edit().remove(FAV_DATA_FAV_LAT).commit();
		oAuthPref.edit().remove(FAV_DATA_FAV_LONG).commit();
	}
	
//=======================================================================================================================================
	private static final String NEW_FIRST_SELLER_FLAG = "new_first_seller_flag";
	private static final String SELLER_MOBILE_FLAG = "seller_mobile_flag";
	private static final String SELLER_PWD_FLAG = "seller_pwd_flag";
	private static final String SELLER_DATA_FLAG = "seller_data_flag";
	private static final String SP_SELLER_DATA = "com.robin.android.login.sellerdata";
	private static final String SP_SELLER_DATA_NAME = "seller_name";
	private static final String SP_SELLER_DATA_PHONE = "seller_phone";
	private static final String SP_SELLER_DATA_PWD = "seller_pwd";
	private static final String SP_SELLER_DATA_ADDRESS = "seller_address";
	private static final String SP_SELLER_DATA_STATUS = "seller_status";
	private static final String SP_SELLER_IMG = "seller_img";
	private static final String SP_SELLER_ZHIZHAO = "seller_img_zhizho";
	private static final String SP_SELLER_EWM = "seller_img_ewm";
	private static final String SP_BAIDU_UID = "baidu_uid";
	private static final String SP_BAIDU_CID = "baidu_cid";
	
	public static int isFirstSeller(Context context) {
		final SharedPreferences storage = context.getSharedPreferences(SP_APP_DATA, Context.MODE_PRIVATE);
		return storage.getInt(NEW_FIRST_SELLER_FLAG, 0);
	}
	
	public static void updateFirstSeller(Context context, int version) {
		final SharedPreferences storage = context.getSharedPreferences(SP_APP_DATA, Context.MODE_PRIVATE);
		storage.edit().putInt(NEW_FIRST_SELLER_FLAG, version).commit();
	}
	
	public static void saveSellerMobile(Context context, String phone, String pwd){
		final SharedPreferences storage = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SELLER_MOBILE_FLAG, phone);
		editor.putString(SELLER_PWD_FLAG, pwd);
		editor.commit();
	}
	
	public static String getSellerMobile(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SELLER_MOBILE_FLAG, "");
	}
	
	public static String[] getSellerMobileAndPwd(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		String[] strs = new String[]{oAuthPref.getString(SELLER_MOBILE_FLAG, ""), oAuthPref.getString(SELLER_PWD_FLAG, "")};
		return strs;
	}
	
	public static void removeSellerMobile(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		oAuthPref.edit().remove(SELLER_MOBILE_FLAG).remove(SELLER_PWD_FLAG).commit();
	}
	
	public static void saveSellerData(Context context, String phone){
		final SharedPreferences storage = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SELLER_DATA_FLAG, phone);
		editor.commit();
	}
	
	public static String getSellerData(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SELLER_DATA_FLAG, "");
	}
	
	public static void removeSellerData(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_USER_DATA, Context.MODE_PRIVATE);
		oAuthPref.edit().remove(SELLER_DATA_FLAG).commit();
	}
	
	public static void saveShopName(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_DATA_NAME, name);
		editor.commit();
	}
	
	public static String getShopName(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_DATA_NAME, "");
	}
	
	public static void saveShopPhone(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_DATA_PHONE, name);
		editor.commit();
	}
	
	public static String getShopPhone(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_DATA_PHONE, "");
	}
	
	public static void saveShopPwd(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_DATA_PWD, name);
		editor.commit();
	}
	
	public static String getShopPwd(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_DATA_PWD, "");
	}
	
	public static void saveShopAddress(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_DATA_ADDRESS, name);
		editor.commit();
	}
	
	public static String getShopAddress(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_DATA_ADDRESS, "");
	}
	
	public static void saveShopStatus(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_DATA_STATUS, name);
		editor.commit();
	}
	
	public static String getShopStatus(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_DATA_STATUS, "");
	}
	
	public static void saveShoperImage(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_IMG, name);
		editor.commit();
	}
	
	public static String getShoperImage(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_IMG, "");
	}
	
	public static void saveZhizhaoImage(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_ZHIZHAO, name);
		editor.commit();
	}
	
	public static String getZhizhaoImage(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_ZHIZHAO, "");
	}
	
	public static void saveEwmImage(Context context, String name) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_SELLER_EWM, name);
		editor.commit();
	}
	
	public static String getEwmImage(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return oAuthPref.getString(SP_SELLER_EWM, "");
	}
	
	public static void saveBaiduUid(Context context, String uid, String cid) {
		final SharedPreferences storage = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		Editor editor = storage.edit();
		editor.putString(SP_BAIDU_UID, uid);
		editor.putString(SP_BAIDU_CID, cid);
		editor.commit();
	}
	
	public static String[] getBaiduUid(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences(SP_SELLER_DATA, Context.MODE_PRIVATE);
		return new String[]{oAuthPref.getString(SP_BAIDU_UID, ""), oAuthPref.getString(SP_BAIDU_CID, "")};
	}
	
	public static void saveOrderList(Context context, String oid) {
		SharedPreferences .Editor editor = context.getSharedPreferences("list_order", Context.MODE_PRIVATE).edit();
		ArrayList<String> orderList = getOrderList(context);
		int size = orderList.size();
		editor.putInt("size", size + 1);
		editor.putString("p_"+ size, oid);
		editor.commit();
	}
	
	public static ArrayList<String> getOrderList(Context context) {
		SharedPreferences sp= context.getSharedPreferences("list_order", Context.MODE_PRIVATE);
        int size=sp.getInt("size", 0);
        ArrayList<String> list=new ArrayList<String>();
        for(int i = 0;i<size;i++){
            String s = sp.getString("p_"+ i, "");
            list.add(s);
        }
        return list;
	}
	
	public static void removeOrderList(Context context) {
		SharedPreferences oAuthPref = context.getSharedPreferences("list_order", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = oAuthPref.edit();
		int size= oAuthPref.getInt("size", 0);
        for(int i = 0;i<size;i++){
        	editor.remove("p_"+ i);
        }
        editor.putInt("size", 0);
        editor.commit();
	}
}
