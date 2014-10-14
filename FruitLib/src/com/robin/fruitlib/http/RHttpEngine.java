package com.robin.fruitlib.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * @Description FanliHttpEngine 为网络模块和其它部分的接口类
 */
public class RHttpEngine {
    private static RHttpEngine instance;
    private IHttpClient mHttpClient;
    
    private static RHttpEngine mHttpUrlConnectionInstance;

    /**
     * 根据type构造不同的HTTP实现
     * @param context
     * @param type
     */
    private RHttpEngine(Context context, Type type) {
        switch (type) {
            case ApacheHttpClient:
                mHttpClient = HttpClientApacheImpl.getInstance(context);
                break;
            case HttpURLConnection:
                mHttpClient = HttpURLConnectionImpl.getInstance(context);
                break;
            default:
                mHttpClient = HttpClientApacheImpl.getInstance(context);
        }
    }

    public synchronized static RHttpEngine getHttpUrlConnectionInstance(Context context) {
        if (mHttpUrlConnectionInstance == null) {
        	mHttpUrlConnectionInstance = new RHttpEngine(context, Type.HttpURLConnection);
        }
        return mHttpUrlConnectionInstance;
    }
    
    public synchronized static RHttpEngine getInstance(Context context) {
        if (instance == null) {
            instance = new RHttpEngine(context, Type.ApacheHttpClient);
        }
        return instance;
    }

    /**HTTP响应处理器，此处将Inputstream处理为文本**/
    private ResponseProcessor<String> mStringProcessor = new ResponseProcessor<String>() {
        @Override
        public String processResponse(ResponseWrapper response,
                IDownloadState callback) throws HttpException {
            if (response == null) {
                return null;
            }
            
            if(!TextUtils.isEmpty(response.getmExtra())){
            	return response.getmExtra();
            }
            
            ByteArrayBuffer buffer = new ByteArrayBuffer(32 * 1024);
            InputStream in = null;
            try {
                in = response.getContent();
                if (response.getHeader() == null) {
                }

                String encoding = response.getHeader().get("Content-Encoding");
                if (encoding != null && encoding.contains("gzip")) {
                    in = new GZIPInputStream(in);
                } 

                int temp;
                byte[] bytes = new byte[8096];
                while ((temp = in.read(bytes)) != -1) {
                    buffer.append(bytes, 0, temp);
                }

            } catch (SocketTimeoutException e) {
            	e.printStackTrace();
                throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
            } catch (IOException e) {
            	e.printStackTrace();
                throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response != null) {
                    response.closeConnection();
                }
            }
            byte[] result = buffer.toByteArray();
            String data = new String(result);
            return data;
        }
    };
    
    /**HTTP响应处理器，此处将Inputstream处理为文本**/
    private ResponseProcessor<InputStream> mStreamProcessor = new ResponseProcessor<InputStream>() {
        @Override
        public InputStream processResponse(ResponseWrapper response,
                IDownloadState callback) throws HttpException {
            if (response == null) {
                return null;
            }

            InputStream in = null;
            try {
                in = response.getContent();
                if (response.getHeader() == null) {
                }

                String encoding = response.getHeader().get("Content-Encoding");
                if (encoding != null && encoding.contains("gzip")) {
                    in = new GZIPInputStream(in);
                } 

                return in;

            } catch (SocketTimeoutException e) {
            	e.printStackTrace();
                throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
            } catch (IOException e) {
            	e.printStackTrace();
                throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
            } finally {
                if (response != null) {
                    response.closeConnection();
                }
            }
        }
    };

    /**
     * 使用HTTP Get方法获取网络数据
     * @param url
     * @return 响应文本
     * @throws RException
     */
    public String httpGet(String url) throws HttpException {
		return httpGet(url, null);
    }

    /**
     * 使用HTTP Get方法获取网络数据(带Get参数)
     * @param url 
     * @param getParams Get参数
     * @return 响应文本
     * @throws RException
     */
    public String httpGet(String url, Map<String, String> getParams) throws HttpException {
		return httpGet(url, getParams, null);
    }

    /**
     * 使用HTTP Get方法获取网络数据(带Get参数和进度回调)
     * @param url
     * @param getParams
     * @param callback 下载进度回调
     * @return 响应文本
     * @throws RException
     */
    public String httpGet(String url, Map<String, String> getParams, IDownloadState callback)
            throws HttpException{
        Bundle headers = new Bundle();
        headers.putString("Accept-Encoding", "gzip,deflate");//压缩
        ResponseWrapper response;
		response = mHttpClient.httpGet(url, getParams, headers);
		return mStringProcessor.processResponse(response, callback);
    }
    
    public InputStream httpGetStream(String url) throws HttpException{
        Bundle headers = new Bundle();
        headers.putString("Accept-Encoding", "gzip,deflate");//压缩
        ResponseWrapper response;
		response = mHttpClient.httpGet(url, null, headers);
		return mStreamProcessor.processResponse(response, null);
    }

    /**
     * 使用HTTP Post方法获取网络数据(带Post参数)
     * @param url
     * @param postParams
     * @return 响应文本
     * @throws RException
     */
    public String httpPost(String url, Bundle postParams)
            throws HttpException {
        return httpPost(url, null, postParams);
    }

    /**
     * 使用HTTP Post方法获取网络数据(带Get,Post参数)
     * @param url
     * @param getParams
     * @param postParams
     * @return 响应文本
     * @throws RException
     */
    public String httpPost(String url, Map<String, String> getParams, Bundle postParams)
            throws HttpException {
        Bundle headers = new Bundle();
        headers.putString("Accept-Encoding", "gzip,deflate");//压缩
        headers.putString("Accept-Charset", "UTF-8,*;q=0.5");
        ResponseWrapper response;
		response = mHttpClient.httpPost(url, getParams,
		        postParams, headers);
		return mStringProcessor.processResponse(response, null);
    }

    /**
     * 上传一段文本
     * @param url
     * @param postText
     * @return
     * @throws RException
     */
    public String httpPost(String url, String postText) throws HttpException {
        ResponseWrapper response;
        Bundle headers = new Bundle();
        headers.putString("Accept-Encoding", "gzip,deflate");//压缩
        headers.putString("Accept-Charset", "UTF-8,*;q=0.5");
		response = mHttpClient.httpPost(url, null, postText, headers);
		return mStringProcessor.processResponse(response, null);
    }
}
