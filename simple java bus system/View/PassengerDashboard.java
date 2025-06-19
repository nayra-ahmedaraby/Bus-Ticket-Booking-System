package View;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PassengerDashboard extends JPanel {
    private final MainFrame frame;
    private final Passenger passenger;
    private JPanel contentPanel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Schedule> schedules;
    private List<Booking> bookings;

    public PassengerDashboard(Passenger passenger, MainFrame frame) {
        this.passenger = passenger;
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
            schedules = DataStorage.loadSchedules();
            List<Booking> allBookings = DataStorage.loadBookings();
            bookings = allBookings.stream()
                .filter(b -> b.getPassenger().getEmail().equals(passenger.getEmail()))
                .toList();

            // Update UI on EDT
            SwingUtilities.invokeLater(() -> {
                showAvailableSchedules();
                revalidate();
                repaint();
            });
        });
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 44, 44));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton schedulesBtn = createMenuButton("Available Schedules");
        JButton bookingsBtn = createMenuButton("My Bookings");

        schedulesBtn.addActionListener(e_ -> SwingUtilities.invokeLater(this::showAvailableSchedules));
        bookingsBtn.addActionListener(e_ -> SwingUtilities.invokeLater(this::showBookings));

        sidebar.add(schedulesBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(bookingsBtn);

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

    private void showAvailableSchedules() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 34, 34));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(34, 34, 34));
        JLabel title = new JLabel("Available Schedules");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(212, 175, 55));
        header.add(title);

        // Create main content panel with GridLayout
        JPanel mainContent = new JPanel(new GridLayout(0, 1, 10, 10));
        mainContent.setBackground(new Color(34, 34, 34));

        // Load all routes and their schedules
        List<Route> allRoutes = DataStorage.loadRoutes();
        schedules = DataStorage.loadSchedules();

        if (allRoutes.isEmpty()) {
            JLabel noRoutes = new JLabel("No routes available.");
            noRoutes.setForeground(Color.WHITE);
            mainContent.add(noRoutes);
        } else {
            for (Route route : allRoutes) {
                JPanel routePanel = createRoutePanel(route);
                mainContent.add(routePanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBackground(new Color(34, 34, 34));
        scrollPane.getViewport().setBackground(new Color(34, 34, 34));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.removeAll();
        contentPanel.add(panel, "schedules");
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "schedules");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createRoutePanel(Route route) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 44, 44));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Route header
        JLabel routeInfo = new JLabel(String.format("%s → %s - $%.2f",
            route.getStartLocation(),
            route.getEndLocation(),
            route.getPrice()));
        routeInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
        routeInfo.setForeground(new Color(212, 175, 55));

        // Schedules for this route
        JPanel schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        schedulesPanel.setBackground(new Color(44, 44, 44));

        List<Schedule> routeSchedules = schedules.stream()
            .filter(s -> s.getRoute().getRouteId() == route.getRouteId())
            .toList();

        if (routeSchedules.isEmpty()) {
            JLabel noSchedules = new JLabel("No schedules available for this route.");
            noSchedules.setForeground(Color.WHITE);
            schedulesPanel.add(noSchedules);
        } else {
            for (Schedule schedule : routeSchedules) {
                JPanel schedulePanel = createSchedulePanel(schedule);
                schedulesPanel.add(schedulePanel);
                schedulesPanel.add(Box.createVerticalStrut(5));
            }
        }

        panel.add(routeInfo, BorderLayout.NORTH);
        panel.add(schedulesPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSchedulePanel(Schedule schedule) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(54, 54, 54));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel info = new JLabel(String.format("Departure: %s",
            schedule.getDepartureTime()));
        info.setForeground(Color.WHITE);

        JButton bookBtn = new JButton("Book");
        bookBtn.setBackground(new Color(212, 175, 55));
        bookBtn.setForeground(Color.BLACK);
        bookBtn.addActionListener(e_ -> SwingUtilities.invokeLater(() -> showBookingDialog(schedule)));

        panel.add(info, BorderLayout.CENTER);
        panel.add(bookBtn, BorderLayout.EAST);

        return panel;
    }

    private void showBookingDialog(Schedule schedule) {
        JDialog dialog = new JDialog(frame, "Book Ticket", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(34, 34, 34));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(34, 34, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Show available seats
        JLabel seatsLabel = new JLabel("Available Seats:");
        seatsLabel.setForeground(Color.WHITE);
        JComboBox<Integer> seatBox = new JComboBox<>();
        for (int i = 1; i <= 50; i++) {
            if (schedule.isSeatAvailable(i)) {
                seatBox.addItem(i);
            }
        }

        gbc.gridx = 0; gbc.gridy = 0;
        content.add(seatsLabel, gbc);
        gbc.gridx = 1;
        content.add(seatBox, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        content.add(message, gbc);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.setBackground(new Color(212, 175, 55));
        bookBtn.setForeground(Color.BLACK);
        bookBtn.addActionListener(e_ -> {
            if (seatBox.getSelectedItem() == null) {
                message.setText("No seats available.");
                return;
            }

            int seat = (Integer) seatBox.getSelectedItem();
            executor.execute(() -> {
                Booking booking = new Booking(
                    DataStorage.loadBookings().size() + 1,
                    seat,
                    passenger,
                    schedule
                );
                schedule.bookSeat(seat);
                DataStorage.saveBooking(booking);
                DataStorage.saveSchedule(schedule);
                SwingUtilities.invokeLater(() -> {
                    dialog.dispose();
                    showBookings();
                });
            });
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(bookBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showBookings() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 34, 34));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(34, 34, 34));
        JLabel title = new JLabel("My Bookings");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(212, 175, 55));
        header.add(title);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(34, 34, 34));

        // Refresh bookings list
        List<Booking> allBookings = DataStorage.loadBookings();
        bookings = allBookings.stream()
            .filter(b -> b.getPassenger().getEmail().equals(passenger.getEmail()))
            .toList();

        if (bookings.isEmpty()) {
            JLabel noBookings = new JLabel("No bookings found.");
            noBookings.setForeground(Color.WHITE);
            content.add(noBookings);
        } else {
            for (Booking booking : bookings) {
                JPanel bookingPanel = createBookingPanel(booking);
                content.add(bookingPanel);
                content.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBackground(new Color(34, 34, 34));
        scrollPane.getViewport().setBackground(new Color(34, 34, 34));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.removeAll();
        contentPanel.add(panel, "bookings");
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "bookings");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createBookingPanel(Booking booking) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 44, 44));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel info = new JLabel(String.format("Ticket #%d - Seat %d - %s → %s on %s - Status: %s",
            booking.getBookingId(),
            booking.getSeatNumber(),
            booking.getSchedule().getRoute().getStartLocation(),
            booking.getSchedule().getRoute().getEndLocation(),
            booking.getSchedule().getDepartureTime(),
            booking.getStatus()));
        info.setForeground(Color.WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(212, 175, 55));
        cancelBtn.setForeground(Color.BLACK);
        
        // Only show cancel button if booking is not already cancelled
        if (!booking.getStatus().equals("Cancelled")) {
            cancelBtn.addActionListener(e_ -> {
                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to cancel this booking?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    executor.execute(() -> {
                        try {
                            // Update booking status
                            booking.setStatus("Cancelled");
                            
                            // Release the seat
                            Schedule schedule = booking.getSchedule();
                            schedule.releaseSeat(booking.getSeatNumber());
                            
                            // Save both the booking and schedule
                            DataStorage.saveBooking(booking);
                            DataStorage.saveSchedule(schedule);
                            
                            // Refresh the bookings view
                            SwingUtilities.invokeLater(() -> {
                                showBookings();
                                JOptionPane.showMessageDialog(
                                    frame,
                                    "Booking cancelled successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            });
                        } catch (Exception e) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                    frame,
                                    "Error cancelling booking: " + e.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            });
                        }
                    });
                }
            });
        } else {
            cancelBtn.setEnabled(false);
            cancelBtn.setText("Cancelled");
        }

        panel.add(info, BorderLayout.CENTER);
        panel.add(cancelBtn, BorderLayout.EAST);

        return panel;
    }
} 