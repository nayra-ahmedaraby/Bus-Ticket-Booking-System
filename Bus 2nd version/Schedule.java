import java.io.*;
import javax.swing.*;

// Class for managing schedules
public class Schedule implements Displayable {
    private int id;
    private String route;
    private int seats;
    private double price;
    public static int scheduleCount = 0;

    public Schedule(int id, String route, int seats, double price) {
        this.id = id;
        this.route = route;
        this.seats = seats;
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getRoute() {
        return route;
    }

    public int getSeats() {
        return seats;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setRoute(String route) {
        this.route = route;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String displayData() {
        return "ID: " + id + ", Route: " + route + ", Seats: " + seats + ", Price: $" + price;
    }

    // Parse a line to create a Schedule object
    public static Schedule parseSchedule(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            try {
                return new Schedule(Integer.parseInt(parts[0]), parts[1],
                                   Integer.parseInt(parts[2]), Double.parseDouble(parts[3]));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid schedule data: " + line);
            }
        }
        return null;
    }

    // Load schedules from file
    public static void loadSchedules(Schedule[] schedules) {
        try {
            File file = new File("schedules.txt");
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            scheduleCount = 0;
            while ((line = reader.readLine()) != null && scheduleCount < schedules.length) {
                Schedule schedule = parseSchedule(line);
                if (schedule != null) {
                    schedules[scheduleCount] = schedule;
                    scheduleCount++;
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading schedules: " + e.getMessage());
        }
    }

    // Save schedules to file
    public static void saveSchedules(Schedule[] schedules) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("schedules.txt"));
            for (int i = 0; i < scheduleCount; i++) {
                writer.write(schedules[i].getId() + "," + schedules[i].getRoute() + "," +
                            schedules[i].getSeats() + "," + schedules[i].getPrice());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving schedules: " + e.getMessage());
        }
    }

    // Find a schedule by ID
    public static Schedule findSchedule(int id, Schedule[] schedules) {
        for (int i = 0; i < scheduleCount; i++) {
            if (schedules[i].getId() == id) {
                return schedules[i];
            }
        }
        return null;
    }

    // Update seats for a schedule
    public static void updateSeats(int scheduleId, int change, Schedule[] schedules) {
        Schedule schedule = findSchedule(scheduleId, schedules);
        if (schedule != null) {
            schedule.setSeats(schedule.getSeats() + change);
            saveSchedules(schedules);
        }
    }
}