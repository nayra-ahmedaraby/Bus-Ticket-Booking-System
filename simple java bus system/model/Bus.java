package model;

import java.io.Serializable;

public class Bus implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int busId;
    private final String model;
    private final int capacity;
    private final String plateNumber;

    public Bus(int id, String model, int capacity, String plateNumber) {
        this.busId = id;
        this.model = model;
        this.capacity = capacity;
        this.plateNumber = plateNumber;
    }

    public int getBusId() {
        return busId;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    @Override
    public String toString() {
        return String.format("Bus[ID=%d, Model=%s, Capacity=%d, Plate=%s]",
            busId, model, capacity, plateNumber);
    }
}