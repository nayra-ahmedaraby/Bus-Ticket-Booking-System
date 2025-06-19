import java.awt.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final int MAX_USERS = 100;
        final int MAX_SCHEDULES = 100;
        final int MAX_BOOKINGS = 100;

        User[] users = new User[MAX_USERS];
        Schedule[] schedules = new Schedule[MAX_SCHEDULES];
        DataStorage.Booking[] bookings = new DataStorage.Booking[MAX_BOOKINGS];

        User.loadUsers(users);
        Schedule.loadSchedules(schedules);
        DataStorage.loadBookings(bookings);

        JFrame frame = new JFrame("Bus Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setMinimumSize(new Dimension(250, 120));
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(230, 240, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(loginButton, gbc);
        gbc.gridy = 1;
        frame.add(registerButton, gbc);
        gbc.gridy = 2;
        frame.add(exitButton, gbc);

        loginButton.addActionListener(e -> {
            frame.dispose();
            User.showLoginFrame(users, schedules, bookings);
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            User.showRegisterFrame(users);
        });

        exitButton.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }
}