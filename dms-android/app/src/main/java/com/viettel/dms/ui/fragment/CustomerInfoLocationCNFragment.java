package com.viettel.dms.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.melnykov.fab.FloatingActionButton;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.GooglePlayUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.location.LocationHelper;
import com.viettel.dms.presenter.CustomerInfoLocationPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.ICustomerInfoLocationView;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CustomerSummary;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerInfoLocationCNFragment extends BaseFragment implements ICustomerInfoLocationView {

    private static String PARAM_DATA = "PARAM_DATA";

    private CustomerSummary mData;
    private double mLatitude;
    private double mLongtitude;
    private String mIdCustomer;
    private String mCustomerName;

    private double lastLatitude, lastLongitude;
    private boolean canEditLocation;

    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
    LocationClient mLocationClient;
    public MyLocationListenner myListener;
    private static Marker mMarker;
    BaiduMap baiduMap;
    @Bind(R.id.baidu_map)
    MapView mMapView;

    @Bind(R.id.fab)
    FloatingActionButton mFab;
    private MenuItem itemEdit, itemDone;
    private Dialog mProgressDialog;

    private boolean isEditing = false;

    CustomerInfoLocationPresenter presenter;

    public static CustomerInfoLocationCNFragment newInstance(CustomerSummary data) {
        CustomerInfoLocationCNFragment fragment = new CustomerInfoLocationCNFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CustomerInfoLocationCNFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelable(PARAM_DATA);

        }
        if (mData != null) {
            mLatitude = mData.getLocation().getLatitude();
            mLongtitude = mData.getLocation().getLongitude();
            canEditLocation = OAuthSession.getDefaultSession().getUserInfo().isCanEditCustomerLocation();
            mIdCustomer = mData.getId();
            mCustomerName = mData.getName();

            this.lastLatitude = this.mLatitude;
            this.lastLongitude = this.mLongtitude;
        }
        presenter = new CustomerInfoLocationPresenter(this);
        mLocationClient = new LocationClient(context);
        myListener = new MyLocationListenner();
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setScanSpan(1000);
        option.setIsNeedAddress(false);
        mLocationClient.setLocOption(option);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_info_location_cn, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.customer_info_title);
        mFab.hide(false);
        setUpMapIfNeeded();
        zoomToMyLocation(new LatLng(lastLatitude, lastLongitude), false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ButterKnife.unbind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (canEditLocation) {
            inflater.inflate(R.menu.mb_menu_customer_info_location, menu);
            itemEdit = menu.findItem(R.id.action_edit);
            itemDone = menu.findItem(R.id.action_done);
            updateMenu();
        }
    }

    private void updateMenu() {
        if (isEditing) {
            itemEdit.setVisible(false);
            itemDone.setVisible(true);
        } else {
            itemEdit.setVisible(true);
            itemDone.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit: {
                isEditing = true;
                updateMenu();
                if (mMarker != null) {
                    mMarker.setDraggable(true);
                }
                mFab.show(true);
                return true;
            }

            case R.id.action_done: {
                if (lastLatitude == mLatitude && lastLongitude == mLongtitude) { // not change location
                    Toast.makeText(context, R.string.customer_info_update_location_unchange, Toast.LENGTH_LONG).show();
                    updateFinish();
                } else {
                    presenter.updateCustomerLocation(mIdCustomer, lastLatitude, lastLongitude);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void setUpMapIfNeeded() {
        if (baiduMap == null) {
            baiduMap = mMapView.getMap();
        }
        try {
            baiduMap.setMyLocationEnabled(false);
            baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Close Soft key
                    ((BaseActivity) getActivity()).closeSoftKey();
                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });
            baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(Marker marker) {
                    baiduMap.hideInfoWindow();
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng lastPosition = marker.getPosition();
                    lastLatitude = lastPosition.latitude;
                    lastLongitude = lastPosition.longitude;
                    showMyWindowInfo(lastPosition);
                }

                @Override
                public void onMarkerDragStart(Marker marker) {
                    baiduMap.hideInfoWindow();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void zoomToMyLocation(LatLng latLng, boolean dragable) {
        if (baiduMap != null) {
            // Update last location
            if (latLng == null) return;
            baiduMap.clear();
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 15.0f);
            OverlayOptions overlay = new MarkerOptions().position(latLng).icon(markerIcon).draggable(dragable).anchor(0.5f, 0.5f);
            mMarker = (Marker) baiduMap.addOverlay(overlay);

            baiduMap.animateMapStatus(update, 2000);
            showMyWindowInfo(latLng);
        }
    }

    private void showMyWindowInfo(LatLng latLng) {
        Button btn = new Button(context);
        btn.setBackgroundResource(R.drawable.popup);
        btn.setText(mCustomerName);
        InfoWindow window = new InfoWindow(btn, latLng, -20);
        baiduMap.showInfoWindow(window);
    }

    @OnClick(R.id.fab)
    void doClickLocation() {
        LocationManager locationManager = LocationHelper.getLocationService(context);
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationHelper.showAlertNoLocationService(context);
        } else {
            mLocationClient.start();
        }
    }


    @Override
    public void showLoadingDialog() {
        mProgressDialog = DialogUtils.showProgressDialog(context, null, getString(R.string.message_update_data), true);
    }

    @Override
    public void broadcastUpdate(String customerId, double lat, double longt) {
        Toast.makeText(context, R.string.customer_info_update_location_successful, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_UPDATE_LOCATION);
        intent.putExtra("id", customerId);
        intent.putExtra("lat", lat);
        intent.putExtra("long", longt);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void updateFailMessage() {
        Toast.makeText(context, R.string.customer_info_update_location_unsuccessful, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateFinish() {
        isEditing = false;
        mFab.hide(true);
        updateMenu();
        if (mMarker != null) {
            mMarker.setDraggable(false);
        }
    }


    @Override
    public void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null)
                return;
            lastLongitude = location.getLongitude();
            lastLatitude = location.getLatitude();
            zoomToMyLocation(new LatLng(location.getLatitude(), location.getLongitude()), true);
            mLocationClient.stop();

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
