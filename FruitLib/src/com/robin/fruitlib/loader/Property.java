package com.robin.fruitlib.loader;

import com.robin.fruitlib.loader.Loader.ILoaderEvent;


public class Property<T> {
	public String key; // 图片路径
	public String dir; // 存储路径
	public long expiringIn; // 失效时间
	public ILoaderEvent<T> iLoaderEvent; //回调接口
	public Type dataType; //数据类型
	public T defaultData; //默认数据
	public int rotate;
	public enum Type{BITMAP,JSON,UNKNOW};
}
