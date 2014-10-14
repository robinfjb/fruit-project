package com.robin.fruitlib.http;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * @Description 
 */
class HttpClientApacheImpl implements IHttpClient {
    static final String TAG = HttpClientApacheImpl.class.getSimpleName();

    private static IHttpClient instance;
    private Context context;

    private HttpClientApacheImpl(Context context) {
        this.context = context.getApplicationContext();
    }

    public static IHttpClient getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpClientApacheImpl.class) {
                if (instance == null) {
                    instance = new HttpClientApacheImpl(context);
                }
            }
        }
        return instance;
    }

    private HttpClient getHttpClient() throws HttpException {
        // scheme配置
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", getSSlSocketFactoryEx(),
                443));

        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);
        httpParams.setParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, 8192);
        httpParams.setParameter(HttpConnectionParams.SO_TIMEOUT, 30000);
        httpParams.setParameter(HttpConnectionParams.TCP_NODELAY, true);

        // DEFAULT_PROXY配置
        NetworkUtils.NetworkState state = NetworkUtils.getNetworkState(context);
        if (state == NetworkUtils.NetworkState.NOTHING) {// 没信号
            throw new HttpException(HttpException.MSG_NETWORK_ERROR);
        } else if (state == NetworkUtils.NetworkState.MOBILE) {// 移动网络
            NetworkUtils.APNWrapper wrapper = NetworkUtils.getAPN(context);
            if (!TextUtils.isEmpty(wrapper.proxy)) {
                httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY,
                        new HttpHost(wrapper.proxy, wrapper.port));
            }
        }

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                httpParams, schemeRegistry);

        DefaultHttpClient httpClient = new DefaultHttpClient(cm, httpParams);
        return httpClient;
    }

    @Override
    public ResponseWrapper httpGet(String url) throws HttpException{
        return httpGet(url, null, null);
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
            HttpClient httpClient = getHttpClient();
            url = NetworkUtils.getCompleteUrl(url, getParams);
            HttpGet request = new HttpGet(url);
            setupHeaders(request, headers);// 装载请求头

            HttpResponse response = httpClient.execute(request);
            checkResponse(response);

            rw = httpResponseToWrapper(response, httpClient);
        } catch (SocketTimeoutException e) {
			throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        } catch (IllegalStateException e) {
        	throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
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
        return httpPost(url, getParams, postParams, headers, null);
    }

    @Override
    public ResponseWrapper httpPost(String url, Map<String, String> getParams, String str,
            Bundle headers) throws HttpException {// TODO 此方法待测试,应该是可以复用的
        ResponseWrapper rw = null;

        try {
            HttpClient httpClient = getHttpClient();
            url = NetworkUtils.getCompleteUrl(url, getParams);
            HttpPost request = new HttpPost(url);
            setupHeaders(request, headers);// 装载请求头
            StringEntity stringEntity = new StringEntity(str);
            request.setEntity(stringEntity);

            HttpResponse response = httpClient.execute(request);

            checkResponse(response);

            rw = httpResponseToWrapper(response, httpClient);
        } catch (SocketTimeoutException e) {
            throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        } catch (IOException e) {
            throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        }
        return rw;
    }

    @Override
    public ResponseWrapper httpPost(String url, Map<String, String> getParams,
            Bundle postParams, Bundle headers, IDownloadState callback)
            throws HttpException {
        ResponseWrapper rw = null;

        try {
            HttpClient httpClient = getHttpClient();
            url = NetworkUtils.getCompleteUrl(url, getParams);
            HttpPost request = new HttpPost(url);
            request.setEntity(createEntity(postParams, callback));
            HttpResponse response = httpClient.execute(request);

            checkResponse(response);
            rw = httpResponseToWrapper(response, httpClient);
        } catch (SocketTimeoutException e) {
            throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        } catch (UnsupportedEncodingException e) {
            throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        } catch (IOException e) {
            throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        }
        return rw;
    }
    
    private HttpEntity createEntity(Bundle postParams, IDownloadState callback) throws UnsupportedEncodingException{
    	HttpEntity entity = null;
    	if(postParams == null){
    		return null;
    	}
    	boolean isMultipart = postParams.getBoolean(AbstractRequestParams.IS_MULTIPART);
    	postParams.remove(AbstractRequestParams.IS_MULTIPART);
        if(isMultipart){
            if (callback != null) {
            	entity = new CustomMultiPartEntity(callback);
            } else {
            	entity = new MultipartEntity();
            }
            setupMultipartEntity((MultipartEntity)entity, postParams);
        }else{
        	List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        	for(String key : postParams.keySet()){
        		params.add(new BasicNameValuePair(key,postParams.getString(key)));
        	}
        	entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        }
        
    	return entity;
    }

    private void checkResponse(HttpResponse response) throws HttpException {
        int code = response.getStatusLine().getStatusCode();
        if (code != HttpStatus.SC_OK) {
        	NetworkUtils.HandleResponseStatusCode(code, new Response(response));
        }
    }
    
    private ResponseWrapper httpResponseToWrapper(HttpResponse response,
            HttpClient client) throws IllegalStateException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        Header[] headers = response.getAllHeaders();
        ResponseWrapper rw = new ResponseWrapper.Builder()
                .setCode(statusLine.getStatusCode())
                .setVersion(statusLine.getProtocolVersion().toString())
                .setContent(entity.getContent())
                .setHeaders(headToHashMap(headers)).setConnection(client)
                .build();

        return rw;
    }

    private static HashMap<String, String> headToHashMap(Header[] headers) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (Header header : headers) {
            map.put(header.getName(), header.getValue());
        }
        return map;
    }

    /**
     * 装载请求头
     * 
     * @param request
     * @param headers
     */
    private void setupHeaders(HttpRequestBase request, Bundle headers) {
        if (headers == null) {
            return;
        }
        Set<String> keys = headers.keySet();
        if (keys.size() > 0) {
            for (String key : keys) {
                request.addHeader(key, headers.getString(key));
            }
        }
    }

    /**
     * 装载混合类型参数到Post的Entity
     * 
     * @param multipartContent
     * @param bundle
     * @throws UnsupportedEncodingException
     */
    private void setupMultipartEntity(MultipartEntity multipartContent,
            Bundle bundle) throws UnsupportedEncodingException {
        for (final String key : bundle.keySet()) {
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
                            FileBody bin;
                            if (NetworkUtils.TYPE_FILE_NAME.equals(key)) {
                                bin = new FileBody(mFile, "image/jpeg");
                            } else {
                                bin = new FileBody(mFile, "application/zip");
                            }
                            multipartContent.addPart(mFilePathBundleKey, bin);
                        }
                    }
                }
            } else {
                String value = bundle.getString(key);
                StringBody mStringBody = null;
                try {
                    mStringBody = new StringBody(value == null ? "" : value,
                            Charset.forName(HTTP.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                multipartContent.addPart(URLEncoder.encode(key, "UTF_8"),
                        mStringBody);
            }
        }
    }

    /**
     * 获取一个SSlSocketFactoryEx对象
     * 
     * @return
     * @throws NetworkException
     */
    private SSLSocketFactory getSSlSocketFactoryEx() throws HttpException {
        KeyStore mKeyStore = null;
        SSLSocketFactory mSslSocketFactory = null;
        try {
            mKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            mKeyStore.load(null, null);
            mSslSocketFactory = new SSLSocketFactoryEx(mKeyStore);
        } catch (Exception e) {
            throw new HttpException(HttpException.MSG_NETWORK_ERROR, e);
        }
        mSslSocketFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return mSslSocketFactory;
    }

    /**
     * 扩展MultipartEntity 添加上传进度
     */
    public static class CustomMultiPartEntity extends MultipartEntity {

        private final IDownloadState listener;

        public CustomMultiPartEntity(final IDownloadState listener) {
            super();
            this.listener = listener;
        }

        public CustomMultiPartEntity(final HttpMultipartMode mode,
                final IDownloadState listener) {
            super(mode);
            this.listener = listener;
        }

        public CustomMultiPartEntity(HttpMultipartMode mode,
                final String boundary, final Charset charset,
                final IDownloadState listener) {
            super(mode, boundary, charset);
            this.listener = listener;
        }

        @Override
        public void writeTo(final OutputStream outstream) throws IOException {
            super.writeTo(new CountingOutputStream(outstream, this.listener,
                    getContentLength()));
        }

        public static class CountingOutputStream extends FilterOutputStream {

            private final IDownloadState listener;
            private long transferred;
            private long mContentLength;

            public CountingOutputStream(final OutputStream out,
                    final IDownloadState listener, final long contentLength) {
                super(out);
                this.listener = listener;
                this.transferred = 0;
                this.mContentLength = contentLength;
            }

            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
                out.flush();
                if (len > 0) {
                    this.transferred += len;

                    this.listener
                            .onProgressChanged((this.transferred * 100 / mContentLength));
                }
            }

            public void write(int b) throws IOException {
                out.write(b);
                out.flush();
                this.transferred++;
            }
        }
    }

}
