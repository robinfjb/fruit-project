package com.robin.fruitlib.io;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.robin.fruitlib.bean.CommonShopBean;
import com.robin.fruitlib.bean.DeviceBean;
import com.robin.fruitlib.bean.NeighborBean;
import com.robin.fruitlib.bean.RecommendBean;
import com.robin.fruitlib.bean.SellerBean;
import com.robin.fruitlib.bean.SellerMapBean;
import com.robin.fruitlib.bean.SellerTradeBean;
import com.robin.fruitlib.bean.SendReturnBean;
import com.robin.fruitlib.bean.TradeBean;
import com.robin.fruitlib.bean.TradeDetailBean;
import com.robin.fruitlib.bean.UserData;
import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.http.RHttpEngine;
import com.robin.fruitlib.io.data.CancelOrderParam;
import com.robin.fruitlib.io.data.ConfirmOrderParam;
import com.robin.fruitlib.io.data.DeviceParam;
import com.robin.fruitlib.io.data.FavParam;
import com.robin.fruitlib.io.data.GetPasscodeParam;
import com.robin.fruitlib.io.data.HistoryOrderParam;
import com.robin.fruitlib.io.data.LoginParam;
import com.robin.fruitlib.io.data.MapSellerParam;
import com.robin.fruitlib.io.data.MonthRecParam;
import com.robin.fruitlib.io.data.MyShopParam;
import com.robin.fruitlib.io.data.NeighborParam;
import com.robin.fruitlib.io.data.QiniuTokenParam;
import com.robin.fruitlib.io.data.RefuseOrderParam;
import com.robin.fruitlib.io.data.SendOrderParam;
import com.robin.fruitlib.io.data.TradeDetailParam;
import com.robin.fruitlib.io.data.TradeParam;
import com.robin.fruitlib.io.data.seller.PrivateQiniuTokenParam;
import com.robin.fruitlib.io.data.seller.PushOrderParam;
import com.robin.fruitlib.io.data.seller.QiangdanParam;
import com.robin.fruitlib.io.data.seller.SellerCancelOrderParam;
import com.robin.fruitlib.io.data.seller.SellerForgotPwdParam;
import com.robin.fruitlib.io.data.seller.SellerGetPasscodeParam;
import com.robin.fruitlib.io.data.seller.SellerGetPasscodeRenewParam;
import com.robin.fruitlib.io.data.seller.SellerLoginParam;
import com.robin.fruitlib.io.data.seller.SellerRegisterParam;
import com.robin.fruitlib.io.data.seller.SellerRenewParam;
import com.robin.fruitlib.io.data.seller.SellerSendPasscodeParam;
import com.robin.fruitlib.io.data.seller.SellerShopParam;
import com.robin.fruitlib.util.UrlConst;
import com.umeng.analytics.MobclickAgent;

public class FruitApi {
	protected Context context;
	private RHttpEngine httpEngine;
	private RHttpEngine urlConnectionEngine;
	private volatile static FruitApi instance;

	// 公开构造方法逐步不使用
	public FruitApi(Context ctx) {
		super();
		this.context = ctx;
		httpEngine = RHttpEngine.getInstance(ctx);
		urlConnectionEngine = RHttpEngine.getHttpUrlConnectionInstance(ctx);
	}

	public static FruitApi getInstance(Context context) {
		if (instance == null) {
			synchronized (FruitApi.class) {
				if (instance == null) {
					instance = new FruitApi(context.getApplicationContext());
				}
			}
		}
		return instance;
	}
	
	/**
	 * image load
	 * @param url
	 * @return
	 * @throws HttpException
	 */
	public InputStream requestBitmap(String url) throws HttpException {
		return httpEngine.httpGetStream(url);
	}
	
	/**
	 * 获取验证码
	 * @param phone
	 * @return
	 * @throws HttpException
	 */
	public String getPassCode(String phone) throws HttpException {
		GetPasscodeParam param = new GetPasscodeParam(context, phone);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.getString("message");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public UserData login(String phone, String code) throws HttpException {
		LoginParam param = new LoginParam(context, phone, code);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		JSONObject json = null;
		try {
			json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return UserData.parseJson(jsonData);
		} catch (JSONException e) {
			String text = json.optString("data");
			if(!TextUtils.isEmpty(text)) {
				throw new HttpException(text);
			} else {
				throw new HttpException(error);
			}
		}
	}
	
	public String uploadToken(String mobile) throws HttpException {
		QiniuTokenParam param = new QiniuTokenParam(context, mobile);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.getString("upToken");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<TradeBean> getTradeListData(String oid) throws HttpException {
		String phone = FruitPerference.getUserMobile(context);
		TradeParam param = new TradeParam(context, phone, oid);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return TradeBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public DeviceBean getDevice() throws HttpException {
		DeviceParam param = new DeviceParam(context);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return DeviceBean.parseItem(jsonData);
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<SellerMapBean> getMapSellerList(double lon, double lat) throws HttpException {
		MapSellerParam param = new MapSellerParam(context, lon, lat);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return SellerMapBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public SendReturnBean sendOrder(
			String addX,
			String addY,
			String sId,
			String expressStart,
			String expressEnd,
			String audioName,
			String address,
			String audioLength) throws HttpException{
		String cTel = FruitPerference.getUserMobile(context);
		SendOrderParam param = new SendOrderParam(context,cTel,addX,addY,sId,expressStart,expressEnd,audioName,address,audioLength);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		Log.d("AddressSelectActivity", "result=" + result);
		String error = "";
		JSONObject json = new JSONObject();
		try {
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
				json = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
			} else {
				json = new JSONObject(result);
			}
			Log.d("AddressSelectActivity", "json=" + json.toString());
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			Log.e("AddressSelectActivity", "jsonData=" + jsonData);
			return SendReturnBean.parse(jsonData);
		} catch (JSONException e) {
			MobclickAgent.reportError(context, "send order error:" + json.toString());
			Log.e("AddressSelectActivity", "e=" + e);
			throw new HttpException(error);
		}
	}
	
	public boolean cancelOrder(String oId) throws HttpException{
		String cTel = FruitPerference.getUserMobile(context);
		CancelOrderParam param = new CancelOrderParam(context, cTel, oId);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.optString("result").equals("1");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean confirmOrder(String oId) throws HttpException{
		String cTel = FruitPerference.getUserMobile(context);
		ConfirmOrderParam param = new ConfirmOrderParam(context, cTel, oId);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.optString("result").equals("1");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean refuseOrder(String oId) throws HttpException{
		String cTel = FruitPerference.getUserMobile(context);
		RefuseOrderParam param = new RefuseOrderParam(context, cTel, oId);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.optString("result").equals("1");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<NeighborBean> getNeighborData(double lat, double lon) throws HttpException {
		String cTel = FruitPerference.getUserMobile(context);
		NeighborParam param = new NeighborParam(context, cTel, lat, lon);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return NeighborBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean updateFavShop(int type, String sTel) throws HttpException {
		String cTel = FruitPerference.getUserMobile(context);
		FavParam param = new FavParam(context, type, cTel, sTel);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			String results = jsonData.optString("result");
			return results.equals("1") || results.equalsIgnoreCase("true");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<RecommendBean> getMonthRecData() throws HttpException {
		MonthRecParam param = new MonthRecParam(context);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return RecommendBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public List<CommonShopBean> getAllShop() throws HttpException {
		String cTel = FruitPerference.getUserMobile(context);
		MyShopParam param = new MyShopParam(context, cTel);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			ArrayList<CommonShopBean> list = new ArrayList<CommonShopBean>();
			ArrayList<CommonShopBean> tradeList = CommonShopBean.parseList(jsonData.getJSONArray("tradeList"));
			ArrayList<CommonShopBean> favoriteList = CommonShopBean.parseList(jsonData.getJSONArray("favoriteList"));
			list.addAll(tradeList);
			list.addAll(favoriteList);
			return list;
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public Pair<List<CommonShopBean>, List<CommonShopBean>> getFavShop() throws HttpException {
		String cTel = FruitPerference.getUserMobile(context);
		MyShopParam param = new MyShopParam(context, cTel);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			ArrayList<CommonShopBean> tradeList = CommonShopBean.parseList(jsonData.getJSONArray("tradeList"));
			ArrayList<CommonShopBean> favoriteList = CommonShopBean.parseList(jsonData.getJSONArray("favoriteList"));
			Pair<List<CommonShopBean>, List<CommonShopBean>> list =
					new Pair<List<CommonShopBean>, List<CommonShopBean>>(tradeList, favoriteList);
			return list;
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public TradeDetailBean getTradeDetail(String id) throws HttpException {
		TradeDetailParam param = new TradeDetailParam(context, id);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return TradeDetailBean.parseItem(jsonData.getJSONObject("info"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	
	public ArrayList<TradeDetailBean> getHistoryOrders(ArrayList<String> oids) throws HttpException {
		String mobile = FruitPerference.getUserMobile(context);
		HistoryOrderParam param = new HistoryOrderParam(context, mobile, oids);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return TradeDetailBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================
//======================================================================================================================================

	public SellerTradeBean qiangdanRequest(String oid) throws HttpException {
		String phone = FruitPerference.getSellerMobile(context);
		QiangdanParam param = new QiangdanParam(context, phone,oid);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		JSONObject json = null;
		try {
			if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
				json = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
			} else {
				json = new JSONObject(result);
			}
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			JSONObject orderJson = jsonData.optJSONObject("orderInfo");
			if(orderJson != null) {
				return SellerTradeBean.parseItem(orderJson);
			} else {
				int resultInt = jsonData.optInt("result");
				if(resultInt == 0) {
					return null;
				}
			}
			return null;
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<TradeBean> getSellerTradeListData() throws HttpException {
		String phone = FruitPerference.getSellerMobile(context);
		TradeParam param = new TradeParam(context, phone,null);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return TradeBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public SellerBean loginSeller(String phone, String code) throws HttpException {
		SellerLoginParam param = new SellerLoginParam(context, phone, code);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			if(jsonData.getInt("result") == 1) {
				JSONObject shop = jsonData.getJSONObject("shopInfo");
				FruitPerference.saveSellerData(context, shop.toString());
				SellerBean data = SellerBean.parseJson(shop);
				if(!TextUtils.isEmpty(data.sId)) {
					return data;
				} else {
					throw new HttpException(jsonData.getString("message"));
				}
			} else {
				throw new HttpException(jsonData.getString("message"));
			}
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public String getSellerPassCode(String phone) throws HttpException {
		SellerGetPasscodeParam param = new SellerGetPasscodeParam(context, phone);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.getString("message");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public String getSellerPassCodeRenewPwd(String phone) throws HttpException {
		SellerGetPasscodeRenewParam param = new SellerGetPasscodeRenewParam(context, phone);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.getString("message");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean submitPassCode(String phone, String code) throws HttpException {
		SellerSendPasscodeParam param = new SellerSendPasscodeParam(context, phone, code);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			String results = jsonData.optString("result");
			if(results.equals("1")) {
				return true;
			} else {
				throw new HttpException(jsonData.getString("message"));
			}
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	/**
	 * 
	 * @param phone
	 * @param address
	 * @param lat
	 * @param lon
	 * @param shopName
	 * @param shopImage
	 * @param shopLicence
	 * @param passwd
	 * @param code
	 * @return
	 * @throws HttpException
	 */
	public String sellerRegister(String phone, String address, double lat, double lon, String shopName,
			String shopImage, String shopLicence, String passwd, String code) throws HttpException {
		SellerRegisterParam param = new SellerRegisterParam(context, 
				phone, address, lat, lon, shopName,
				shopImage, shopLicence, passwd, code);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.optString("register");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public String uploadPrivateToken(String mobile) throws HttpException {
		PrivateQiniuTokenParam param = new PrivateQiniuTokenParam(context, mobile);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.getString("upToken");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean forgetPassCode(String phone, String code, String pwd) throws HttpException {
		SellerForgotPwdParam param = new SellerForgotPwdParam(context, phone, code, pwd);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			String results = jsonData.optString("register");
			if(results.equals("1") || results.toLowerCase().equals("true")) {
				return true;
			} else {
				throw new HttpException(jsonData.getString("message"));
			}
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean renewShopInfo(ArrayList<String> key, ArrayList<String> name) throws HttpException {
		SellerRenewParam param = new SellerRenewParam(context, key, name);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			String results = jsonData.optString("result");
			if(results.equals("1") || results.toLowerCase().equals("true")) {
				return true;
			} else {
				throw new HttpException(jsonData.getString("message"));
			}
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<SellerTradeBean> getShopList() throws HttpException {
		String mobile = FruitPerference.getSellerMobile(context);
		SellerShopParam param = new SellerShopParam(context, mobile);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return SellerTradeBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public boolean sellerCancelOrder(String oid) throws HttpException {
		String mobile = FruitPerference.getSellerMobile(context);
		SellerCancelOrderParam param = new SellerCancelOrderParam(context, mobile, oid);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return jsonData.getString("result").equals("1") || jsonData.getString("result").equalsIgnoreCase("true");
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
	
	public ArrayList<TradeDetailBean> getPushShopOrderList() throws HttpException {
		String mobile = FruitPerference.getSellerMobile(context);
		PushOrderParam param = new PushOrderParam(context, mobile);
		Map<String, String> getParams = param.getNetRequestGetBundle();
		String result = httpEngine.httpGet(UrlConst.GET_URL, getParams);
		String error = "";
		try {
			JSONObject json = new JSONObject(result);
			error = json.getString("errorNo");
			JSONObject jsonData = json.getJSONObject("data");
			return TradeDetailBean.parseList(jsonData.getJSONArray("list"));
		} catch (JSONException e) {
			throw new HttpException(error);
		}
	}
}
