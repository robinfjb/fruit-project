package com.robin.fruitlib.http;

/**
 * 解析response时出现IOException, JSONException等
 */
public class ResponseException extends HttpException {

	private static final long serialVersionUID = -9161304367990941666L;

	public ResponseException(Exception cause) {
		super(MSG_DATA_FORMAT_ERROR, cause);
		// TODO Auto-generated constructor stub
	}

	public ResponseException(String msg, Exception cause, int statusCode) {
		super(MSG_DATA_FORMAT_ERROR, cause, statusCode);
		// TODO Auto-generated constructor stub
	}

	public ResponseException(String msg, Exception cause) {
		super(MSG_DATA_FORMAT_ERROR, cause);
		// TODO Auto-generated constructor stub
	}

	public ResponseException(String msg, int statusCode) {
		super(MSG_DATA_FORMAT_ERROR, statusCode);
		// TODO Auto-generated constructor stub
	}

	public ResponseException(String msg) {
		super(MSG_DATA_FORMAT_ERROR);
		// TODO Auto-generated constructor stub
	}

}
