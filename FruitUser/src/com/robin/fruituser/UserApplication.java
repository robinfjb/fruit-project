package com.robin.fruituser;

import android.util.Pair;

import com.amap.api.maps2d.model.LatLng;
import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.bean.CommonShopBean;
import com.robin.fruitlib.bean.DeviceBean;
import com.robin.fruitlib.bean.OrderBean;
import com.robin.fruitlib.data.FruitPerference;

public class UserApplication extends BaseApplication{
	public static DeviceBean deviceBean = new DeviceBean();
	public static LatLng currentLocation = null;
	public static OrderBean shop = new OrderBean();
	public static LatLng buyLocation = null;
	 @Override
    public void onCreate() {
		UserApplication.deviceBean.timeStamp = FruitPerference.getDeviceTimeStamp(this);
        super.onCreate();
    }
}
