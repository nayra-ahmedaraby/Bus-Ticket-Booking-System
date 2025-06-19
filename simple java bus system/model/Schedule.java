package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    private int scheduleId;
    private Route route;
    private String departureTime;
    private List<Integer> availableSeats;
    private Bus bus;

    public Schedule(int id, Route route, String departureTime) {
        this.scheduleId = id;
        this.route = route;
        this.departureTime = departureTime;
        this.availableSeats = new ArrayList<>();
        // Initialize with a default bus
        this.bus = new Bus(1, "Standard Bus", 40, "BUS001");
        initializeSeats();
    }

    private void initializeSeats() {
        if (availableSeats == null) {
            availableSeats = new ArrayList<>();
        }
        availableSeats.clear();
        for (int i = 1; i <= bus.getCapacity(); i++) {
            availableSeats.add(i);
        }
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public List<Integer> getAvailableSeats() {
        if (availableSeats == null) {
            initializeSeats();
        }
        return new ArrayList<>(availableSeats);
    }

    public boolean isSeatAvailable(int seatNumber) {
        if (availableSeats == null) {
            initializeSeats();
        }
        return availableSeats.contains(seatNumber);
    }

    public void bookSeat(int seatNumber) {
        if (availableSeats == null) {
            initializeSeats();
        }
        availableSeats.remove(Integer.valueOf(seatNumber));
    }

    public void releaseSeat(int seatNumber) {
        if (availableSeats == null) {
            initializeSeats();
        }
        if (!availableSeats.contains(seatNumber)) {
            availableSeats.add(seatNumber);
        }
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
        initializeSeats();
    }

    @Override
    public String toString() {
        if (availableSeats == null) {
            initializeSeats();
        }
        return String.format("Schedule[ID=%d, Route=%s, Time=%s, Available Seats=%d]",
            scheduleId, route, departureTime, availableSeats.size());
    }
}