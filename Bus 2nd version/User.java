import java.awt.*;
import java.io.*;
import javax.swing.*;

// Interface for displaying data
interface Displayable {
    String displayData();
}

// Abstract base class for users
public abstract class User {
    // Predefined admin accounts
    private static final String[][] PREDEFINED_ADMINS = {
        {"admin1", "admin123"},
        {"superadmin", "super456"}
    };

    // Enum for User Types
    enum UserType {
        PASSENGER,
        ADMIN
    }

    private String username;
    private String password;
    private UserType type;
    private static int userCount = 0;

    // Constructor Chaining
    public User(String username) {
        this(username, "defaultPass");
    }

    public User(String username, String password) {
        this(username, password, UserType.PASSENGER);
    }

    public User(String username, String password, UserType type) {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be at least 3 characters and cannot contain spaces!");
        }
        this.username = username;
        this.password = password;
        this.type = type;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public UserType getType() {
        return type;
    }

    // Setters
    public void setPassword(String password) {
        this.password = password;
    }

    // Abstract Method
    public abstract void showMenu(User[] users, Schedule[] schedules, DataStorage.Booking[] bookings);

    // toString
    @Override
    public String toString() {
        return username + "," + password + "," + type.name();
    }

    // Helper method to validate username
    private static boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && !username.contains(" ");
    }

    // Parse a line to create a User object
    public static User parseUser(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            try {
                UserType type = UserType.valueOf(parts[2]);
                if (type == UserType.PASSENGER) {
                    return new Passenger(parts[0], parts[1]);
                } else if (type == UserType.ADMIN) {
                    return new Admin(parts[0], parts[1]);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Invalid user type: " + line);
            }
        }
        return null;
    }

    // Load users from file
    public static void loadUsers(User[] users) {
        try {
            File file = new File("users.txt");
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            userCount = 0;
            while ((line = reader.readLine()) != null && userCount < users.length) {
                User user = parseUser(line);
                if (user != null) {
                    users[userCount] = user;
                    userCount++;
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
        }
    }

    // Save users to file
    public static void saveUsers(User[] users) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"));
            for (int i = 0; i < userCount; i++) {
                writer.write(users[i].toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving users: " + e.getMessage());
        }
    }

    // Check if a user exists
    private static boolean userExists(String username, User[] users) {
        // Check registered users
        for (int i = 0; i < userCount; i++) {
            if (users[i].username.equals(username)) {
                return true;
            }
        }
        // Check predefined admins
        for (String[] admin : PREDEFINED_ADMINS) {
            if (admin[0].equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Find a user
    private static User findUser(String username, String password, User[] users) {
        // First check predefined admins
        for (String[] admin : PREDEFINED_ADMINS) {
            if (admin[0].equals(username) && admin[1].equals(password)) {
                return new Admin(username, password);
            }
        }

        // Then check users.txt
        for (int i = 0; i < userCount; i++) {
            if (users[i].username.equals(username) && users[i].password.equals(password)) {
                return users[i];
            }
        }
        return null;
    }

    // GUI for Register
    public static void showRegisterFrame(User[] users) {
        JFrame frame = new JFrame("Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setMinimumSize(new Dimension(300, 200));
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(230, 240, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPassField = new JPasswordField(15);
        JLabel typeLabel = new JLabel("Type (PASSENGER/ADMIN):");
        JTextField typeField = new JTextField(15);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(userLabel, gbc);
        gbc.gridx = 1;
        frame.add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(passLabel, gbc);
        gbc.gridx = 1;
        frame.add(passField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(confirmPassLabel, gbc);
        gbc.gridx = 1;
        frame.add(confirmPassField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(typeLabel, gbc);
        gbc.gridx = 1;
        frame.add(typeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(registerButton, gbc);
        gbc.gridx = 1;
        frame.add(backButton, gbc);

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());
            String typeStr = typeField.getText().toUpperCase();

            if (!isValidUsername(username)) {
                JOptionPane.showMessageDialog(frame, "Username must be at least 3 characters and cannot contain spaces!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!");
                return;
            }

            UserType type;
            try {
                type = UserType.valueOf(typeStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid type! Use PASSENGER or ADMIN.");
                return;
            }

            if (userExists(username, users)) {
                JOptionPane.showMessageDialog(frame, "Username already exists!");
                return;
            }

            if (userCount < users.length) {
                if (type == UserType.PASSENGER) {
                    users[userCount] = new Passenger(username, password);
                } else {
                    users[userCount] = new Admin(username, password);
                }
                userCount++;
                saveUsers(users);
                JOptionPane.showMessageDialog(frame, "Registration successful!");
                frame.dispose();
                Main.main(null);
            } else {
                JOptionPane.showMessageDialog(frame, "User limit reached!");
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            Main.main(null);
        });

        frame.setVisible(true);
    }

    // GUI for Login
    public static void showLoginFrame(User[] users, Schedule[] schedules, DataStorage.Booking[] bookings) {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setMinimumSize(new Dimension(250, 120));
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(230, 240, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(userLabel, gbc);
        gbc.gridx = 1;
        frame.add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(passLabel, gbc);
        gbc.gridx = 1;
        frame.add(passField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(loginButton, gbc);
        gbc.gridx = 1;
        frame.add(backButton, gbc);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            User user = findUser(username, password, users);
            if (user == null) {
                boolean usernameExists = false;
                for (String[] admin : PREDEFINED_ADMINS) {
                    if (admin[0].equals(username)) {
                        usernameExists = true;
                        break;
                    }
                }
                for (int i = 0; i < userCount; i++) {
                    if (users[i].username.equals(username)) {
                        usernameExists = true;
                        break;
                    }
                }
                if (usernameExists) {
                    JOptionPane.showMessageDialog(frame, "Incorrect password!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Username not found!");
                }
                return;
            }

            frame.dispose();
            user.showMenu(users, schedules, bookings);
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            Main.main(null);
        });

        frame.setVisible(true);
    }
}