package Resume;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.Serializable;

class RoundedButton extends JButton {
    private static final long serialVersionUID = 1L; // For serialization
    public Color hoverBackgroundColor;
    public Color normalBackgroundColor;

    public RoundedButton(String text, Color normalBackgroundColor, Color hoverBackgroundColor) {
        super(text);
        this.normalBackgroundColor = normalBackgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;

        // Set default properties
        setContentAreaFilled(false);
        setOpaque(true);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
        setBackground(normalBackgroundColor); // Set initial background color

        // Add mouse listeners for hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackgroundColor); // Use hover color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalBackgroundColor); // Use normal color
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded corners
        super.paintComponent(g);
    }
}

public class RegisterWindow extends JFrame implements ActionListener, Serializable {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedButton registerButton, cancelButton;
    private String role; // To store whether the user is registering as a "student" or "recruiter"

    public RegisterWindow(String role) {
        this.role = role; // Store the role passed from the LoginWindow

        setTitle("Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set a background color
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue

        // Title Label
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180)); // Steel Blue
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; // Span both columns
        add(titleLabel, gbc);

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; // Reset grid width
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        add(usernameField, gbc);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        add(passwordField, gbc);

        // Register Button
        registerButton = new RoundedButton("Register", new Color(70, 130, 180), new Color(100, 149, 237)); // Steel Blue and Cornflower Blue hover
        registerButton.addActionListener(this); // Register button action
        gbc.gridx = 0; gbc.gridy = 3; 
        gbc.gridwidth = 2; // Span both columns
        add(registerButton, gbc);

        // Cancel Button
        cancelButton = new RoundedButton("Cancel", Color.RED, new Color(255, 99, 71)); // Red and Tomato hover
        cancelButton.addActionListener(e -> dispose()); // Close window
        gbc.gridy = 4; 
        add(cancelButton, gbc);

        // Center the window
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if fields are empty
        }

        // Register user in the appropriate database based on the role
        if (registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close window after successful registration
            // Open the login window again
            SwingUtilities.invokeLater(LoginWindow::new); // Open the LoginWindow again
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean registerUser(String username, String password) {
        // Adjust table name based on the role
        String tableName = role.equals("student") ? "student_db" : "recruiters_db";
        String query = "INSERT INTO " + tableName + " (username, password) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Returns true if the user was registered
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace for debugging
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterWindow("student")); // Default to "student"
    }
}