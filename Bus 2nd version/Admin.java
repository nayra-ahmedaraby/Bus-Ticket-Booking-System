import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Admin class, inherits from User
public class Admin extends User implements Displayable {
    public Admin(String username, String password) {
        super(username, password, UserType.ADMIN);
    }

    @Override
    public void showMenu(User[] users, Schedule[] schedules, DataStorage.Booking[] bookings) {
        JFrame frame = new JFrame("Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setMinimumSize(new Dimension(350, 250));
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(230, 240, 255));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add Schedule");
        JButton deleteButton = new JButton("Delete Schedule");
        JButton editButton = new JButton("Edit Schedule");
        JButton viewSchedulesButton = new JButton("View Schedules");
        JButton viewBookingsButton = new JButton("View All Bookings");
        JButton logoutButton = new JButton("Logout");

        Dimension buttonSize = new Dimension(200, 30);
        addButton.setMaximumSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        editButton.setMaximumSize(buttonSize);
        viewSchedulesButton.setMaximumSize(buttonSize);
        viewBookingsButton.setMaximumSize(buttonSize);
        logoutButton.setMaximumSize(buttonSize);

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewSchedulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewBookingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(viewSchedulesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());

        frame.add(buttonPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            JFrame addFrame = new JFrame("Add Schedule");
            addFrame.setSize(300, 200);
            addFrame.setMinimumSize(new Dimension(250, 150));
            addFrame.setLayout(new GridBagLayout());
            addFrame.getContentPane().setBackground(new Color(230, 240, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel routeLabel = new JLabel("Route:");
            JTextField routeField = new JTextField(10);
            JLabel seatsLabel = new JLabel("Seats:");
            JTextField seatsField = new JTextField(10);
            JLabel priceLabel = new JLabel("Price:");
            JTextField priceField = new JTextField(10);
            JButton submitButton = new JButton("Add");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            addFrame.add(routeLabel, gbc);
            gbc.gridx = 1;
            addFrame.add(routeField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            addFrame.add(seatsLabel, gbc);
            gbc.gridx = 1;
            addFrame.add(seatsField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            addFrame.add(priceLabel, gbc);
            gbc.gridx = 1;
            addFrame.add(priceField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            addFrame.add(submitButton, gbc);
            gbc.gridx = 1;
            addFrame.add(backButton, gbc);

            submitButton.addActionListener(e1 -> {
                String route = routeField.getText();
                int seats;
                double price;
                try {
                    seats = Integer.parseInt(seatsField.getText());
                    price = Double.parseDouble(priceField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addFrame, "Invalid seats or price!");
                    return;
                }

                if (Schedule.scheduleCount < schedules.length) {
                    schedules[Schedule.scheduleCount] = new Schedule(Schedule.scheduleCount + 1, route, seats, price);
                    Schedule.scheduleCount++;
                    Schedule.saveSchedules(schedules);
                    JOptionPane.showMessageDialog(addFrame, "Schedule added successfully!");
                    addFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(addFrame, "Schedule limit reached!");
                }
            });

            backButton.addActionListener(e1 -> addFrame.dispose());

            addFrame.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            JFrame deleteFrame = new JFrame("Delete Schedule");
            deleteFrame.setSize(300, 150);
            deleteFrame.setMinimumSize(new Dimension(250, 120));
            deleteFrame.setLayout(new GridBagLayout());
            deleteFrame.getContentPane().setBackground(new Color(230, 240, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel idLabel = new JLabel("Schedule ID:");
            JTextField idField = new JTextField(10);
            JButton submitButton = new JButton("Delete");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            deleteFrame.add(idLabel, gbc);
            gbc.gridx = 1;
            deleteFrame.add(idField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            deleteFrame.add(submitButton, gbc);
            gbc.gridx = 1;
            deleteFrame.add(backButton, gbc);

            submitButton.addActionListener(e1 -> {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(deleteFrame, "Invalid schedule ID!");
                    return;
                }

                int index = -1;
                for (int i = 0; i < Schedule.scheduleCount; i++) {
                    if (schedules[i].getId() == id) {
                        index = i;
                        break;
                    }
                }

                if (index == -1) {
                    JOptionPane.showMessageDialog(deleteFrame, "Schedule not found!");
                    return;
                }

                for (int i = index; i < Schedule.scheduleCount - 1; i++) {
                    schedules[i] = schedules[i + 1];
                }
                Schedule.scheduleCount--;
                Schedule.saveSchedules(schedules);
                JOptionPane.showMessageDialog(deleteFrame, "Schedule deleted successfully!");
                deleteFrame.dispose();
            });

            backButton.addActionListener(e1 -> deleteFrame.dispose());

            deleteFrame.setVisible(true);
        });

        editButton.addActionListener(e -> {
            JFrame editFrame = new JFrame("Edit Schedule");
            editFrame.setSize(300, 250);
            editFrame.setMinimumSize(new Dimension(250, 200));
            editFrame.setLayout(new GridBagLayout());
            editFrame.getContentPane().setBackground(new Color(230, 240, 255));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel idLabel = new JLabel("Schedule ID:");
            JTextField idField = new JTextField(10);
            JLabel routeLabel = new JLabel("New Route:");
            JTextField routeField = new JTextField(10);
            JLabel seatsLabel = new JLabel("New Seats:");
            JTextField seatsField = new JTextField(10);
            JLabel priceLabel = new JLabel("New Price:");
            JTextField priceField = new JTextField(10);
            JButton submitButton = new JButton("Edit");
            JButton backButton = new JButton("Back");

            gbc.gridx = 0;
            gbc.gridy = 0;
            editFrame.add(idLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(idField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            editFrame.add(routeLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(routeField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            editFrame.add(seatsLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(seatsField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            editFrame.add(priceLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(priceField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 4;
            editFrame.add(submitButton, gbc);
            gbc.gridx = 1;
            editFrame.add(backButton, gbc);

            submitButton.addActionListener(e1 -> {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editFrame, "Invalid schedule ID!");
                    return;
                }

                Schedule schedule = Schedule.findSchedule(id, schedules);
                if (schedule == null) {
                    JOptionPane.showMessageDialog(editFrame, "Schedule not found!");
                    return;
                }

                String newRoute = routeField.getText();
                if (!newRoute.isEmpty()) {
                    schedule.setRoute(newRoute);
                }

                String seatsText = seatsField.getText();
                if (!seatsText.isEmpty()) {
                    try {
                        int newSeats = Integer.parseInt(seatsText);
                        schedule.setSeats(newSeats);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(editFrame, "Invalid seats!");
                        return;
                    }
                }

                String priceText = priceField.getText();
                if (!priceText.isEmpty()) {
                    try {
                        double newPrice = Double.parseDouble(priceText);
                        schedule.setPrice(newPrice);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(editFrame, "Invalid price!");
                        return;
                    }
                }

                Schedule.saveSchedules(schedules);
                JOptionPane.showMessageDialog(editFrame, "Schedule updated successfully!");
                editFrame.dispose();
            });

            backButton.addActionListener(e1 -> editFrame.dispose());

            editFrame.setVisible(true);
        });

        viewSchedulesButton.addActionListener(e -> {
            JFrame tableFrame = new JFrame("Schedules");
            tableFrame.setSize(500, 300);
            tableFrame.setMinimumSize(new Dimension(400, 200));
            tableFrame.setLayout(new BorderLayout());

            String[] columns = {"ID", "Route", "Seats", "Price"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < Schedule.scheduleCount; i++) {
                model.addRow(new Object[]{schedules[i].getId(), schedules[i].getRoute(),
                                         schedules[i].getSeats(), schedules[i].getPrice()});
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

        viewBookingsButton.addActionListener(e -> {
            JFrame tableFrame = new JFrame("All Bookings");
            tableFrame.setSize(600, 300);
            tableFrame.setMinimumSize(new Dimension(500, 200));
            tableFrame.setLayout(new BorderLayout());

            String[] columns = {"Booking ID", "Username", "Schedule ID", "Seat", "Route", "Price", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            for (int i = 0; i < DataStorage.getBookingCount(); i++) {
                if (bookings[i].getStatus() == DataStorage.BookingStatus.CONFIRMED) {
                    Schedule schedule = Schedule.findSchedule(bookings[i].getScheduleId(), schedules);
                    String route = (schedule != null) ? schedule.getRoute() : "Unknown";
                    double price = (schedule != null) ? schedule.getPrice() : 0.0;
                    model.addRow(new Object[]{bookings[i].getId(), bookings[i].getUsername(),
                                             bookings[i].getScheduleId(), bookings[i].getSeatNumber(),
                                             route, price, bookings[i].getStatus()});
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

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Main.main(null);
        });

        frame.setVisible(true);
    }

    @Override
    public String displayData() {
        return "Admin: " + getUsername();
    }
}