package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Passenger extends User {
    private String passportNumber;
    private List<Booking> bookings = new ArrayList<>();
    private static int nextId = 1;

    public Passenger(String email, String password) {
        super(email, password);
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);  // Return a copy to maintain encapsulation
    }

    public Booking bookTicket(Schedule schedule, int seat) {
        if (schedule == null || !schedule.isSeatAvailable(seat)) return null;
        
        schedule.bookSeat(seat);
        Booking newBooking = new Booking(nextId++, seat, this, schedule);
        bookings.add(newBooking);
        return newBooking;
    }

    public boolean cancelBooking(int bookingId) {
        Iterator<Booking> iterator = bookings.iterator();
        while(iterator.hasNext()) {
            Booking b = iterator.next();
            if(b.getBookingId() == bookingId) {
                b.getSchedule().releaseSeat(b.getSeatNumber());
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}