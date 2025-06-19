package auth;

import model.User;
import model.Admin;
import model.Passenger;
import model.DataStorage;

public class Authentication {
    private static final String ADMIN_EMAIL = "admin@bus.com";
    private static final String ADMIN_PASSWORD = "admin123";

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        // Admin login
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            return new Admin(1, "Admin", email, password, "Management");
        }
        
        // Passenger login
        return DataStorage.loadUsers().stream()
            .filter(u -> u instanceof Passenger)
            .map(u -> (Passenger)u)
            .filter(p -> p.getEmail().equals(email) && p.getPassword().equals(password))
            .findFirst()
            .orElse(null);
    }

    public User createAccount(String email, String password, String type) {
        if (email == null || password == null || type == null) {
            return null;
        }

        // Check if email already exists
        if (DataStorage.loadUsers().stream().anyMatch(u -> u.getEmail().equals(email))) {
            return null;
        }

        User newUser;
        if (type.equalsIgnoreCase("admin")) {
            newUser = new Admin(DataStorage.loadUsers().size() + 1, "New Admin", email, password, "General");
        } else {
            newUser = new Passenger(email, password);
        }
        
        DataStorage.saveUser(newUser);
        return newUser;
    }
}