package com.capstone.wifiposition.model;

import androidx.annotation.NonNull;

// Location distance
public class Distance implements Comparable<Distance>{

    private double distance;
    private String location;
    private String name;

    public Distance(double distance, String location, String name){
        this.distance = distance;
        this.location = location;
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull Distance dist) {
        if (distance == dist.getDistance())
            return 0;
        else if (distance > dist.getDistance())
            return 1;
        else
            return -1;
    }

}
