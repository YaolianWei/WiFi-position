package com.capstone.wifiposition.model;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Wifi scanned list data
public class WifiData implements Parcelable {

    private List<WifiInfo> wifiList;

    public WifiData(){
        wifiList = new ArrayList<>();
    }

    protected WifiData(Parcel in) {
//        wifiList = in.createTypedArrayList(WifiInfo.CREATOR);
        in.readTypedList(wifiList, WifiInfo.CREATOR);
    }

    public static final Creator<WifiData> CREATOR = new Creator<WifiData>() {
        @Override
        public WifiData createFromParcel(Parcel in) {
            return new WifiData(in);
        }

        @Override
        public WifiData[] newArray(int size) {
            return new WifiData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(wifiList);
    }

    /**
     * Store the last WiFi scan performed by manager
     * & Create an object for each network detected.
     *
     * @param results list of networks detected
     */
    public void addNetworks(List<ScanResult> results) {
        wifiList.clear();
        for (ScanResult result : results) {
            wifiList.add(new WifiInfo(result));
        }
        Collections.sort(wifiList);
    }

    /**
     * @return Returns the list of scanned Wifi
     */
    public List<WifiInfo> getNetworks() {
        return wifiList;
    }

    /**
     * @return Return a string containing a concise, human-readable description of this object.
     */
    @Override
    public String toString() {
        if (wifiList == null || wifiList.size() == 0)
            return "Empty data.";
        else
            return wifiList.size() + " networks data.";
    }

}
