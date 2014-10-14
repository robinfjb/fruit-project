package com.robin.fruitlib.http;
/**
 * @Description 下载进度回调
 */
public interface IDownloadState {

    public void onStart(Object arg);
    
    public void onProgressChanged(final float percent);
    
    public void onComplete(Object arg);
    
    public void onFail(Object arg);
}
