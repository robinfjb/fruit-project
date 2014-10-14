package com.robin.fruitlib.http;

/**
 * HTTP StatusCode is not 200
 */
public class RException extends HttpException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7585003967500496700L;
	private int statusCode = -1;

	public RException(String msg) {
		super(msg);
	}

	public RException(Exception cause) {
		super(cause);
	}

	public RException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public RException(String msg, Exception cause) {
		super(msg, cause);
	}

	public RException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

}
