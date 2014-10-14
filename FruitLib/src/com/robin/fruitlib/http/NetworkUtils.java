package com.robin.fruitlib.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * @Description 网络模块工具类,包含了网络状态获取，URL拼接，VPN获取等工具方法
 */
public class NetworkUtils {
	// 请求头中的代理设置
	public static String USER_AGENT = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.1.4) Gecko/20091111 Gentoo Firefox/3.5.4";
	public static final String TYPE_FILE_NAME = "TYPE_FILE_NAME";
	public static final String GZIP_FILE_NAME = "GZIP_FILE_NAME";
	public static final String FILE_SAME_KEY = "file_same_key";

	public enum NetworkState {
		MOBILE, WIFI, NOTHING;
	}
	
	/**
	 * 获取当前网络状态.
	 * @param context
	 * @return
	 */
	public static NetworkState getNetworkState(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return NetworkState.NOTHING;
		} else {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				return NetworkState.MOBILE;
			} else {
				return NetworkState.WIFI;
			}
		}
	}

	/**
	 * 判断当前网络是否为wifi
	 * @param mContext
	 * @return
	 */
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前是否是连网状态
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		NetworkState networkState = getNetworkState(context);
		return networkState != NetworkState.NOTHING;
	}

	/**
	 * 通过拼接get参数（Bundle类型）获得完整的URL
	 * @param url
	 * @param getParams
	 * @return
	 */
	public static String getCompleteUrl(String url, Map<String, String> getParams) {
		String params = encodeUrl(getParams);
		return appendUrlParams(url, params);
	}
	
	/**
	 * 通过拼接get参数（String类型）获得完整的URL
	 * @param url   
	 * @param params 已经拼接完成的参数串
	 * @return
	 */
	public static String appendUrlParams(String url, String params) {
		if (TextUtils.isEmpty(params)) {
			return url;
		}

		final String HASH_KEY = "#";
		String browserAction = null;
		if (url.contains(HASH_KEY)) {
			int hashKeyPos = url.indexOf(HASH_KEY);
			browserAction = url.substring(hashKeyPos);
			url = url.substring(0, hashKeyPos);
		}

		if (url.indexOf('?') != -1 && url.indexOf('?') != url.length() - 1) {
			url = url + "&" + params;
		} else {
			url = url + "?" + params;
		}

		if (!TextUtils.isEmpty(browserAction)) {
			url += browserAction;
		}
		return url;
	}
	
	
	/**
	 * 将Bundle中存储的参数编码为String类型的串
	 * @param parameters
	 * @return
	 */
	public static String encodeUrl(Map<String, String> parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			sb.append(urlEncode(key) + "="
					+ urlEncode(parameters.get(key)));
		}
		return sb.toString();
	}
	
	public static String encodeUrl(Bundle parameters){
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			sb.append(urlEncode(key) + "="
					+ urlEncode(parameters.getString(key)));
		}
		return sb.toString();
	}

	private static String urlEncode(String in) {
		if (TextUtils.isEmpty(in)) {
			return "";
		}

		try {
			return URLEncoder.encode(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}


	private static final Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	private static final String NO_APN = "N/A";

	/** 
	 * APN包装类
	 **/
	public static class APNWrapper {
		public String name;
		public String apn;
		public String proxy;
		public int port;

		public String getApn() {
			return apn;
		}

		public String getName() {
			return name;
		}

		public int getPort() {
			return port;
		}

		public String getProxy() {
			return proxy;
		}

		APNWrapper() {
		}
	}

	/**
	 * 获取APN 
	 * @param ctx
	 * @return
	 */
	public static APNWrapper getAPN(Context mContext) {
		APNWrapper wrapper = new APNWrapper();

		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver().query(PREFERRED_APN_URI,
					new String[] { "name", "apn", "proxy", "port" }, null,
					null, null);
		} catch (Exception e) {
			// 为了解决在4.2系统上禁止非系统进程获取apn相关信息，会抛出安全异常
			// java.lang.SecurityException: No permission to write APN settings
		}
		if (cursor != null) {
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				wrapper.name = cursor.getString(0) == null ? "" : cursor
						.getString(0).trim();
				wrapper.apn = cursor.getString(1) == null ? "" : cursor
						.getString(1).trim();
			}
			cursor.close();
		}
		if (TextUtils.isEmpty(wrapper.apn)) {
			ConnectivityManager conManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = conManager.getActiveNetworkInfo();
			if (info != null) {
				wrapper.apn = info.getExtraInfo();
			}
		}
		if (TextUtils.isEmpty(wrapper.apn)) {
			TelephonyManager telephonyManager = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			wrapper.apn = telephonyManager.getNetworkOperatorName();
		}
		if (TextUtils.isEmpty(wrapper.apn)) {
			wrapper.name = NO_APN;
			wrapper.apn = NO_APN;
		}
		wrapper.proxy = android.net.Proxy.getDefaultHost();
		wrapper.proxy = TextUtils.isEmpty(wrapper.proxy) ? "" : wrapper.proxy;
		wrapper.port = android.net.Proxy.getDefaultPort();
		wrapper.port = wrapper.port > 0 ? wrapper.port : 80;
		return wrapper;
	}
	
	/**
	 * Handle Status code
	 * 
	 * @param statusCode
	 *            响应的状态码
	 * @param res
	 *            服务器响应
	 * @throws HttpException
	 *             当响应码不为200时都会报出此异常:<br />
	 *             <li>HttpRequestException, 通常发生在请求的错误,如请求错误了 网址导致404等, 抛出此异常,
	 *             首先检查request log, 确认不是人为错误导致请求失败</li> <li>HttpAuthException,
	 *             通常发生在Auth失败, 检查用于验证登录的用户名/密码/KEY等</li> <li>
	 *             HttpRefusedException, 通常发生在服务器接受到请求, 但拒绝请求, 可是多种原因, 具体原因
	 *             服务器会返回拒绝理由, 调用HttpRefusedException#getError#getMessage查看</li>
	 *             <li>HttpServerException, 通常发生在服务器发生错误时, 检查服务器端是否在正常提供服务</li>
	 *             <li>HttpException, 其他未知错误.</li>
	 */
	public static void HandleResponseStatusCode(int statusCode, Response res)
			throws HttpException {
		String msg = getCause(statusCode) + "\n";
		throw new HttpException(msg + res.asString(), statusCode);
	}
	
	/**
	 * 解析HTTP错误码
	 * 
	 * @param statusCode
	 * @return
	 */
	private static String getCause(int statusCode) {
		return statusCode + ":" + HttpException.MSG_NETWORK_ERROR;
	}
	
	/**
	 * 生成POST Parameters助手
	 * 
	 * @param nameValuePair
	 *            参数(一个或多个)
	 * @return post parameters
	 */
	public static ArrayList<BasicNameValuePair> createParams(
			BasicNameValuePair... nameValuePair) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (BasicNameValuePair param : nameValuePair) {
			params.add(param);
		}
		return params;
	}

	public static String createGetParams(BasicNameValuePair... nameValuePair) {
		StringBuilder sb = new StringBuilder();
		for (BasicNameValuePair param : nameValuePair) {
			if (sb.length() != 0)
				sb.append("&");
			sb.append(param.getName()).append("=").append(param.getValue());
		}
		return sb.toString();
	}
}
