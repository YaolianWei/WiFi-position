package com.capstone.wifiposition.model;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

// Each scanned Wifi information
public class WifiInfo implements Comparable<WifiInfo>, Parcelable {

    // 访问点的地址
    private String bssid;
    // 网络名称
    private String ssid;
    // 描述了身份验证、密钥管理和访问点支持的加密方案
    private String capabilities;
    // 主20 MHz的频率(MHz)的渠道客户交流访问点
    private int frequency;
    // dBm的检测信号电平,也被称为RSSI
    private int level;
    // 时间戳在微秒(因为)这个结果最后被看见
    private long timestamp;

    public WifiInfo(ScanResult scanResult){
        bssid = scanResult.BSSID;
        ssid = scanResult.SSID;
        capabilities = scanResult.capabilities;
        frequency = scanResult.frequency;
        level = scanResult.level;
        timestamp = System.currentTimeMillis();

    }

    protected WifiInfo(Parcel in) {
        bssid = in.readString();
        ssid = in.readString();
        capabilities = in.readString();
        frequency = in.readInt();
        level = in.readInt();
        timestamp = in.readLong();
    }

    public static final Creator<WifiInfo> CREATOR = new Creator<WifiInfo>() {
        @Override
        public WifiInfo createFromParcel(Parcel in) {
            return new WifiInfo(in);
        }

        @Override
        public WifiInfo[] newArray(int size) {
            return new WifiInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bssid);
        parcel.writeString(ssid);
        parcel.writeString(capabilities);
        parcel.writeInt(frequency);
        parcel.writeInt(level);
        parcel.writeLong(timestamp);
    }

    @Override
    public int compareTo(WifiInfo wifiInfo) {
//        return 0;
        return wifiInfo.level - this.level;
    }

    /**
     * Convert Wi-Fi frequency to the corresponding channel
     *
     * @param freq frequency as given by {@link ScanResult frequency}
     *
     * @return the channel associated with the given frequency
     */
    public static int channel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Return a string containing a concise, human-readable description of this object.
     */
    @Override
    public String toString() {
        return ssid + " addr:" + bssid + " lev:" + level + "dBm freq:" + frequency + "MHz cap:" + capabilities;
    }
}
