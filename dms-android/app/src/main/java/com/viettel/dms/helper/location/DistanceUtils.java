
package com.viettel.dms.helper.location;

import android.location.Location;

/**
 * Ho tro tinh toan khoang cach giua hai toa do
 * 
 * @author TrungKH
 */
public class DistanceUtils {

    /**
     * Tinh khoang cach giua hai toa do
     */
    public static double calculateDistance(double lat1, double lon1,
            double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    /**
     * Tinh khoang cach giua hai toa do
     */
    public static double calculateDistance(Location loc1, Location loc2) {
        return calculateDistance(loc1.getLatitude(), loc1.getLongitude(),
                loc2.getLatitude(), loc2.getLongitude());
    }

    public static double calculateDistance(Location loc1, double lat2, double lon2) {
        return calculateDistance(loc1.getLatitude(), loc1.getLongitude(), lat2, lon2);
    }

    /**
     * This function converts decimal degrees to radians
     */
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * This function converts radians to decimal degrees
     */
    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}