package model;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    private int bookingId;
    private int seatNumber;
    private Date bookingDate;
    private String status;
    private Passenger passenger;
    private Schedule schedule;

    public Booking(int id, int seat, Passenger p, Schedule s) {
        this.bookingId = id;
        this.seatNumber = seat;
        this.passenger = p;
        this.schedule = s;
        this.bookingDate = new Date();
        this.status = "Confirmed";
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getTicket() {
        return "Ticket #" + bookingId 
             + "\nSeat: " + seatNumber 
             + "\nPassenger: " + passenger.getName() 
             + "\nFrom: " + schedule.getRoute().getStartLocation()
             + "\nTo: " + schedule.getRoute().getEndLocation()
             + "\nDeparture: " + schedule.getDepartureTime();
    }
}