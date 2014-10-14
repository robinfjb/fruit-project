package com.robin.fruitlib.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.HashMap;
import org.apache.http.client.HttpClient;

/**
 * @Description 统一Apache HttpClient，URLConnection的响应结果. 包括状态行，响应头，响应体
 */
class ResponseWrapper {
	private String mVersion;//协议版本
	private int mCode;//状态码
	private HashMap<String, String> mHeaders;//报文头
	private InputStream mContent;//报文实体
	private Object connection;//链接（采用实现不同，类型也不同）
	private String mExtra;

	private ResponseWrapper(Builder builder) {
		mVersion = builder.version;
		mCode = builder.code;
		mHeaders = builder.headers;
		mContent = builder.content;
		connection = builder.connection;
		mExtra = builder.extra;
	}

	public String getmExtra() {
		return mExtra;
	}

	public void setmExtra(String mExtra) {
		this.mExtra = mExtra;
	}

	/**
	 * 获取HTTP版本
	 * @return
	 */
	public String getHttpVersion() {
		return mVersion;
	}

	/**
	 * 获取状态码
	 * @return
	 */
	public int getCode() {
		return mCode;
	}

	/**
	 * 获取报文内容
	 * @return
	 */
	public InputStream getContent() {
		return mContent;
	}

	/**
	 * 获取报文头
	 * @return
	 */
	public HashMap<String, String> getHeader() {
		return mHeaders;
	}

	public Object getConnection() {
		return connection;
	}

	/**
	 * 获取实体长度
	 * @return
	 */
	public int getLength() {
		int length = -1;
		if (mHeaders != null) {
			String lengthStr = mHeaders.get("Content-Length");
			if (lengthStr != null) {
				length = Integer.parseInt(lengthStr);
			}
		}
		return length;
	}
	
	/**
	 * 关闭链接
	 */
	public void closeConnection() {
		if (connection != null) {
			if (connection instanceof HttpClient) {
				HttpClient conn = (HttpClient) connection;
				conn.getConnectionManager().shutdown();
			} else if (connection instanceof HttpURLConnection) {
				HttpURLConnection conn = (HttpURLConnection) connection;
				conn.disconnect();
			} else if (connection instanceof Socket) {
				Socket conn = (Socket) connection;
				try {
					conn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static class Builder {
		private HashMap<String, String> headers;
		private InputStream content;
		private String version;
		private int code;
		private Object connection;
		private String extra;

		public ResponseWrapper build() {
			ResponseWrapper resposne = new ResponseWrapper(this);
			return resposne;
		}

		public Builder setContent(InputStream content) {
			this.content = content;
			return this;
		}

		public Builder setVersion(String version) {
			this.version = version;
			return this;
		}

		public Builder setCode(int code) {
			this.code = code;
			return this;
		}

		public Builder setConnection(Object conn) {
			this.connection = conn;
			return this;
		}

		public Builder setHeaders(HashMap<String, String> headers) {
			this.headers = headers;
			return this;
		}
		
		public Builder setExtra(String extra){
			this.extra = extra;
			return this;
		}
	}

}
