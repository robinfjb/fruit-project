package com.robin.fruitlib.http;

import java.util.Map;

import android.os.Bundle;
/**
 * @Description 
 */
interface IHttpClient {
	/**
	 * 使用Get方法获取网络数据
	 * @param url
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpGet(String url) throws HttpException;

	/**
	 * 使用Get方法获取网络数据(带Get参数)
	 * @param url
	 * @param getParams
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpGet(String url, Map<String, String> getParams)
			throws HttpException;
    /**
     * 使用Get方法获取网络数据（带Get参数，可配置Header）
     * @param url
     * @param getParams
     * @param headers
     * @return
     * @throws NetworkException
     */
	public ResponseWrapper httpGet(String url, Map<String, String> getParams, Bundle headers)
			throws HttpException;

	/**
	 * 使用Post方法获取网络数据（带Post参数）
	 * @param url
	 * @param postParams
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpPost(String url, Bundle postParams)
			throws HttpException;

	/**
	 * 使用Post方法获取网络数据（带Get,Post参数）
	 * @param url
	 * @param getParams
	 * @param postParams
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpPost(String url, Map<String, String> getParams,
			Bundle postParams) throws HttpException;

	/**
	 * 使用Post方法获取网络数据（带Get,Post参数,可配置Header）
	 * @param url
	 * @param getParams
	 * @param postParams
	 * @param headers
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpPost(String url, Map<String, String> getParams,
			Bundle postParams, Bundle headers) throws HttpException;

	/**
	 * 使用Post方法获取网络数据（带Get,Post参数(已拼接成串),可配置Header）
	 * @param url
	 * @param getParams
	 * @param str
	 * @param headers
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpPost(String url, Map<String, String> getParams, String str,
			Bundle headers) throws HttpException;
	
	/**
	 * 
	 * @param url
	 * @param getParams
	 * @param postParams
	 * @param headers
	 * @param callback
	 * @return
	 * @throws NetworkException
	 */
	public ResponseWrapper httpPost(String url, Map<String, String> getParams,
            Bundle postParams, Bundle headers, IDownloadState callback)
            throws HttpException ;

}
