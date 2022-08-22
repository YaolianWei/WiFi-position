package com.capstone.wifiposition.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// Positioning range
public class Places extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Date date = new Date();
    private String name;
    private String detail;
    private RealmList<AccessPoint> aps;
    private RealmList<ReferencePoint> rps;

    public Places(){
    }

    public Places(Date date, String name, String detail){
        this.date = date;
        this.name = name;
        this.detail = detail;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public RealmList<AccessPoint> getAps() {
        return aps;
    }

    public void setAps(RealmList<AccessPoint> aps) {
        this.aps = aps;
    }

    public RealmList<ReferencePoint> getRps() {
        return rps;
    }

    public void setRps(RealmList<ReferencePoint> rps) {
        this.rps = rps;
    }

}
