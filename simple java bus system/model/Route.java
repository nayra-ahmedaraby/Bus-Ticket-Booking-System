package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    private int routeId;
    private String startLocation;
    private String endLocation;
    private float distance;
    private float price;
    private final ArrayList<String> stops;

    public Route(int routeId, String startLocation, String endLocation, float distance, float price) {
        this.routeId = routeId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distance = distance;
        this.price = price;
        this.stops = new ArrayList<>();
    }

    public int getRouteId() {
        return routeId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void addStop(String stop) {
        stops.add(stop);
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return startLocation + " to " + endLocation;
    }
}