package View;
//import java.awt.event.*;
//import model.*;
import auth.*;
import java.awt.*;
import javax.swing.*;
import model.DataStorage;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Authentication auth;

    public MainFrame() {
        setTitle("Bus Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(34, 34, 34));

        auth = new Authentication();

        // Add panels
        mainPanel.add(new LoginPanel(this), "login");
        mainPanel.add(new RegisterPanel(this), "register");

        add(mainPanel);

        // Add window listener for cleanup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Cleanup resources
                DataStorage.shutdown();
            }
        });
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void showPanel(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel);
        cardLayout.show(mainPanel, panel.getName());
        revalidate();
        repaint();
    }

    public Authentication getAuth() {
        return auth;
    }

    // Entry point
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
