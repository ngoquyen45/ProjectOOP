package com.viettel.dms.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Spinner;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.GooglePlayUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.location.LocationHelper;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.RegisterCustomerPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.IRegisterCustomerView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CategorySimple;
import com.viettel.dmsplus.sdk.models.CategorySimpleResult;
import com.viettel.dmsplus.sdk.models.CustomerRegisterModel;
import com.viettel.dmsplus.sdk.models.UserInfo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class RegisterCustomerFragment extends BaseFragment implements IRegisterCustomerView, GoogleApiClient.ConnectionCallbacks, View.OnFocusChangeListener
            , GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

        @Bind(R.id.edt_Name)
        MaterialEditText edtName;
        @Bind(R.id.edt_Address)
        MaterialEditText edtAddress;
        @Bind(R.id.edt_Mobile)
        MaterialEditText edtMobile;
        @Bind(R.id.edt_Home)
        MaterialEditText edtHome;
        @Bind(R.id.edt_Email)
        MaterialEditText edtEmail;
        @Bind(R.id.edt_Representative)
        MaterialEditText edtRepresentative;
        @Bind(R.id.edt_Note)
        MaterialEditText edtNote;
        @Bind(R.id.google_map)
        MapView mMapView;
        @Bind(R.id.sp_Customer_Type)
        Spinner spCustomerType;
        @Bind(R.id.sp_Customer_Area)
        Spinner spCustomerArea;
        @Bind(R.id.tv_hl_Customer_Type)
        TextView tvHightLightCustomerType;
        @Bind(R.id.tv_hl_Customer_Area)
        TextView tvHightLightCustomerArea;

        RegisterCustomerPresenter presenter;
        Dialog mProgressDialog;

        double lastLatitude = 0, lastLongitude = 0;
        private LocationRequest mLocationRequest;
        private GoogleApiClient mGoogleApiClient;
        private GoogleMap mGoogleMap;
        private static Marker locationMaker;

        private CustomerRegisterModel postData;
        private MySpinnerAdaper spCustomerTypeAdapter;
        private MySpinnerAdaper spCustomerAreaAdapter;

        private static int colorFocus, colorUnfocus;

        public static RegisterCustomerFragment newInstance() {
            RegisterCustomerFragment fragment = new RegisterCustomerFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public RegisterCustomerFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            postData = new CustomerRegisterModel();
            createGoogleApiClient();
            createLocationRequest();
            presenter = new RegisterCustomerPresenter(this);
            presenter.processToGetCustomerType();
            presenter.processToGetArea();
            colorFocus = ThemeUtils.getColor(context, R.attr.colorPrimary);
            colorUnfocus = ContextCompat.getColor(context, R.color.Black26);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (getResources().getBoolean(R.bool.is_tablet)) setPaddingLeft(104f);
            View view = inflater.inflate(R.layout.fragment_customer_register, container, false);
            ButterKnife.bind(this, view);
            setTitleResource(R.string.register_customer_title);
            mMapView.onCreate(savedInstanceState);
            spCustomerType.setOnFocusChangeListener(this);
            spCustomerArea.setOnFocusChangeListener(this);
            spCustomerType.setFocusableInTouchMode(true);
            spCustomerArea.setFocusableInTouchMode(true);
            return view;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
//        ButterKnife.unbind(this);
        }

        private void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        private void createGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        @Override
        public void onPause() {
            super.onPause();
            mMapView.onPause();
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            mMapView.onResume();
            if (GooglePlayUtils.checkPlayService(context, getActivity())) {
                MapsInitializer.initialize(this.getActivity());
                setUpMapIfNeeded();
            }
            mGoogleApiClient.connect();
        }

        private void collectDataAndPost() {
            // Collect data
            postData.setName(edtName.getText().toString());
            postData.setMobile(edtMobile.getText().toString());
            postData.setPhone(edtHome.getText().toString());
            postData.setContact(edtRepresentative.getText().toString());
            postData.setEmail(edtEmail.getText().toString());
            postData.setLocation(new com.viettel.dmsplus.sdk.models.Location(lastLatitude, lastLongitude));

            int pos = spCustomerType.getSelectedItemPosition();
            postData.setCustomerTypeId(spCustomerTypeAdapter.getId(pos));
            int pos2 = spCustomerArea.getSelectedItemPosition();
            postData.setAreaId(spCustomerAreaAdapter.getId(pos2));
            // Post
            presenter.processRegisterCustomer(postData);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mMapView.onLowMemory();
        }

        @OnClick(R.id.fab)
        void onClick() {
            LocationManager locationManager = LocationHelper.getLocationService(context);
            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                LocationHelper.showAlertNoLocationService(context);
            }
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, RegisterCustomerFragment.this);
            }
        }

        private void setUpMapIfNeeded() {
            if (mGoogleMap == null) {
                UserInfo userInfo = OAuthSession.getDefaultSession().getUserInfo();
                double defaultLatitude = userInfo.getLocation().getLatitude() != 0 ? userInfo.getLocation().getLatitude() : HardCodeUtil.Location.defaultLast;
                double defaultLongtitude = userInfo.getLocation().getLongitude() != 0 ? userInfo.getLocation().getLongitude() : HardCodeUtil.Location.defaultLong;
                mGoogleMap = mMapView.getMap();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(defaultLatitude, defaultLongtitude), 14.0f));
            }
            try {
                mGoogleMap.setMyLocationEnabled(false);
                LocationManager locationManager = LocationHelper.getLocationService(context);
                if (!locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LocationHelper.showAlertNoLocationService(context);
                }
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // Close Soft Keyboard
                        ((BaseActivity) getActivity()).closeSoftKey();
                        try {
                            mGoogleMap.clear();
                            lastLongitude = latLng.longitude;
                            lastLatitude = latLng.latitude;
                            LatLng newLocation = new LatLng(lastLatitude, lastLongitude);
                            locationMaker = mGoogleMap.addMarker(new MarkerOptions().position(newLocation).title(getString(R.string.register_customer_customer_location)).draggable(true));
                            locationMaker.showInfoWindow();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        ((BaseActivity) getActivity()).closeSoftKey();
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng lastPosition = marker.getPosition();
                        lastLatitude = lastPosition.latitude;
                        lastLongitude = lastPosition.longitude;
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void zoomToMyLocation(Location location) {
            try {
                mGoogleMap.clear();
                if (location == null) return;
                lastLatitude = location.getLatitude();
                lastLongitude = location.getLongitude();
                LatLng newLocation = new LatLng(lastLatitude, lastLongitude);
                locationMaker = mGoogleMap.addMarker(new MarkerOptions().position(newLocation).title(getString(R.string.register_customer_customer_location)).draggable(true));
                locationMaker.showInfoWindow();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 20));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private boolean checkValidateData() {
            if (StringUtils.isNullOrEmpty(edtName.getText().toString())) {
                edtName.setError(getString(R.string.register_customer_error_customer_name));
                edtName.requestFocus();
                return false;
            } else {
                edtName.setError(null);
            }
            if (spCustomerType.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
                DialogUtils.showMessageDialog(context, R.string.warning, R.string.register_customer_wrong_customer_type, backPress);
                return false;
            }
            if (spCustomerArea.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
                DialogUtils.showMessageDialog(context, R.string.warning, R.string.register_customer_wrong_customer_type, backPress);
                return false;
            }
            if (StringUtils.isNullOrEmpty(edtMobile.getText().toString())) {
                edtMobile.setError(getString(R.string.register_customer_error_mobile_phone));
                edtMobile.requestFocus();
                return false;
            } else {
                edtMobile.setError(null);
            }
            if (lastLatitude == 0 && lastLongitude == 0) {
                DialogUtils.showMessageDialog(context, R.string.warning, R.string.register_customer_wrong_location);
                return false;
            }

            return true;
        }

        private DialogInterface.OnClickListener backPress = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().onBackPressed();
            }
        };

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.mb_menu_action_done, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_done) {
                mProgressDialog = DialogUtils.showProgressDialog(context,
                        StringUtils.getStringOrNull(context, R.string.message_please_wait),
                        StringUtils.getStringOrNull(context, R.string.message_new_data), true);
                if (checkValidateData()) {
                    collectDataAndPost();
                    return true;
                } else {
                    dismissDialog();
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void showLoadingDialog() {
            if (mProgressDialog != null) {
                mProgressDialog = DialogUtils.showProgressDialog(context,
                        StringUtils.getStringOrNull(context, R.string.message_please_wait),
                        StringUtils.getStringOrNull(context, R.string.message_loading_data), true);
                mProgressDialog.show();
            }
        }

        @Override
        public void dismissDialog() {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }

        @Override
        public void getCustomerTypeSuccess() {
            spCustomerTypeAdapter = new MySpinnerAdaper(context, presenter.getSpCustomerTypeData());
            spCustomerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCustomerType.setAdapter(spCustomerTypeAdapter);
        }

        @Override
        public void getCustomerTypeError(SdkException info) {
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void getCustomerAreaSuccess() {
            spCustomerAreaAdapter = new MySpinnerAdaper(context, presenter.getSpCustomerAreaData());
            spCustomerAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCustomerArea.setAdapter(spCustomerAreaAdapter);
        }

        @Override
        public void getCustomerAreaError(SdkException info) {
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void createCustomerSuccess(CustomerRegisterModel customerRegisterModel) {
            Toast.makeText(context, R.string.register_customer_successful, Toast.LENGTH_LONG).show();
//      Broadcasting to new Customer
            Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_NEW_CUSTOMER_ADD);
            Bundle bundle = new Bundle();
            bundle.putParcelable("newCustomer", customerRegisterModel);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            dismissDialog();
            getActivity().onBackPressed();
        }

        @Override
        public void createCustomerError(SdkException info) {
            NetworkErrorDialog.processError(context, info);
        }


        @Override
        public void onConnected(Bundle bundle) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                zoomToMyLocation(location);
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if (view.getId() == spCustomerType.getId()) {
                changeFocusSeparator(tvHightLightCustomerType, b);
            }
            if (view.getId() == spCustomerArea.getId()) {
                changeFocusSeparator(tvHightLightCustomerArea, b);
            }
        }

        private void changeFocusSeparator(TextView tvHighLight, boolean focus) {
            if (focus) {
                tvHighLight.setTextColor(colorFocus);
            } else {
                tvHighLight.setTextColor(colorUnfocus);
            }
        }

        class MySpinnerAdaper extends ArrayAdapter<CategorySimple> {
            Context context;
            CategorySimpleResult visibleData = null;

            public MySpinnerAdaper(Context context, CategorySimpleResult data) {
                super(context, android.R.layout.simple_spinner_dropdown_item);
                this.context = context;
                this.visibleData = data;
            }

            private View getCustomView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                    SpinnerViewHolder holder = new SpinnerViewHolder();
                    holder.txt = (TextView) row.findViewById(android.R.id.text1);
                    row.setTag(holder);
                }
                if (row.getTag() instanceof SpinnerViewHolder) {
                    SpinnerViewHolder holder = (SpinnerViewHolder) row.getTag();
                    CategorySimple item = visibleData.getList()[position];
                    holder.txt.setText(item.getName());
                }
                return row;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
            }

            @Override
            public int getCount() {
                if (visibleData == null) return 0;
                else return visibleData.getList().length;
            }

            public String getId(int pos) {
                return visibleData.getList()[pos].getId();
            }
        }

        private static class SpinnerViewHolder {
            public TextView txt;
        }
}
