package com.robin.fruituser.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.robin.fruitlib.base.BaseFragment;
import com.robin.fruitlib.bean.SendReturnBean.SendReturnSellerBean;
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruituser.R;

public class FragmentMapSuccess extends BaseFragment implements LocationSource,
InfoWindowAdapter,OnMarkerClickListener,OnInfoWindowClickListener{
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation currentLocation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		mapView = (MapView) view.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		init();
		setUpMap();
//		initData();
		return view;
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.self_s1));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 180));// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
//		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
//		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	   // aMap.setMyLocationType()
		aMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		aMap.setInfoWindowAdapter(this);// 设置点击marker事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
	}
	
	public void addMarker(final ArrayList<SendReturnSellerBean> list) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (SendReturnSellerBean sendReturnSellerBean : list) {
					MarkerOptions option = new MarkerOptions();
					option.anchor(1f, 1f);
					option.position(new LatLng(Double.parseDouble(sendReturnSellerBean.addX), Double.parseDouble(sendReturnSellerBean.addY)));
					option.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_red_s1));
					option.title(sendReturnSellerBean.shopName);
					option.snippet(sendReturnSellerBean.address);
					option.draggable(true);
					aMap.addMarker(option);
					aMap.invalidate();
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
	}
	
	private void initData() {
		/*aMap.addMarker(new MarkerOptions().anchor(1f, 1f)
				.position(new LatLng(31.2238295291, 121.5317889949)).title("小孙水果店")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_red_s1))
				.snippet("小孙水果店提供苹果").draggable(true));
		aMap.addMarker(new MarkerOptions().anchor(1f, 1f)
				.position(new LatLng(31.2280777376,121.5207541080)).title("小张水果店")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_red_s1))
				.snippet("小张水果店提供香蕉").draggable(true));
		aMap.addMarker(new MarkerOptions().anchor(1f, 1f)
				.position(new LatLng(31.2286957376,121.5194251080)).title("小留水果店")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_red_s1))
				.snippet("小留水果店提供炸弹").draggable(true));*/
	}
	
	public void addMarkerBuyLocation(double lat, double lon, String address) {
		if(aMap == null)
			return;
		MarkerOptions option = new MarkerOptions();
		option.anchor(1f, 1f);
		option.position(new LatLng(lat, lon));
		option.icon(BitmapDescriptorFactory.fromResource(R.drawable.self_s1));
		option.title("送到此地址");
		option.snippet(address);
		option.draggable(false);
		aMap.addMarker(option);
		aMap.invalidate();
	}
	
	public void changeCamera(double lat, double longt) {
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(
				new CameraPosition(new LatLng(lat, longt), 16, 0, 0));
		aMap.moveCamera(update);
	}
	
	/**
	 * 得到商户信息
	 * @param lat
	 * @param longt
	 */
	public void getShopInfo(double lat, double longt) {
		GetShopByLocationTask task = new GetShopByLocationTask(getActivity());
		task.execute();
	}
	
	/*@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		}
		if(currentLocation != null && (currentLocation.getLatitude() == aLocation.getLatitude()
				&& currentLocation.getLongitude() == aLocation.getLongitude())) {
			return;
		}
		currentLocation = aLocation;
		double lat = currentLocation.getLatitude();
		double lon = currentLocation.getLongitude();
		Log.d("FragmentDefaultMain", "onLocationChanged:lat=" + lat + ";lon=" + lon);
//		getShopInfo(lat, lon);
	}*/
	
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
//		if (mAMapLocationManager == null) {
//			mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
//			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
//		}
	}

	@Override
	public void deactivate() {
		mListener = null;
//		if (mAMapLocationManager != null) {
//			mAMapLocationManager.removeUpdates(this);
//			mAMapLocationManager.destory();
//		}
		mAMapLocationManager = null;
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		if(currentLocation != null) {
			double lat = currentLocation.getLatitude();
			double lon = currentLocation.getLongitude();
			Log.d("FragmentDefaultMain", "lat=" + lat + ";lon=" + lon);
//			getShopInfo(lat, lon);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	private class GetShopByLocationTask extends RGenericTask<ArrayList<String>> {
		public GetShopByLocationTask(Context ctx) {
			super(ctx);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ArrayList<String> getContent() throws HttpException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onSuccess(ArrayList<String> result) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onAnyError(int code, String msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onTaskBegin() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onTaskFinished() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Override
	public void onInfoWindowClick(Marker marker) {
	}
	
	@Override
	public boolean onMarkerClick(final Marker marker) {
		return false;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
		render(marker, infoWindow);
		return infoWindow;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View infoContent = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;
	}
	
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleUi.setTextSize(15);
			titleUi.setText(titleText);
		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetUi.setTextSize(18);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}
}
