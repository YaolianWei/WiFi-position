package com.capstone.wifiposition.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.capstone.wifiposition.model.Distance;
import com.capstone.wifiposition.model.Location;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

// Need change !!!
public class AppConfig {

    //Reference points
    public static final int FETCH_INTERVAL = 3000;//3 secs
    public static final int READINGS_BATCH = 10;//10 values in every 3 secs

    public static final Float NaN = -110.0f;//RSSI value for no reception

    public static final String INTENT_FILTER = "ANDROID_WIFI_SCANNER";
    public static final String WIFI_DATA = "WIFI_DATA";

    public static String getDefaultAlgo(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String prefAlgo = prefs.getString("prefAlgo", "2");
        return prefAlgo;
    }


    public static boolean isLocationEnabled(Context context) {
        LocationManager locManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS enabled
            Log.d("AppConfig", "isLocationEnabled:" + true);
            return true;
        } else {
            //GPS disabled
            Log.d("AppConfig", "isLocationEnabled:" + false);
            return false;
        }
    }

    public static Distance getNearestPoint(Location loc) {
        ArrayList<Distance> distances = loc.getDistances();
        if (distances != null && distances.size() > 0) {
            Collections.sort(distances);
            return distances.get(0);
        }
        return null;
    }

    public static String reduceDecimalPlaces(String location) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        String[] split = location.split(" ");
        Double latValue = Double.valueOf(split[0]);
        Double lonValue = Double.valueOf(split[1]);
        String latFormat = formatter.format(latValue);
        String lonFormat = formatter.format(lonValue);
        return latFormat + ", " + lonFormat;
    }

    public static String getDistanceFromOrigin(String location) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        String[] split = location.split(" ");
        Double latValue = Double.valueOf(split[0]);
        Double lonValue = Double.valueOf(split[1]);
        double distance = Math.sqrt(latValue * latValue + lonValue * lonValue);
        String distanceValue = formatter.format(distance);
        return distanceValue;
    }

}
