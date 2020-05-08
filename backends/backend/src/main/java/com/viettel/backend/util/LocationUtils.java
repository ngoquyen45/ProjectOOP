package com.viettel.backend.util;

import com.viettel.backend.util.entity.Location;

/**
 * Ho tro tinh toan khoang cach giua hai toa do
 * 
 * @author TrungKH
 */
public class LocationUtils {

    /**
     * Tinh khoang cach giua hai toa do (km)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
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
        return calculateDistance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
    }

    /**
     * This function converts decimal degrees to radians
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * This function converts radians to decimal degrees
     */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static boolean checkLocationValid(Location location) {
        // latitude is above -90 and below 90 and longitude is above -180 and
        // below 180
        if (location == null || !checkLocationValid(location.getLongitude(), location.getLatitude())) {
            return false;
        }

        return true;
    }

    public static boolean checkLocationValid(double[] location) {
        if (location == null || location.length != 2 || !checkLocationValid(location[0], location[1])) {
            return false;
        }

        return true;
    }

    public static boolean checkLocationValid(double longitude, double latitude) {
        if (checkLongitude(longitude) && checkLatitude(latitude)) {
            return true;
        }

        return false;
    }
    
    public static boolean checkLongitude(double longitude) {
        // longitude is above -180 and below 180
        if (longitude <= -180 || longitude >= 180) {
            return false;
        }

        return true;
    }
    
    public static boolean checkLatitude(double latitude) {
        // latitude is above -90 and below 90
        if (latitude <= -90 || latitude >= 90) {
            return false;
        }

        return true;
    }

    public static double[] convert(Location dto) {
        if (checkLocationValid(dto)) {
            return new double[] { dto.getLongitude(), dto.getLatitude() };
        }
        return null;
    }

}