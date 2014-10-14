package com.robin.fruitlib.http;

/**
 * HTTP StatusCode is not 200
 */
public class HttpException extends Exception {
	public static final String MSG_NETWORK_ERROR = "网络连接错误";
	public static final String MSG_DATA_FORMAT_ERROR = "服务器数据解析错误";
	/**
	 * 
	 */
	private static final long serialVersionUID = 6709638977967263501L;
	private int statusCode = -1;

	public HttpException(String msg) {
		super(msg);
	}

	public HttpException(Exception cause) {
		super(cause);
	}

	public HttpException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public HttpException(String msg, Exception cause) {
		super(msg, cause);
	}

	public HttpException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

}
