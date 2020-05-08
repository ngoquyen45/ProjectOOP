package com.viettel.dms.helper.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;

import java.util.Calendar;

public class LocationHelper {

    public static boolean isNetworkProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isGpsProviderEnabled(Context context) {
        return isProviderEnabled(context, LocationManager.GPS_PROVIDER);
    }

    public static boolean isProviderEnabled(Context context, String provider) {
        LocationManager locationManager = getLocationService(context);
        try {
            return locationManager.isProviderEnabled(provider);
        } catch (SecurityException ex) {
            return false;
        }
    }

    public static LocationManager getLocationService(Context context) {
        return (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public static boolean isLocationServiceEnabled(Context context) {
        return isNetworkProviderEnabled(context)
                || isGpsProviderEnabled(context);
    }

    public static boolean requestLocationServiceEnabled(Activity activity) {
        if (!isLocationServiceEnabled(activity)) {
            showAlertNoLocationService(activity);
            return false;
        }
        if (!isHaveRequiredPermission(activity)) {
            requestPermission(activity);
            return false;
        }
        return true;
    }

    public static void showAlertNoLocationService(final Context context) {
        DialogUtils.showConfirmDialog(context, R.string.notify, R.string.customer_register_confirm_turn_on_location, R.string.confirm_ok, R.string.confirm_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            }
        });
    }

    public static void requestPermission(final Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);
    }

    public static boolean isBetterLocation(Location location,
                                           Location currentBestLocation) {

        final int TWO_MINUTES = 1000 * 60 * 2;

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using ctx combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @SuppressWarnings("ResourceType")
    public static void addLocationUpdatesListener(Context context,
                                                  String locationProvider, LocationListener listener) {
        if (!isHaveRequiredPermission(context)) {
            return;
        }
        LocationManager locationManager = getLocationService(context);
        if (locationProvider == null) {
            if (isGpsProviderEnabled(context)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
            }
            if (isNetworkProviderEnabled(context)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, listener);
            }
        } else {
            locationManager.requestLocationUpdates(locationProvider, 1000, 10, listener);
        }
    }

    @SuppressWarnings("ResourceType")
    public static void removeLocationUpdatesListener(Context context,
                                                     LocationListener listener) {
        if (!isHaveRequiredPermission(context)) {
            return;
        }
        LocationManager locationManager = getLocationService(context);
        locationManager.removeUpdates(listener);
    }

    public static boolean isLocationExpired(Location location) {
        if (location != null
                && location.getTime() > Calendar.getInstance()
                .getTimeInMillis() - 2 * 60 * 1000) {
            return false;
        }
        return true;
    }

    public static LocationDistanceMatcher matcherWithLocation(Context context, float maximumDistance,
                                                              long timeout, double lattitude, double longitude, LocationDistanceMatcherListener listener) {
        return new LocationDistanceMatcher(context, maximumDistance, timeout, lattitude, longitude, listener);
    }

    public static boolean isHaveRequiredPermission(Context context) {
        return !(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }
}
