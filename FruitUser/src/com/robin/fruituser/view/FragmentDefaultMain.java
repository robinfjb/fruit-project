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
import android.widget.Toast;

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
import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.io.FruitApi;
import com.robin.fruitlib.task.RGenericTask;
import cn.sgone.fruituser.R;
import com.robin.fruituser.UserApplication;
import com.robin.fruituser.activity.HomeActivity;
import com.robin.fruitlib.bean.SellerMapBean;

public class FragmentDefaultMain extends BaseFragment implements LocationSource,AMapLocationListener,
InfoWindowAdapter,OnMarkerClickListener,OnInfoWindowClickListener{
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation currentLocation;
	private MarkerOptions currentoption;

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
	 * ����һЩamap������
	 */
	private void setUpMap() {
		// �Զ���ϵͳ��λС����
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.self_s1));// ����С�����ͼ��
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 180));// ����Բ�εı߿���ɫ
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 180));// ����Բ�ε������ɫ
		// myLocationStyle.anchor(int,int)//����С�����ê��
//		myLocationStyle.strokeWidth(1.0f);// ����Բ�εı߿��ϸ
//		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
	   // aMap.setMyLocationType()
		aMap.animateCamera(CameraUpdateFactory.zoomTo(16));
		aMap.setInfoWindowAdapter(this);// ���õ��marker�¼�������
		aMap.setOnMarkerClickListener(this);// ���õ��marker�¼�������
		aMap.setOnInfoWindowClickListener(this);// ���õ��infoWindow�¼�������
	}
	
	/**
	 * debug��
	 */
	private void initData() {
		aMap.addMarker(new MarkerOptions().anchor(1f, 1f)
				.position(new LatLng(31.2238295291, 121.5317889949)).title("С��ˮ����")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_s1))
				.snippet("С��ˮ�����ṩƻ��").draggable(true));
		aMap.addMarker(new MarkerOptions().anchor(1f, 1f)
				.position(new LatLng(31.2280777376,121.5207541080)).title("С��ˮ����")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_s1))
				.snippet("С��ˮ�����ṩ�㽶").draggable(true));
		aMap.addMarker(new MarkerOptions().anchor(1f, 1f)
				.position(new LatLng(31.2286957376,121.5194251080)).title("С��ˮ����")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_s1))
				.snippet("С��ˮ�����ṩը��").draggable(true));
	}
	
	/**
	 * �õ��̻���Ϣ
	 * @param lat
	 * @param longt
	 */
	public void getShopInfo(double lat, double longt) {
		GetShopByLocationTask task = new GetShopByLocationTask(getActivity(), lat, longt);
		task.execute();
	}
	
	public void changeCamera(double lat, double longt) {
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(
				new CameraPosition(new LatLng(lat, longt), 16, 0, 0));
		aMap.moveCamera(update);
	}
	
	@Override
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
			mListener.onLocationChanged(aLocation);// ��ʾϵͳС����
		}
		if(currentLocation != null && (currentLocation.getLatitude() == aLocation.getLatitude()
				&& currentLocation.getLongitude() == aLocation.getLongitude())) {
			return;
		}
		currentLocation = aLocation;
		double lat = currentLocation.getLatitude();
		double lon = currentLocation.getLongitude();
//		Log.d("FragmentDefaultMain", "onLocationChanged:lat=" + lat + ";lon=" + lon);
		UserApplication.currentLocation = new LatLng(lat, lon);
		aMap.clear();
		currentoption = new MarkerOptions();
		currentoption.anchor(1f, 1f);
		currentoption.position(new LatLng(lat, lon));
		currentoption.icon(BitmapDescriptorFactory.fromResource(R.drawable.self_s1));
		currentoption.draggable(false);
		aMap.addMarker(currentoption);
		aMap.invalidate();
		getShopInfo(lat, lon);
	}
	
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true Location
			 * API��λ����GPS�������϶�λ��ʽ
			 * ����һ�������Ƕ�λprovider���ڶ�������ʱ�������2000���룬������������������λ���ף����ĸ������Ƕ�λ������
			 */
			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
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
//			Log.d("FragmentDefaultMain", "lat=" + lat + ";lon=" + lon);
//			getShopInfo(lat, lon);
			UserApplication.currentLocation = new LatLng(lat, lon);
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
	
	public void addMarkerBuyLocation(double lat, double lon, String address) {
		if(aMap == null)
			return;
		aMap.clear();
		MarkerOptions option = new MarkerOptions();
		option.anchor(1f, 1f);
		option.position(new LatLng(lat, lon));
		option.icon(BitmapDescriptorFactory.fromResource(R.drawable.self_s1));
		option.title("�͵��˵�ַ");
		option.snippet(address);
		option.draggable(false);
		aMap.addMarker(option);
		aMap.invalidate();
	}
	
	private class GetShopByLocationTask extends RGenericTask<ArrayList<SellerMapBean>> {
		private double lon;
		private double lat;
		public GetShopByLocationTask(Context ctx, double lon, double lat) {
			super(ctx);
			this.lon = lon;
			this.lat = lat;
		}

		@Override
		protected ArrayList<SellerMapBean> getContent() throws HttpException {
			FruitApi api = new FruitApi(ctx);
			return api.getMapSellerList(lon, lat);
		}

		@Override
		protected void onSuccess(ArrayList<SellerMapBean> result) {
			for (SellerMapBean sellerMapBean : result) {
				MarkerOptions option = new MarkerOptions();
				option.anchor(1f, 1f);
				option.position(new LatLng(Double.parseDouble(sellerMapBean.addX), Double.parseDouble(sellerMapBean.addY)));
				option.icon(BitmapDescriptorFactory.fromResource(R.drawable.bussins_s1));
				option.title(sellerMapBean.shopName);
				option.snippet(sellerMapBean.address);
				option.draggable(true);
				aMap.addMarker(option);
			}
			aMap.invalidate();
		}
		
		@Override
		protected void onAnyError(int code, String msg) {
			Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onTaskBegin() {
			((HomeActivity)getActivity()).showProgressBar();
		}

		@Override
		protected void onTaskFinished() {
			((HomeActivity)getActivity()).hideProgressBar();
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
