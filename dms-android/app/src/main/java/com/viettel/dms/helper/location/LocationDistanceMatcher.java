package com.viettel.dms.helper.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author Thanh
 * @since 3/10/2015
 */
public class LocationDistanceMatcher implements LocationListener {

    private Context mContext;

    private float mMaximunDistance;
    private float mMaximumAccuracy;
    private long mTimeout;
    private Location lastKnowLocation;
    private String mLocationProvider;
    private LocationDistanceMatcherListener mListener;
    private Location mRefLocation;

    private Thread timeOutThread;
    private Boolean finished;

    public LocationDistanceMatcher(Context context, float mMaximunDistance, long timeout,
                                   double lattitude, double longitude,
                                   LocationDistanceMatcherListener listener) {
        this.mContext = context;
        this.mMaximunDistance = mMaximunDistance;
        this.mMaximumAccuracy = mMaximunDistance / 2;
        this.mTimeout = timeout;
        this.mListener = listener;

        boolean notGpsOrNetwork = true;
        if (!LocationHelper.isHaveRequiredPermission(context)) {
            onMatchFail();
            return;
        }
        if (LocationHelper.isGpsProviderEnabled(mContext)) {
            notGpsOrNetwork = false;
            mLocationProvider = LocationManager.GPS_PROVIDER;
            LocationHelper.addLocationUpdatesListener(mContext, LocationManager.GPS_PROVIDER, this);
        }
        if (LocationHelper.isNetworkProviderEnabled(mContext)) {
            notGpsOrNetwork = false;
            mLocationProvider = LocationManager.NETWORK_PROVIDER;
            LocationHelper.addLocationUpdatesListener(mContext, LocationManager.NETWORK_PROVIDER, this);
        }
        if (notGpsOrNetwork) {
            onMatchFail();
            return;
        }
        this.finished = Boolean.FALSE;

        this.mRefLocation = new Location(mLocationProvider);
        this.mRefLocation.setLatitude(lattitude);
        this.mRefLocation.setLongitude(longitude);

        if (timeout > 0) {
            timeOutThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(mTimeout);
                    } catch (InterruptedException e) {
                        ;
                    }
                    synchronized (this) {
                        if (finished) {
                            return;
                        }
                        finished = Boolean.TRUE;
                    }
                    stopListening();
                    if (isLocationMatch(lastKnowLocation)) {
                        onMatchSuccess(lastKnowLocation);
                    } else {
                        onMatchFail();
                    }
                }
            };
            timeOutThread.start();
        }
    }

    private void onMatchFail() {
        Handler mainHandler = new Handler(mContext.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onLocationMatchFail(lastKnowLocation);
            }
        });
    }

    private void onMatchSuccess(final Location location) {
        Handler mainHandler = new Handler(mContext.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onLocationMatchSuccess(location);
            }
        });
    }

    private boolean isLocationMatch(Location location) {
        if (location == null
                || location.getAccuracy() > mMaximumAccuracy
                || mRefLocation.distanceTo(location) > this.mMaximunDistance) {
            return false;
        }
        return true;
    }

    private boolean isAccuracyEnough(Location location) {
        return location.getAccuracy() <= mMaximumAccuracy;
    }

    public void stop() {
        synchronized (this) {
            if (finished) {
                return;
            }
            finished = Boolean.TRUE;
            if (timeOutThread != null && timeOutThread.isAlive()) {
                timeOutThread.interrupt();
            }
            stopListening();
        }
    }

    private void stopListening() {
        LocationHelper.removeLocationUpdatesListener(mContext, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isLocationMatch(location)) {
            stop();
            onMatchSuccess(location);
            return;
        }
        if (lastKnowLocation == null
                || LocationHelper.isBetterLocation(location, lastKnowLocation)) {
            lastKnowLocation = location;
        }
        if (isAccuracyEnough(lastKnowLocation)) {
            stop();
            onMatchFail();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            if (mListener != null) {
                stop();
                mListener.onTurnOffLocation();
            }
        }
    }

}
