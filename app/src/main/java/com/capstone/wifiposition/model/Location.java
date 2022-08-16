package com.capstone.wifiposition.model;

import java.util.ArrayList;

public class Location {

    private String location;
    private ArrayList<Distance> distances;

    public Location(String location, ArrayList<Distance> distances){
        this.location = location;
        this.distances = distances;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Distance> getDistances() {
        return distances;
    }

    public void setDistances(ArrayList<Distance> distances) {
        this.distances = distances;
    }
}
