package com.robin.fruitlib.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.robin.fruitlib.bean.SimpleResponseBean;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
/**
 * @Description HttpURLConnection实现IHttpClient接口.
 */
public class HttpURLConnectionImpl implements IHttpClient {
	final static String TAG = HttpURLConnectionImpl.class
			.getSimpleName();
	private static boolean debug = false;
	private static final String TWO_HYPHENS = "--";
	private static final String BOUNDARY = "weibo";
	private static final String END = "\r\n";

	private static IHttpClient instance;
	private Context context;

	private HttpURLConnectionImpl(Context context) {
		this.context = context.getApplicationContext();
	}

	/**
	 * 构造HttpURLConnectionImpl实例
	 * @param context
	 * @return
	 */
	public static IHttpClient getInstance(Context context) {
		if (instance == null) {
			synchronized (HttpURLConnectionImpl.class) {
				if (instance == null) {
					instance = new HttpURLConnectionImpl(context);
				}
			}
		}
		return instance;
	}

	private InetSocketAddress getInetSocketAddress() throws HttpException {
		InetSocketAddress mInetSocketAddress = null;
		NetworkUtils.NetworkState state = NetworkUtils.getNetworkState(context);
		if (state == NetworkUtils.NetworkState.NOTHING) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR);
		} else if (state == NetworkUtils.NetworkState.MOBILE) {
			NetworkUtils.APNWrapper wrapper = NetworkUtils.getAPN(context);
			if (!TextUtils.isEmpty(wrapper.proxy)) {
				mInetSocketAddress = new InetSocketAddress(wrapper.proxy,
						wrapper.port);
			}
		}
		return mInetSocketAddress;
	}

	private HttpURLConnection getConnection(String url)
			throws HttpException {
		HttpURLConnection connection = null;

		// proxy 配置
		InetSocketAddress inetSocketAddress = getInetSocketAddress();
		if (inetSocketAddress == null) {
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				connection = (HttpURLConnection) new URL(url)
						.openConnection(new Proxy(Proxy.Type.HTTP,
								inetSocketAddress));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// https
		if (!TextUtils.isEmpty(url)
				&& url.toLowerCase(Locale.ENGLISH).startsWith("https")) {
			trustAllHosts((HttpsURLConnection) connection);
		}

		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);
		return connection;
	}

	@Override
	public ResponseWrapper httpGet(String url) throws HttpException {
		return httpGet(url, null);
	}

	@Override
	public ResponseWrapper httpGet(String url, Map<String, String> getParams)
			throws HttpException {
		return httpGet(url, getParams, null);
	}

	@Override
	public ResponseWrapper httpGet(String url, Map<String, String> getParams, Bundle headers)
			throws HttpException {
		ResponseWrapper rw = null;
		try {
			url = NetworkUtils.getCompleteUrl(url, getParams);
			HttpURLConnection connection = getConnection(url);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(false);
			setupHeaders(connection, headers);

			connection.connect();

			SimpleResponseBean result = checkResponse(connection);
			
			if(result.isNeedRedirect()){//重定向
				return httpGet(result.getRedirectUrl(), getParams, headers);
			}else{
				rw = connectionToWrapper(connection, result.getRedirectUrl());
			}
		} catch (IOException e) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
		}
		return rw;
	}

	@Override
	public ResponseWrapper httpPost(String url, Bundle postParams)
			throws HttpException {
		return httpPost(url, null, postParams);
	}

	@Override
	public ResponseWrapper httpPost(String url, Map<String, String> getParams,
			Bundle postParams) throws HttpException {
		return httpPost(url, getParams, postParams, null);
	}

	@Override
	public ResponseWrapper httpPost(String url, Map<String, String> getParams,
			Bundle postParams, Bundle headers) throws HttpException {
		String str = NetworkUtils.encodeUrl(postParams);
		return httpPost(url, getParams, str, headers);
	}

	@Override
	public ResponseWrapper httpPost(String url, Map<String, String> getParams, String str,
			Bundle headers) throws HttpException {
		ResponseWrapper rw = null;
		try {
			HttpURLConnection connection = getConnection(url);
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			setupHeaders(connection, headers);

			printRequestHeaders(connection);
			byte[] buffer = null;
			buffer = str.getBytes();
			connection.getOutputStream().write(buffer);
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			connection.connect();

			printResponseHeaders(connection);

			checkResponse(connection);

			rw = connectionToWrapper(connection, null);
		} catch (IOException e) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
		}
		return rw;
	}


	private ResponseWrapper connectionToWrapper(HttpURLConnection connection, String redirectUrl)
			throws IOException {
		ResponseWrapper rw = new ResponseWrapper.Builder()
				.setCode(connection.getResponseCode())
				.setContent(connection.getInputStream())
				.setHeaders(headerToHashMap(connection))
				.setExtra(redirectUrl).build();
		return rw;
	}

	private HashMap<String, String> headerToHashMap(HttpURLConnection connection) {
		// printResponseHeaders(connection);
		HashMap<String, String> map = new HashMap<String, String>();
		Map<String, List<String>> headers = connection.getHeaderFields();
		StringBuilder sb = new StringBuilder();
		for (String key : headers.keySet()) {
			List<String> list = headers.get(key);

			boolean first = true;
			for (String head : list) {
				if (first) {
					first = false;
				} else {
					sb.append(";");
				}
				sb.append(head);
			}
			map.put(key, sb.toString());
			sb.delete(0, sb.length());// 清空
		}
		return map;
	}

	private void setupHeaders(HttpURLConnection connection, Bundle headers) {
		if (headers == null) {
			return;
		}
		Set<String> keys = headers.keySet();
		if (keys.size() > 0) {
			for (String key : keys) {
				connection.setRequestProperty(key, headers.getString(key));
			}
		}
	}

	/**
	 * 装载上传输出流
	 * 
	 * @param dataOutputStream
	 * @param bundle
	 * @throws IOException
	 */
	private void setupUploadOutputStream(DataOutputStream dataOutputStream,
			Bundle bundle, IDownloadState callback) throws IOException {
		long size = getAllFileLengthSum(bundle);
		long count = 0;

		StringBuffer sb = new StringBuffer();
		for (String key : bundle.keySet()) {
			if (NetworkUtils.TYPE_FILE_NAME.equals(key)
					|| NetworkUtils.GZIP_FILE_NAME.equals(key)) {
				Object object = bundle.get(key);
				if (object != null && object instanceof Bundle) {
					Bundle mFilePathBundle = ((Bundle) object);
					for (final String mFilePathBundleKey : mFilePathBundle
							.keySet()) {
						StringBuffer fileTable = new StringBuffer();
						File mFile = new File(
								mFilePathBundle.getString(mFilePathBundleKey));
						if (mFile.exists()) {
							fileTable.append(TWO_HYPHENS + BOUNDARY + END);
							fileTable
									.append("Content-Disposition:form-data;name=");
							fileTable.append("\"" + "pic" + "\"" + ";");
							fileTable.append("filename=");
							fileTable.append("\"" + mFile.getName() + "\""
									+ END);
							fileTable.append("Content-Type: " + "image/jpeg"
									+ END);
							fileTable.append(END);
							dataOutputStream.writeBytes(fileTable.toString());
							InputStream mInputStream = new FileInputStream(
									mFile);
							byte[] buffers = new byte[1024];
							int temp = 0;
							while ((temp = mInputStream.read(buffers)) != -1) {
								dataOutputStream.write(buffers, 0, temp);

								if (callback != null) {
									count += temp;
									callback.onProgressChanged((count * 100 / size));
								}
							}
							mInputStream.close();
							dataOutputStream.writeBytes(END);
						}
					}
				}
			} else {
				sb.append(TWO_HYPHENS + BOUNDARY + END);
				sb.append("Content-Disposition: form-data;name=");
				sb.append("\"" + key + "\"" + ";" + END);
				sb.append(END);
				sb.append(URLEncoder.encode(String.valueOf(bundle.get(key)),
						"UTF-8"));
				sb.append(END);
				dataOutputStream.writeBytes(sb.toString());
			}
		}
	}

	/**
	 * 计算待上传文件的总长度
	 * 
	 * @param bundle
	 * @return
	 */
	private long getAllFileLengthSum(Bundle bundle) {
		long size = 0;
		for (String key : bundle.keySet()) {
			if (NetworkUtils.TYPE_FILE_NAME.equals(key)
					|| NetworkUtils.GZIP_FILE_NAME.equals(key)) {
				Object object = bundle.get(key);
				if (object != null && object instanceof Bundle) {
					Bundle mFilePathBundle = ((Bundle) object);
					for (final String mFilePathBundleKey : mFilePathBundle
							.keySet()) {
						File mFile = new File(
								mFilePathBundle.getString(mFilePathBundleKey));
						if (mFile.exists()) {
							size += mFile.length();
						}
					}
				}
			}
		}
		return size;
	}

	private static void trustAllHosts(HttpsURLConnection conn)
			throws HttpException {
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(new KeyManager[0], TRUST_ALL_CERTS,
					new java.security.SecureRandom());
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(DO_NOT_VERIFY);
		} catch (Exception e) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
		}
	}

	private final static TrustManager[] TRUST_ALL_CERTS = new TrustManager[] { new X509TrustManager() {
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	} };

	private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private SimpleResponseBean checkResponse(HttpURLConnection connection)
			throws HttpException {
		SimpleResponseBean result = new SimpleResponseBean();;
		try {
			String url = connection.getURL().toString();
			result.setUrl(url);
			connection.setInstanceFollowRedirects(true);
			int statusCode = connection.getResponseCode();
			
			if (statusCode != HttpURLConnection.HTTP_OK) {
				throw new HttpException(HttpException.MSG_NETWORK_ERROR, statusCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
		}
		return result;
	}
	

	/**
	 * 打印响应头（for debug）
	 * 
	 * @param connection
	 */
	private static void printResponseHeaders(HttpURLConnection connection) {
		if (!debug)
			return;
		Map<String, List<String>> map = connection.getHeaderFields();
		Set<String> set = map.keySet();
		for (String key : set) {
			List<String> list = map.get(key);
			StringBuilder values = new StringBuilder();
			boolean first = true;
			for (String value : list) {
				if (first) {
					first = false;
					values.append(value);
				} else {
					values.append("," + value);
				}
			}
//			Log.w(TAG, key + ":" + values.toString());
		}
	}

	private static void printRequestHeaders(HttpURLConnection connection) {
		if (!debug)
			return;
		Map<String, List<String>> map = connection.getRequestProperties();
		Set<String> set = map.keySet();
		for (String key : set) {
			List<String> list = map.get(key);
			StringBuilder values = new StringBuilder();
			boolean first = true;
			for (String value : list) {
				if (first) {
					first = false;
					values.append(value);
				} else {
					values.append("," + value);
				}
			}
//			Log.w(TAG, key + ":" + values.toString());
		}
	}

	@Override
	public ResponseWrapper httpPost(String url, Map<String, String> getParams,
			Bundle postParams, Bundle headers, IDownloadState callback)
			throws HttpException {
		// TODO 还是要统一post的操作，所有post只能有一个入口。
		ResponseWrapper rw = null;
		HttpURLConnection connection = null;
		try {
			url = NetworkUtils.getCompleteUrl(url, getParams);
			connection = getConnection(url);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			setupHeaders(connection, headers);
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			printRequestHeaders(connection);
			DataOutputStream dataOutputStream = new DataOutputStream(
					connection.getOutputStream());
			setupUploadOutputStream(dataOutputStream, postParams, callback);// 装载上传输出流
			dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS
					+ END);
			dataOutputStream.flush();

			connection.connect();

			checkResponse(connection);
//			Log.e(TAG, "================");
			printResponseHeaders(connection);

			rw = connectionToWrapper(connection, null);
			dataOutputStream.close();
		} catch (MalformedURLException e) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
		} catch (IOException e) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
		}
		return rw;
	}
}
