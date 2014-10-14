package com.robin.fruitlib.util;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;

public class GeoUtils {
	OnGeocodeSearchListener listner;
	public GeoUtils(OnGeocodeSearchListener listner) {
		this.listner = listner;
	}

	public void getAddress(Context context, final LatLonPoint latLonPoint) {
		GeocodeSearch geocoderSearch = new GeocodeSearch(context);
		geocoderSearch.setOnGeocodeSearchListener(listner);
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}
	
	public void getLatlon(Context context,final String name) {
		GeocodeSearch geocoderSearch = new GeocodeSearch(context);
		geocoderSearch.setOnGeocodeSearchListener(listner);
		GeocodeQuery query = new GeocodeQuery(name, "021");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}
}
