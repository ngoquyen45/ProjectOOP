package com.viettel.dms.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.baidu.mapapi.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.viettel.dms.R;
import com.viettel.dms.helper.GooglePlayUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.MapWrapperLayout;
import com.viettel.dms.helper.layout.OnInfoWindowElemTouchListener;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.layout.ViewCompat;
import com.viettel.dms.ui.activity.CustomerInfoActivity;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.Location;
import com.viettel.dmsplus.sdk.models.UserInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class CustomerRouteCNFragment extends BaseFragment {
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private boolean mapSupported = true;

    private LatLngBounds mLatLngBounds;
    private HashMap<Marker, CustomerForVisit> mMarkersHashMap;
    private CustomerForVisit[] mPlannedCustomers;
    private LayoutInflater layoutInflater;
    int colorPrimary, colorSecondary;

    TextView tvName, tvAddress, tvPhone;
    Button btnViewMore, btnVisitNow;
    ViewGroup view;
    OnInfoWindowElemTouchListener onClickViewMore, onClickVisitNow;
    BitmapDescriptor visitedBitMapDescriptor, unvisitBitMapDescriptor;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            calculateLatLngBound();
        }
    };
    private static String PARAM_LOC = "PARAM_LOC";

    public static CustomerRouteCNFragment newInstance(CustomerForVisit[] info) {
        CustomerRouteCNFragment fragment = new CustomerRouteCNFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(PARAM_LOC, info);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CustomerRouteCNFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMarkersHashMap = new HashMap<>();
        layoutInflater = getLayoutInflater(savedInstanceState);
        if (getArguments() != null) {
            mPlannedCustomers = (CustomerForVisit[]) getArguments().getParcelableArray(PARAM_LOC);
        }
        setHasOptionsMenu(true);
        colorPrimary = ThemeUtils.getColor(context, R.attr.colorPrimary);
        colorSecondary = ThemeUtils.getColor(context, R.attr.colorSecondary);
        LocalBroadcastManager.getInstance(context).registerReceiver(updateLocationReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_UPDATE_LOCATION));
        LocalBroadcastManager.getInstance(context).registerReceiver(updateVisitStatusReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_STATUS_CHANGED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_route_cn, container, false);

        mapView = (MapView) view.findViewById(R.id.id_map_route);
        setTitleResource(R.string.customer_route_title);

        visitedBitMapDescriptor = BitmapDescriptorFactory.fromBitmap(tintColorToMarker(HardCodeUtil.CustomerVisitStatus.VISITED));
        unvisitBitMapDescriptor = BitmapDescriptorFactory.fromBitmap(tintColorToMarker(HardCodeUtil.CustomerVisitStatus.NOT_VISITED));
        setUpMapIfNeeded();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeMap();
    }

    void initializeMap() {
        if (mapSupported) {
            setUpMapIfNeeded();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void setUpMapIfNeeded() {
        try {
            mBaiduMap = mapView.getMap();
            mBaiduMap.setMyLocationEnabled(false);
            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mBaiduMap.hideInfoWindow();
                    showWindowInfo(marker);
                    return true;
                }
            });
            mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mBaiduMap.hideInfoWindow();
                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });
            mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mHandler.sendEmptyMessage(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateLatLngBound() {
        if (mPlannedCustomers.length > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLng ll;
            for (CustomerForVisit myMarkerInfo : mPlannedCustomers) {
                // Create user marker with custom icon and other options
                ll = new LatLng(myMarkerInfo.getLocation().getLatitude(), myMarkerInfo.getLocation().getLongitude());
                OverlayOptions markerOptions = new MarkerOptions().icon(myMarkerInfo.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED ? visitedBitMapDescriptor : unvisitBitMapDescriptor)
                        .position(ll)
                        .anchor(0.5f, 0.5f).draggable(false);
                Marker currentMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
                mMarkersHashMap.put(currentMarker, myMarkerInfo);
                builder.include(ll);
            }
            final LatLngBounds bounds = builder.build();
            mapView.post(new Runnable() {
                @Override
                public void run() {
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
                    mBaiduMap.setMapStatus(u);
                }
            });
        }
    }


    private void showWindowInfo(final Marker marker) {
        View view = layoutInflater.inflate(R.layout.adapter_customer_route_window_item_cn, null);
        tvName = (TextView) view.findViewById(R.id.id_name);
        tvAddress = (TextView) view.findViewById(R.id.id_address);
        tvPhone = (TextView) view.findViewById(R.id.id_phone);
        btnViewMore = (Button) view.findViewById(R.id.id_view_more);
        btnVisitNow = (Button) view.findViewById(R.id.id_visit_now);

        final CustomerForVisit info = mMarkersHashMap.get(marker);

        btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CustomerInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(CustomerVisitFragment.EXTRA_ID, info.getId());
                startActivity(intent);
            }
        });
        btnVisitNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerVisitFragment fragment = CustomerVisitFragment.newInstance(info);
                replaceCurrentFragment(fragment);
            }
        });
        int status = info.getVisitStatus();
        if (status == 0 || status == 1) {
            tvName.setTextColor(colorPrimary);
            btnVisitNow.setVisibility(View.VISIBLE);
            btnViewMore.setVisibility(View.VISIBLE);
        } else {
            tvName.setTextColor(colorSecondary);
            btnVisitNow.setVisibility(View.GONE);
            btnViewMore.setVisibility(View.VISIBLE);
        }
        SetTextUtils.setText(tvName, info.getName());
        SetTextUtils.setText(tvAddress, info.getAddress());
        SetTextUtils.setText(tvPhone, info.getMobile());

        InfoWindow window = new InfoWindow(view, marker.getPosition(), -10);
        mBaiduMap.showInfoWindow(window);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mapView.onDestroy();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(updateLocationReceiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(updateVisitStatusReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    BroadcastReceiver updateLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String customerId = intent.getStringExtra("id");
            double latitude = intent.getDoubleExtra("lat", 0f);
            double longitude = intent.getDoubleExtra("long", 0f);
            for (CustomerForVisit v : mPlannedCustomers) {
                if (v.getId().equalsIgnoreCase(customerId)) {
                    v.setLocation(new Location(latitude, longitude));
                    break;
                }
            }
            setUpMapIfNeeded();
            for (Marker m : mMarkersHashMap.keySet()) {
                CustomerForVisit v = mMarkersHashMap.get(m);
                if (v.getId().equalsIgnoreCase(customerId)) {
                    m.setPosition(new LatLng(latitude, longitude));
                    showWindowInfo(m);
                    break;
                }
            }

        }
    };
    BroadcastReceiver updateVisitStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String customerId = intent.getStringExtra("customerId");
            int visitStatus = intent.getIntExtra("visitStatus", HardCodeUtil.CustomerVisitStatus.NOT_VISITED);
            for (CustomerForVisit v : mPlannedCustomers) {
                if (v.getId().equalsIgnoreCase(customerId)) {
                    v.setVisitStatus(visitStatus);
                    break;
                }
            }
        }
    };

    Bitmap tintColorToMarker(int status) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_radio_button_on_grey600_18dp);
        Bitmap mutableBitmap = convertToMutable(bitmap);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        if (status == HardCodeUtil.CustomerVisitStatus.NOT_VISITED || status == HardCodeUtil.CustomerVisitStatus.VISITING)
            paint.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
        else
            paint.setColorFilter(new PorterDuffColorFilter(colorSecondary, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mutableBitmap, new Matrix(), paint);
        return mutableBitmap;
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }
}
