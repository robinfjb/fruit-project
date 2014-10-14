package com.robin.fruitlib.http;
/**
 * 
 * @Description 响应结果处理器接口，可根据需要实现本接口，将响应处理为不同类型
 */
interface ResponseProcessor<T> {
	public T processResponse(ResponseWrapper response,IDownloadState callback) throws HttpException;
}
