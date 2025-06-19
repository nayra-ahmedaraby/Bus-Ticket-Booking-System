package View;
import javax.swing.*;
import java.awt.*;
public class LoginPanel extends JPanel {
 public LoginPanel(MainFrame frame) {
        setBackground(new Color(34, 34, 34));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login to SwiftRide");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(212,175,55)); // Gold
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        JTextField emailField = new JTextField(18);
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(emailLabel, gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passField = new JPasswordField(18);
        gbc.gridx = 0; gbc.gridy = 2;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(212,175,55));
        loginBtn.setForeground(Color.BLACK);

        JButton regBtn = new JButton("Create Account");
        regBtn.setBackground(new Color(44,44,44));
        regBtn.setForeground(new Color(212,175,55));
        gbc.gridy = 3; gbc.gridx = 0;
        add(loginBtn, gbc);
        gbc.gridx = 1;
        add(regBtn, gbc);

        JLabel message = new JLabel(" ");
        message.setForeground(Color.RED);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(message, gbc);

        loginBtn.addActionListener(e_ -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                message.setText("Please fill all fields.");
                return;
            }

            model.User user = frame.getAuth().login(email, pass);
            if(user != null) {
                if (user instanceof model.Admin) {
                    frame.showPanel(new AdminDashboard(frame));
                } else {
                    frame.showPanel(new PassengerDashboard((model.Passenger)user, frame));
                }
            } else {
                message.setText("Invalid credentials.");
            }
        });

        regBtn.addActionListener(e_ -> {
            frame.showPanel("register");
            message.setText(" "); // Clear any error messages
        });
    }
}
