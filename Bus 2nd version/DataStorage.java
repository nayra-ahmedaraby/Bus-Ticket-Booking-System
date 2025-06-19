import java.awt.*;
import java.io.*;
import javax.swing.*;

// Interface for booking operations
interface Bookable {
    String bookSeat(String username, int scheduleId, int seatNumber, Schedule[] schedules, DataStorage.Booking[] bookings);
    String cancelBooking(String username, int bookingId, Schedule[] schedules, DataStorage.Booking[] bookings);
}

// Class for managing booking data
public class DataStorage {
    // Enum for Booking Status
    enum BookingStatus {
        CONFIRMED,
        CANCELLED
    }

    // Inner class for Booking
    public static class Booking implements Displayable {
        private int id;
        private String username;
        private int scheduleId;
        private int seatNumber;
        private BookingStatus status;

        public Booking(int id, String username, int scheduleId, int seatNumber, BookingStatus status) {
            this.id = id;
            this.username = username;
            this.scheduleId = scheduleId;
            this.seatNumber = seatNumber;
            this.status = status;
        }

        // Getters
        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public int getScheduleId() {
            return scheduleId;
        }

        public int getSeatNumber() {
            return seatNumber;
        }

        public BookingStatus getStatus() {
            return status;
        }

        // Setters
        public void setSeatNumber(int seatNumber) {
            this.seatNumber = seatNumber;
        }

        public void setStatus(BookingStatus status) {
            this.status = status;
        }

        @Override
        public String displayData() {
            return "Booking ID: " + id + ", User: " + username + ", Schedule ID: " + scheduleId +
                   ", Seat: " + seatNumber + ", Status: " + status;
        }
    }

    private static int bookingCount = 0;

    // Parse a line to create a Booking object
    public static Booking parseBooking(String line) {
        String[] parts = line.split(",");
        if (parts.length == 5) {
            try {
                BookingStatus status = BookingStatus.valueOf(parts[4]);
                return new Booking(Integer.parseInt(parts[0]), parts[1],
                                  Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), status);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Invalid booking data: " + line);
            }
        }
        return null;
    }

    // Load bookings from file
    public static void loadBookings(Booking[] bookings) {
        try {
            File file = new File("bookings.txt");
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            bookingCount = 0;
            while ((line = reader.readLine()) != null && bookingCount < bookings.length) {
                Booking booking = parseBooking(line);
                if (booking != null) {
                    bookings[bookingCount] = booking;
                    bookingCount++;
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading bookings: " + e.getMessage());
        }
    }

    // Save bookings to file
    public static void saveBookings(Booking[] bookings) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("bookings.txt"));
            for (int i = 0; i < bookingCount; i++) {
                writer.write(bookings[i].getId() + "," + bookings[i].getUsername() + "," +
                            bookings[i].getScheduleId() + "," + bookings[i].getSeatNumber() + "," +
                            bookings[i].getStatus().name());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving bookings: " + e.getMessage());
        }
    }

        public String bookSeat(String username, int scheduleId, int seatNumber, Schedule[] schedules, Booking[] bookings) {
        Schedule schedule = Schedule.findSchedule(scheduleId, schedules);
        if (schedule == null) {
            return "Invalid schedule ID!";
        }

        if (schedule.getSeats() <= 0) {
            return "No seats available!";
        }

        if (seatNumber < 1 || seatNumber > schedule.getSeats()) {
            return "Seat number must be between 1 and " + schedule.getSeats() + "!";
        }

        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getScheduleId() == scheduleId && bookings[i].getSeatNumber() == seatNumber &&
                bookings[i].getStatus() == BookingStatus.CONFIRMED) {
                return "Seat already booked!";
            }
        }

        if (bookingCount < bookings.length) {
            bookings[bookingCount] = new Booking(bookingCount + 1, username, scheduleId, seatNumber, BookingStatus.CONFIRMED);
            bookingCount++;
            Schedule.updateSeats(scheduleId, -1, schedules);
            saveBookings(bookings);

            // Process payment simulation
            boolean paymentSuccess = processPayment(username, scheduleId, seatNumber, schedule.getPrice());
            if (!paymentSuccess) {
                // Rollback the booking if payment fails
                bookingCount--;
                Schedule.updateSeats(scheduleId, 1, schedules);
                saveBookings(bookings);
                return "Payment failed! Booking cancelled.";
            }

            return "Seat booked successfully!";
        }
        return "Booking limit reached!";
    }

        public String cancelBooking(String username, int bookingId, Schedule[] schedules, Booking[] bookings) {
        int bookingIndex = -1;
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getId() == bookingId && bookings[i].getUsername().equals(username)) {
                bookingIndex = i;
                break;
            }
        }

        if (bookingIndex == -1) {
            return "Invalid booking ID or not your booking!";
        }

        int scheduleId = bookings[bookingIndex].getScheduleId();
        bookings[bookingIndex].setStatus(BookingStatus.CANCELLED);
        for (int i = bookingIndex; i < bookingCount - 1; i++) {
            bookings[i] = bookings[i + 1];
        }
        bookingCount--;
        Schedule.updateSeats(scheduleId, 1, schedules);
        saveBookings(bookings);
        return "Booking cancelled successfully!";
    }

    public static String modifyBooking(String username, int bookingId, int newSeatNumber, Schedule[] schedules, Booking[] bookings) {
        int bookingIndex = -1;
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getId() == bookingId && bookings[i].getUsername().equals(username)) {
                bookingIndex = i;
                break;
            }
        }

        if (bookingIndex == -1) {
            return "Invalid booking ID or not your booking!";
        }

        int scheduleId = bookings[bookingIndex].getScheduleId();
        Schedule schedule = Schedule.findSchedule(scheduleId, schedules);
        if (schedule == null) {
            return "Schedule not found!";
        }

        if (newSeatNumber < 1 || newSeatNumber > schedule.getSeats()) {
            return "New seat number must be between 1 and " + schedule.getSeats() + "!";
        }

        for (int i = 0; i < bookingCount; i++) {
            if (i != bookingIndex && bookings[i].getScheduleId() == scheduleId && 
                bookings[i].getSeatNumber() == newSeatNumber && bookings[i].getStatus() == BookingStatus.CONFIRMED) {
                return "New seat already booked!";
            }
        }

        bookings[bookingIndex].setSeatNumber(newSeatNumber);
        saveBookings(bookings);
        return "Booking modified successfully!";
    }

    public static String viewUserBookings(String username, Booking[] bookings, Schedule[] schedules) {
        String result = "";
        boolean found = false;
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getUsername().equals(username) && bookings[i].getStatus() == BookingStatus.CONFIRMED) {
                Schedule schedule = Schedule.findSchedule(bookings[i].getScheduleId(), schedules);
                String route = (schedule != null) ? schedule.getRoute() : "Unknown";
                double price = (schedule != null) ? schedule.getPrice() : 0.0;
                result += bookings[i].displayData() + ", Route: " + route + ", Price: $" + price + "\n";
                found = true;
            }
        }
        return found ? result : "No bookings found!";
    }

        public static String viewAllBookings(Booking[] bookings, Schedule[] schedules) {
        if (bookingCount == 0) {
            return "No bookings available!";
        }

        String result = "";
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getStatus() == BookingStatus.CONFIRMED) {
                Schedule schedule = Schedule.findSchedule(bookings[i].getScheduleId(), schedules);
                String route = (schedule != null) ? schedule.getRoute() : "Unknown";
                double price = (schedule != null) ? schedule.getPrice() : 0.0;
                result += bookings[i].displayData() + ", Route: " + route + ", Price: $" + price + "\n";
            }
        }
        return result.isEmpty() ? "No active bookings!" : result;
    }

    public static int getBookingCount() {
        return bookingCount;
    }

    // Simulated payment processing with Vodafone Cash or InstaPay
    public boolean processPayment(String username, int scheduleId, int seatNumber, double price) {
    JDialog paymentDialog = new JDialog((JFrame) null, "Payment", true); // Modal Dialog
    paymentDialog.setSize(300, 200);
    paymentDialog.setMinimumSize(new Dimension(250, 150));
    paymentDialog.setLayout(new GridBagLayout());
    paymentDialog.getContentPane().setBackground(new Color(230, 240, 255));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel methodLabel = new JLabel("Payment Method:");
    String[] methods = {"Vodafone Cash", "InstaPay"};
    JComboBox<String> methodCombo = new JComboBox<>(methods);
    JLabel phoneLabel = new JLabel("Phone Number:");
    JTextField phoneField = new JTextField(15);
    JButton payButton = new JButton("Pay");
    JButton cancelButton = new JButton("Cancel");

    gbc.gridx = 0;
    gbc.gridy = 0;
    paymentDialog.add(methodLabel, gbc);
    gbc.gridx = 1;
    paymentDialog.add(methodCombo, gbc);
    gbc.gridx = 0;
    gbc.gridy = 1;
    paymentDialog.add(phoneLabel, gbc);
    gbc.gridx = 1;
    paymentDialog.add(phoneField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 2;
    paymentDialog.add(payButton, gbc);
    gbc.gridx = 1;
    paymentDialog.add(cancelButton, gbc);

    final boolean[] paymentSuccess = {false};
    final String companyAccount = "01012345678";

    payButton.addActionListener(e -> {
        String selectedMethod = (String) methodCombo.getSelectedItem();
        String phoneNumber = phoneField.getText().trim();

        if (!phoneNumber.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(paymentDialog, "Phone number must be 11 digits!");
            return;
        }

        if (selectedMethod.equals("Vodafone Cash")) {
            if (!phoneNumber.startsWith("010")) {
                JOptionPane.showMessageDialog(paymentDialog, "Vodafone Cash number must start with 010!");
                return;
            }
        }

        JOptionPane.showMessageDialog(paymentDialog, 
            "Payment request of " + price + " EGP sent via " + selectedMethod + " to company account: " + companyAccount);
        JOptionPane.showMessageDialog(paymentDialog, "Payment successful!");
        paymentSuccess[0] = true;
        paymentDialog.dispose();
    });

    cancelButton.addActionListener(e -> {
        paymentDialog.dispose();
    });

    paymentDialog.setVisible(true); 
    return paymentSuccess[0];
}
}