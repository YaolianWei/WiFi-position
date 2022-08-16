package com.capstone.wifiposition.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AccessPoint extends RealmObject implements Parcelable {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Date date = new Date();
    private String bssid;//identifier

    private String description;
    private String ssid;
    private String mac_address;
    private double x;
    private double y;
    private int sequence;
    private double meanRss;//for RP (-50 to -100)
//    High quality: 90% ~= -55db
//    Medium quality: 50% ~= -75db
//    Low quality: 30% ~= -85db
//    Unusable quality: 8% ~= -96db

    public AccessPoint() {
    }

    public AccessPoint(AccessPoint accessPoint) {
        this.id = UUID.randomUUID().toString();
        this.date = Calendar.getInstance().getTime();
        this.bssid = accessPoint.bssid;
        this.description = accessPoint.description;
        this.ssid = accessPoint.ssid;
        this.mac_address = accessPoint.mac_address;
        this.x = accessPoint.x;
        this.y = accessPoint.y;
        this.sequence = accessPoint.sequence;
        this.meanRss = accessPoint.meanRss;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public double getMeanRss() {
        return meanRss;
    }

    public void setMeanRss(double meanRss) {
        this.meanRss = meanRss;
    }

    protected AccessPoint(Parcel in) {
        id = in.readString();
        bssid = in.readString();
        description = in.readString();
        ssid = in.readString();
        mac_address = in.readString();
        x = in.readDouble();
        y = in.readDouble();
        sequence = in.readInt();
        meanRss = in.readDouble();
    }

    public static final Creator<AccessPoint> CREATOR = new Creator<AccessPoint>() {
        @Override
        public AccessPoint createFromParcel(Parcel in) {
            return new AccessPoint(in);
        }

        @Override
        public AccessPoint[] newArray(int size) {
            return new AccessPoint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(bssid);
        parcel.writeString(description);
        parcel.writeString(ssid);
        parcel.writeString(mac_address);
        parcel.writeDouble(x);
        parcel.writeDouble(y);
        parcel.writeInt(sequence);
        parcel.writeDouble(meanRss);
    }

}
