package com.capstone.wifiposition.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReferencePoint extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Date date = new Date();
    private String name;
    private String description;
    private double x;
    private double y;
    private String locId;
    // scanAps list count is equal to the number of APs.
    private RealmList<AccessPoint> scanAps;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public RealmList<AccessPoint> getScanAps() {
        return scanAps;
    }

    public void setsSanAps(RealmList<AccessPoint> scanAps) {
        this.scanAps = scanAps;
    }
}
