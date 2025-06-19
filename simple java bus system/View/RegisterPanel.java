package View;
import java.awt.*;
import javax.swing.*;
import model.DataStorage;

public class RegisterPanel extends JPanel {
    public RegisterPanel(MainFrame frame) {
        setBackground(new Color(34, 34, 34));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(212,175,55));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        JTextField emailField = new JTextField(18);
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(emailLabel, gbc); gbc.gridx = 1;
        add(emailField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passField = new JPasswordField(18);
        gbc.gridy = 2; gbc.gridx = 0;
        add(passLabel, gbc); gbc.gridx = 1;
        add(passField, gbc);

        JLabel typeLabel = new JLabel("Account Type:");
        typeLabel.setForeground(Color.WHITE);
        String[] types = {"Passenger", "Admin"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        gbc.gridy = 3; gbc.gridx = 0;
        add(typeLabel, gbc); gbc.gridx = 1;
        add(typeBox, gbc);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        JTextField nameField = new JTextField(18);
        gbc.gridy = 4; gbc.gridx = 0;
        add(nameLabel, gbc); gbc.gridx = 1;
        add(nameField, gbc);

        JLabel passportLabel = new JLabel("Passport No.:");
        passportLabel.setForeground(Color.WHITE);
        JTextField passportField = new JTextField(18);
        gbc.gridy = 5; gbc.gridx = 0;
        add(passportLabel, gbc); gbc.gridx = 1;
        add(passportField, gbc);

        JButton regBtn = new JButton("Register");
        regBtn.setBackground(new Color(212,175,55));
        regBtn.setForeground(Color.BLACK);
        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(44,44,44));
        backBtn.setForeground(new Color(212,175,55));
        gbc.gridy = 6; gbc.gridx = 0;
        add(regBtn, gbc); gbc.gridx = 1;
        add(backBtn, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        add(message, gbc);

        // Hide passport for admin
        typeBox.addActionListener(e_ -> {
            passportLabel.setVisible(typeBox.getSelectedIndex() == 0);
            passportField.setVisible(typeBox.getSelectedIndex() == 0);
        });
        passportLabel.setVisible(true);
        passportField.setVisible(true);

        regBtn.addActionListener(e_ -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            String type = (String)typeBox.getSelectedItem();
            String name = nameField.getText();

            if(email.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                message.setText("Please fill all required fields.");
                return;
            }

            if(type.equals("Passenger") && passportField.getText().isEmpty()) {
                message.setText("Please enter passport number.");
                return;
            }

            model.User user = frame.getAuth().createAccount(email, pass, type);
            if(user != null) {
                if(type.equals("Passenger")) {
                    ((model.Passenger)user).setName(name);
                    ((model.Passenger)user).setPassportNumber(passportField.getText());
                    DataStorage.saveUser(user); // Save the updated passenger data
                    message.setText("Passenger account created! Please login.");
                } else {
                    user.setName(name);
                    DataStorage.saveUser(user); // Save the updated admin data
                    message.setText("Admin account created! Please login.");
                }
                // Clear fields
                emailField.setText("");
                passField.setText("");
                nameField.setText("");
                passportField.setText("");
                // Go back to login after 2 seconds
                Timer timer = new Timer(2000, e -> {
                    frame.showPanel("login");
                    ((Timer)e.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                message.setText("Email already exists.");
            }
        });

        backBtn.addActionListener(e_ -> {
            frame.showPanel("login");
            message.setText(" "); // Clear any error messages
        });
    }
}