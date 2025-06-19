package View;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminDashboard extends JPanel {
    private final MainFrame frame;
    private JPanel contentPanel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Route> routes;
    private List<Schedule> schedules;
    private List<Passenger> passengers;

    public AdminDashboard(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(34, 34, 34));

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create content panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(34, 34, 34));
        add(contentPanel, BorderLayout.CENTER);

        // Load initial data
        loadData();
    }

    private void loadData() {
        executor.execute(() -> {
            // Load all data
            routes = DataStorage.loadRoutes();
            schedules = DataStorage.loadSchedules();
            List<User> users = DataStorage.loadUsers();
            passengers = users.stream()
                .filter(u -> u instanceof Passenger)
                .map(u -> (Passenger)u)
                .toList();

            // Add sample routes if none exist
            if (routes.isEmpty()) {
                addSampleRoutes();
                routes = DataStorage.loadRoutes();
            }

            // Update UI on EDT
            SwingUtilities.invokeLater(() -> {
                showRoutes();
                revalidate();
                repaint();
            });
        });
    }

    private void addSampleRoutes() {
        Route route1 = new Route(1, "New York", "Boston", 350.0f, 45.0f);
        Route route2 = new Route(2, "Boston", "Philadelphia", 300.0f, 40.0f);
        Route route3 = new Route(3, "Philadelphia", "Washington DC", 225.0f, 35.0f);
        
        DataStorage.saveRoute(route1);
        DataStorage.saveRoute(route2);
        DataStorage.saveRoute(route3);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 44, 44));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton routesBtn = createMenuButton("Routes");
        JButton schedulesBtn = createMenuButton("Schedules");
        JButton passengersBtn = createMenuButton("Passengers");

        routesBtn.addActionListener(e_ -> SwingUtilities.invokeLater(this::showRoutes));
        schedulesBtn.addActionListener(e_ -> SwingUtilities.invokeLater(this::showSchedules));
        passengersBtn.addActionListener(e_ -> SwingUtilities.invokeLater(this::showPassengers));

        sidebar.add(routesBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(schedulesBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(passengersBtn);

        return sidebar;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(new Color(212, 175, 55));
        button.setBackground(new Color(44, 44, 44));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        return button;
    }

    private void showRoutes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 34, 34));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(34, 34, 34));
        JLabel title = new JLabel("Routes");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(212, 175, 55));
        header.add(title);

        JButton addRouteBtn = new JButton("Add Route");
        addRouteBtn.setBackground(new Color(212, 175, 55));
        addRouteBtn.setForeground(Color.BLACK);
        header.add(addRouteBtn);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(34, 34, 34));

        // Refresh routes list
        routes = DataStorage.loadRoutes();
        
        if (routes.isEmpty()) {
            JLabel noRoutes = new JLabel("No routes available.");
            noRoutes.setForeground(Color.WHITE);
            content.add(noRoutes);
        } else {
            for (Route route : routes) {
                JPanel routePanel = createRoutePanel(route);
                content.add(routePanel);
                content.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBackground(new Color(34, 34, 34));
        scrollPane.getViewport().setBackground(new Color(34, 34, 34));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        addRouteBtn.addActionListener(e_ -> SwingUtilities.invokeLater(() -> showAddRouteDialog()));

        contentPanel.removeAll();
        contentPanel.add(panel, "routes");
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "routes");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSchedules() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 34, 34));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(34, 34, 34));
        JLabel title = new JLabel("Schedules");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(212, 175, 55));
        header.add(title);

        JButton addScheduleBtn = new JButton("Add Schedule");
        addScheduleBtn.setBackground(new Color(212, 175, 55));
        addScheduleBtn.setForeground(Color.BLACK);
        header.add(addScheduleBtn);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(34, 34, 34));

        // Refresh schedules list
        schedules = DataStorage.loadSchedules();
        
        if (schedules.isEmpty()) {
            JLabel noSchedules = new JLabel("No schedules available.");
            noSchedules.setForeground(Color.WHITE);
            content.add(noSchedules);
        } else {
            for (Schedule schedule : schedules) {
                JPanel schedulePanel = createSchedulePanel(schedule);
                content.add(schedulePanel);
                content.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBackground(new Color(34, 34, 34));
        scrollPane.getViewport().setBackground(new Color(34, 34, 34));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        addScheduleBtn.addActionListener(e_ -> SwingUtilities.invokeLater(() -> showAddScheduleDialog()));

        contentPanel.removeAll();
        contentPanel.add(panel, "schedules");
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "schedules");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPassengers() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 34, 34));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(34, 34, 34));
        JLabel title = new JLabel("Passengers");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(212, 175, 55));
        header.add(title);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(34, 34, 34));

        // Refresh passengers list
        List<User> users = DataStorage.loadUsers();
        passengers = users.stream()
            .filter(u -> u instanceof Passenger)
            .map(u -> (Passenger)u)
            .toList();

        if (passengers.isEmpty()) {
            JLabel noPassengers = new JLabel("No passengers registered.");
            noPassengers.setForeground(Color.WHITE);
            content.add(noPassengers);
        } else {
            for (Passenger passenger : passengers) {
                JPanel passengerPanel = createPassengerPanel(passenger);
                content.add(passengerPanel);
                content.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBackground(new Color(34, 34, 34));
        scrollPane.getViewport().setBackground(new Color(34, 34, 34));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.removeAll();
        contentPanel.add(panel, "passengers");
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "passengers");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAddRouteDialog() {
        JDialog dialog = new JDialog(frame, "Add Route", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField startField = new JTextField(20);
        JTextField endField = new JTextField(20);
        JTextField distanceField = new JTextField(20);
        JTextField priceField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        content.add(new JLabel("Start Location:"), gbc);
        gbc.gridx = 1;
        content.add(startField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        content.add(new JLabel("End Location:"), gbc);
        gbc.gridx = 1;
        content.add(endField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        content.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        content.add(distanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        content.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        content.add(priceField, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        content.add(message, gbc);

        JButton addBtn = new JButton("Add Route");
        addBtn.setBackground(new Color(212, 175, 55));
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e_ -> {
            try {
                String start = startField.getText();
                String end = endField.getText();
                String distance = distanceField.getText();
                String price = priceField.getText();

                if (start.isEmpty() || end.isEmpty() || distance.isEmpty() || price.isEmpty()) {
                    message.setText("Please fill all fields.");
                    return;
                }

                float distanceValue = Float.parseFloat(distance);
                float priceValue = Float.parseFloat(price);

                if (distanceValue <= 0 || priceValue <= 0) {
                    message.setText("Distance and price must be greater than 0.");
                    return;
                }

                executor.execute(() -> {
                    Route route = new Route(
                        DataStorage.loadRoutes().size() + 1,
                        start,
                        end,
                        distanceValue,
                        priceValue
                    );
                    DataStorage.saveRoute(route);
                    SwingUtilities.invokeLater(() -> {
                        dialog.dispose();
                        showRoutes();
                    });
                });
            } catch (NumberFormatException e) {
                message.setText("Please enter valid numbers for distance and price.");
            }
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(addBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private JPanel createRoutePanel(Route route) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 44, 44));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel(String.format("%s → %s (%.2f km) - $%.2f",
            route.getStartLocation(),
            route.getEndLocation(),
            route.getDistance(),
            route.getPrice()));
        info.setForeground(Color.WHITE);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(212, 175, 55));
        editBtn.setForeground(Color.BLACK);
        editBtn.addActionListener(e_ -> SwingUtilities.invokeLater(() -> showEditRouteDialog(route)));

        panel.add(info, BorderLayout.CENTER);
        panel.add(editBtn, BorderLayout.EAST);

        return panel;
    }

    private void showEditRouteDialog(Route route) {
        JDialog dialog = new JDialog(frame, "Edit Route", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField startField = new JTextField(route.getStartLocation(), 20);
        JTextField endField = new JTextField(route.getEndLocation(), 20);
        JTextField distanceField = new JTextField(String.valueOf(route.getDistance()), 20);
        JTextField priceField = new JTextField(String.valueOf(route.getPrice()), 20);

        gbc.gridx = 0; gbc.gridy = 0;
        content.add(new JLabel("Start Location:"), gbc);
        gbc.gridx = 1;
        content.add(startField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        content.add(new JLabel("End Location:"), gbc);
        gbc.gridx = 1;
        content.add(endField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        content.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        content.add(distanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        content.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        content.add(priceField, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        content.add(message, gbc);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(212, 175, 55));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.addActionListener(e_ -> {
            try {
                String start = startField.getText();
                String end = endField.getText();
                String distance = distanceField.getText();
                String price = priceField.getText();

                if (start.isEmpty() || end.isEmpty() || distance.isEmpty() || price.isEmpty()) {
                    message.setText("Please fill all fields.");
                    return;
                }

                float distanceValue = Float.parseFloat(distance);
                float priceValue = Float.parseFloat(price);

                if (distanceValue <= 0 || priceValue <= 0) {
                    message.setText("Distance and price must be greater than 0.");
                    return;
                }

                executor.execute(() -> {
                    route.setStartLocation(start);
                    route.setEndLocation(end);
                    route.setDistance(distanceValue);
                    route.setPrice(priceValue);
                    DataStorage.saveRoute(route);
                    SwingUtilities.invokeLater(() -> {
                        dialog.dispose();
                        showRoutes();
                    });
                });
            } catch (NumberFormatException e) {
                message.setText("Please enter valid numbers for distance and price.");
            }
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showAddScheduleDialog() {
        JDialog dialog = new JDialog(frame, "Add Schedule", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Route selection
        JLabel routeLabel = new JLabel("Route:");
        routeLabel.setForeground(Color.WHITE);
        List<Route> routes = DataStorage.loadRoutes();
        String[] routeOptions = routes.stream()
            .map(r -> r.getStartLocation() + " → " + r.getEndLocation())
            .toArray(String[]::new);
        JComboBox<String> routeBox = new JComboBox<>(routeOptions);
        gbc.gridx = 0; gbc.gridy = 0;
        content.add(routeLabel, gbc);
        gbc.gridx = 1;
        content.add(routeBox, gbc);

        // Date selection
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setForeground(Color.WHITE);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 0; gbc.gridy = 1;
        content.add(dateLabel, gbc);
        gbc.gridx = 1;
        content.add(dateSpinner, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        content.add(message, gbc);

        JButton addBtn = new JButton("Add Schedule");
        addBtn.setBackground(new Color(212, 175, 55));
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e_ -> {
            try {
                if (routes.isEmpty()) {
                    message.setText("No routes available. Please add a route first.");
                    return;
                }

                Date selectedDate = (Date) dateSpinner.getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedDate = dateFormat.format(selectedDate);

                executor.execute(() -> {
                    Route selectedRoute = routes.get(routeBox.getSelectedIndex());
                    Schedule schedule = new Schedule(
                        DataStorage.loadSchedules().size() + 1,
                        selectedRoute,
                        formattedDate
                    );
                    DataStorage.saveSchedule(schedule);
                    SwingUtilities.invokeLater(() -> {
                        dialog.dispose();
                        showSchedules();
                    });
                });
            } catch (Exception e) {
                message.setText("Error creating schedule.");
            }
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(addBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showEditScheduleDialog(Schedule schedule) {
        JDialog dialog = new JDialog(frame, "Edit Schedule", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date selection
        JLabel dateLabel = new JLabel("New Date:");
        dateLabel.setForeground(Color.WHITE);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm");
        dateSpinner.setEditor(dateEditor);
        
        // Set current date in spinner
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date currentDate = dateFormat.parse(schedule.getDepartureTime());
            dateSpinner.setValue(currentDate);
        } catch (Exception e) {
            dateSpinner.setValue(new Date());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        content.add(dateLabel, gbc);
        gbc.gridx = 1;
        content.add(dateSpinner, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        content.add(message, gbc);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(212, 175, 55));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.addActionListener(e_ -> {
            try {
                Date selectedDate = (Date) dateSpinner.getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String formattedDate = dateFormat.format(selectedDate);

                executor.execute(() -> {
                    schedule.setDepartureTime(formattedDate);
                    DataStorage.saveSchedule(schedule);
                    SwingUtilities.invokeLater(() -> {
                        dialog.dispose();
                        showSchedules();
                    });
                });
            } catch (Exception e) {
                message.setText("Error updating schedule.");
            }
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private JPanel createSchedulePanel(Schedule schedule) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 44, 44));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel(String.format("%s → %s on %s",
            schedule.getRoute().getStartLocation(),
            schedule.getRoute().getEndLocation(),
            schedule.getDepartureTime()));
        info.setForeground(Color.WHITE);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(212, 175, 55));
        editBtn.setForeground(Color.BLACK);
        editBtn.addActionListener(e_ -> SwingUtilities.invokeLater(() -> showEditScheduleDialog(schedule)));

        panel.add(info, BorderLayout.CENTER);
        panel.add(editBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createPassengerPanel(Passenger passenger) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 44, 44));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel(String.format("%s (%s) - Passport: %s",
            passenger.getName(),
            passenger.getEmail(),
            passenger.getPassportNumber()));
        info.setForeground(Color.WHITE);

        panel.add(info, BorderLayout.CENTER);

        return panel;
    }
} 