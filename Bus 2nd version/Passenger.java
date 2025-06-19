import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Passenger class, inherits from User
public class Passenger extends User implements Displayable {
    public Passenger(String username, String password) {
        super(username, password, UserType.PASSENGER);
    }

    @Override
    public void showMenu(User[] users, Schedule[] schedules, DataStorage.Booking[] bookings) {
        JFrame frame = new JFrame("Passenger Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setMinimumSize(new Dimension(350, 250));
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(230, 240, 255));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton bookButton = new JButton("Book Seat");
        JButton cancelButton = new JButton("Cancel Booking");
        JButton modifyButton = new JButton("Modify Booking");
        JButton viewButton = new JButton("View My Bookings");
        JButton searchButton = new JButton("Search Schedules");
        JButton logoutButton = new JButton("Logout");

        Dimension buttonSize = new Dimension(200, 30);
        bookButton.setMaximumSize(buttonSize);
        cancelButton.setMaximumSize(buttonSize);
        modifyButton.setMaximumSize(buttonSize);
        viewButton.setMaximumSize(buttonSize);
        searchButton.setMaximumSize(buttonSize);
        logoutButton.setMaximumSize(buttonSize);

        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        modifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(bookButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(modifyButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(viewButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());

        frame.add(buttonPanel, BorderLayout.CENTER);

        bookButton.addActionListener(e -> {
            JFrame bookFrame = new JFrame("Book Seat");
            bookFrame.setSize(600, 400);
            bookFrame.setMinimumSize(new Dimension(500, 300));
            bookFrame.setLayout(new BorderLayout(10, 10));
            bookFrame.getContentPane().setBackground(new Color(230, 240, 255));

            // Display available schedules in a table
            String[] columns = {"ID", "Route", "Seats", "Price"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < Schedule.scheduleCount; i++) {
                if (schedules[i].getSeats() > 0) { // Show only schedules with available seats
                    model.addRow(new Object[]{schedules[i].getId(), schedules[i].getRoute(),
                                             schedules[i].getSeats(), schedules[i].getPrice()});
                }
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            bookFrame.add(scrollPane, BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(new Color(230, 240, 255));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel seatLabel = new JLabel("Seat Number:");
            JTextField seatField = new JTextField(10);
            JButton submitButton = new JButton("Book");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            inputPanel.add(seatLabel, gbc);
            gbc.gridx = 1;
            inputPanel.add(seatField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            inputPanel.add(submitButton, gbc);
            gbc.gridx = 1;
            inputPanel.add(backButton, gbc);

            bookFrame.add(inputPanel, BorderLayout.SOUTH);

            submitButton.addActionListener(e1 -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(bookFrame, "Please select a schedule from the table!");
                    return;
                }

                int scheduleId = (int) model.getValueAt(selectedRow, 0);
                int seatNumber;
                try {
                    seatNumber = Integer.parseInt(seatField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(bookFrame, "Invalid seat number!");
                    return;
                }

                DataStorage dataStorage = new DataStorage();
                String result = dataStorage.bookSeat(getUsername(), scheduleId, seatNumber, schedules, bookings);
                JOptionPane.showMessageDialog(bookFrame, result);
                bookFrame.dispose();
                showMenu(users, schedules, bookings); // Refresh the menu
            });

            backButton.addActionListener(e1 -> bookFrame.dispose());

            bookFrame.setVisible(true);
        });

        cancelButton.addActionListener(e -> {
            JFrame cancelFrame = new JFrame("Cancel Booking");
            cancelFrame.setSize(300, 150);
            cancelFrame.setMinimumSize(new Dimension(250, 120));
            cancelFrame.setLayout(new GridBagLayout());
            cancelFrame.getContentPane().setBackground(new Color(230, 240, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel bookingLabel = new JLabel("Booking ID:");
            JTextField bookingField = new JTextField(10);
            JButton submitButton = new JButton("Cancel");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            cancelFrame.add(bookingLabel, gbc);
            gbc.gridx = 1;
            cancelFrame.add(bookingField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            cancelFrame.add(submitButton, gbc);
            gbc.gridx = 1;
            cancelFrame.add(backButton, gbc);

            submitButton.addActionListener(e1 -> {
                int bookingId;
                try {
                    bookingId = Integer.parseInt(bookingField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(cancelFrame, "Invalid booking ID!");
                    return;
                }
                DataStorage dataStorage = new DataStorage();
                String result = dataStorage.cancelBooking(getUsername(), bookingId, schedules, bookings);
                JOptionPane.showMessageDialog(cancelFrame, result);
                cancelFrame.dispose();
            });

            backButton.addActionListener(e1 -> cancelFrame.dispose());

            cancelFrame.setVisible(true);
        });

        modifyButton.addActionListener(e -> {
            JFrame modifyFrame = new JFrame("Modify Booking");
            modifyFrame.setSize(300, 200);
            modifyFrame.setMinimumSize(new Dimension(250, 150));
            modifyFrame.setLayout(new GridBagLayout());
            modifyFrame.getContentPane().setBackground(new Color(230, 240, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel bookingLabel = new JLabel("Booking ID:");
            JTextField bookingField = new JTextField(10);
            JLabel seatLabel = new JLabel("New Seat Number:");
            JTextField seatField = new JTextField(10);
            JButton submitButton = new JButton("Modify");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            modifyFrame.add(bookingLabel, gbc);
            gbc.gridx = 1;
            modifyFrame.add(bookingField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            modifyFrame.add(seatLabel, gbc);
            gbc.gridx = 1;
            modifyFrame.add(seatField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            modifyFrame.add(submitButton, gbc);
            gbc.gridx = 1;
            modifyFrame.add(backButton, gbc);

            submitButton.addActionListener(e1 -> {
                int bookingId;
                int newSeatNumber;
                try {
                    bookingId = Integer.parseInt(bookingField.getText());
                    newSeatNumber = Integer.parseInt(seatField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(modifyFrame, "Invalid booking ID or seat number!");
                    return;
                }
                String result = DataStorage.modifyBooking(getUsername(), bookingId, newSeatNumber, schedules, bookings);
                JOptionPane.showMessageDialog(modifyFrame, result);
                modifyFrame.dispose();
            });

            backButton.addActionListener(e1 -> modifyFrame.dispose());

            modifyFrame.setVisible(true);
        });

        viewButton.addActionListener(e -> {
            JFrame tableFrame = new JFrame("My Bookings");
            tableFrame.setSize(600, 300);
            tableFrame.setMinimumSize(new Dimension(500, 200));
            tableFrame.setLayout(new BorderLayout());

            String[] columns = {"Booking ID", "Schedule ID", "Seat", "Route", "Price", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < DataStorage.getBookingCount(); i++) {
                if (bookings[i].getUsername().equals(getUsername()) && bookings[i].getStatus() == DataStorage.BookingStatus.CONFIRMED) {
                    Schedule schedule = Schedule.findSchedule(bookings[i].getScheduleId(), schedules);
                    String route = (schedule != null) ? schedule.getRoute() : "Unknown";
                    double price = (schedule != null) ? schedule.getPrice() : 0.0;
                    model.addRow(new Object[]{bookings[i].getId(), bookings[i].getScheduleId(),
                                             bookings[i].getSeatNumber(), route, price, bookings[i].getStatus()});
                }
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            tableFrame.add(scrollPane, BorderLayout.CENTER);

            JButton backButton = new JButton("Back");
            backButton.setPreferredSize(new Dimension(100, 30));
            backButton.addActionListener(e1 -> tableFrame.dispose());
            JPanel buttonPanelSouth = new JPanel();
            buttonPanelSouth.setBackground(new Color(230, 240, 255));
            buttonPanelSouth.add(backButton);
            tableFrame.add(buttonPanelSouth, BorderLayout.SOUTH);

            tableFrame.setVisible(true);
        });

        searchButton.addActionListener(e -> {
            JFrame searchFrame = new JFrame("Search Schedules");
            searchFrame.setSize(400, 200);
            searchFrame.setMinimumSize(new Dimension(350, 150));
            searchFrame.setLayout(new GridBagLayout());
            searchFrame.getContentPane().setBackground(new Color(230, 240, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel routeLabel = new JLabel("Route (partial match):");
            JTextField routeField = new JTextField(10);
            JButton searchButtonInner = new JButton("Search");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            searchFrame.add(routeLabel, gbc);
            gbc.gridx = 1;
            searchFrame.add(routeField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            searchFrame.add(searchButtonInner, gbc);
            gbc.gridx = 1;
            searchFrame.add(backButton, gbc);

            searchButtonInner.addActionListener(e1 -> {
                String routeQuery = routeField.getText().trim();
                JFrame resultFrame = new JFrame("Search Results");
                resultFrame.setSize(500, 300);
                resultFrame.setMinimumSize(new Dimension(400, 200));
                resultFrame.setLayout(new BorderLayout());

                String[] columns = {"ID", "Route", "Seats", "Price"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                for (int i = 0; i < Schedule.scheduleCount; i++) {
                    if (schedules[i].getRoute().toLowerCase().contains(routeQuery.toLowerCase())) {
                        model.addRow(new Object[]{schedules[i].getId(), schedules[i].getRoute(),
                                                 schedules[i].getSeats(), schedules[i].getPrice()});
                    }
                }

                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
                resultFrame.add(scrollPane, BorderLayout.CENTER);

                JButton closeButton = new JButton("Close");
                closeButton.setPreferredSize(new Dimension(100, 30));
                closeButton.addActionListener(e2 -> resultFrame.dispose());
                JPanel buttonPanelSouth = new JPanel();
                buttonPanelSouth.setBackground(new Color(230, 240, 255));
                buttonPanelSouth.add(closeButton);
                resultFrame.add(buttonPanelSouth, BorderLayout.SOUTH);

                resultFrame.setVisible(true);
            });

            backButton.addActionListener(e1 -> searchFrame.dispose());

            searchFrame.setVisible(true);
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Main.main(null);
        });

        frame.setVisible(true);
    }

    @Override
    public String displayData() {
        return "Passenger: " + getUsername();
    }
}