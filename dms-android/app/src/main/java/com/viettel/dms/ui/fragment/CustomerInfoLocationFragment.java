package com.viettel.dms.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class CustomerInfoLocationFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ICustomerInfoLocationView {

    private static String PARAM_DATA = "PARAM_DATA";
    private static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 2015;

    private CustomerSummary mData;

    private double mLatitude;
    private double mLongtitude;
    private String mIdCustomer;
    private String mCustomerName;
    private double lastLatitude, lastLongitude;
    private boolean canEditLocation;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mGoogleMap;
    private static Marker mMarker;

    @Bind(R.id.google_map)
    MapView mMapView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private MenuItem itemEdit, itemDone;
    private Dialog mProgressDialog;

    private boolean isEditing = false;

    CustomerInfoLocationPresenter presenter;

    public static CustomerInfoLocationFragment newInstance(CustomerSummary data) {
        CustomerInfoLocationFragment fragment = new CustomerInfoLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CustomerInfoLocationFragment() {
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
        createGoogleApiClient();
        createLocationRequest();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_info_location, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.customer_info_title);
        mMapView.onCreate(savedInstanceState);
        mFab.hide(false);
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
                mFab.show(true);
                if (mMarker != null) {
                    mMarker.setDraggable(true);
                }
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
        if (GooglePlayUtils.checkPlayService(context, getActivity())) {
            MapsInitializer.initialize(this.getActivity());
            setUpMapIfNeeded();
            zoomToMyLocation(new LatLng(mLatitude, mLongtitude), isEditing);
        }
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
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void setUpMapIfNeeded() {
        try {
            if (mMapView != null)
                mGoogleMap = mMapView.getMap();
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Close Soft key
                    ((BaseActivity) getActivity()).closeSoftKey();
                }
            });
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

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

    @OnClick(R.id.fab)
    void doClickLocation() {
        LocationManager locationManager = LocationHelper.getLocationService(context);
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationHelper.showAlertNoLocationService(context);
        }
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
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

    private void zoomToMyLocation(LatLng latLng, boolean dragable) {
        if (mGoogleMap != null) {
            // Update last location
            lastLongitude = latLng.longitude;
            lastLatitude = latLng.latitude;
            mGoogleMap.clear();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions option = new MarkerOptions();
            option.position(latLng);
            option.title(mCustomerName);
            option.draggable(dragable);
            mMarker = mGoogleMap.addMarker(option);
            mMarker.showInfoWindow();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("DMS Location Failed", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            zoomToMyLocation(new LatLng(location.getLatitude(), location.getLongitude()), isEditing);
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
            mMarker.showInfoWindow();
        }
    }


    @Override
    public void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
