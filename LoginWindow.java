package Resume;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends JFrame implements ActionListener, Serializable {
    private static final long serialVersionUID = 1L;

    // GUI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, cancelButton, verifierButton;
    private JRadioButton studentRadioButton, recruiterRadioButton;
    private ButtonGroup roleGroup;

    public LoginWindow() {
        // Set the Look and Feel to the system's default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Frame properties
        setTitle("Login");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set background color for the frame
        getContentPane().setBackground(Color.decode("#F0F8FF")); // Light background color

        // Role Selection
        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setForeground(Color.decode("#2F4F4F")); // Dark slate gray
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(roleLabel, gbc);

        // Create radio buttons for role selection
        studentRadioButton = new JRadioButton("Student");
        studentRadioButton.setBackground(Color.decode("#E6E6FA")); // Lavender color

        recruiterRadioButton = new JRadioButton("Recruiter");
        recruiterRadioButton.setBackground(Color.decode("#E6E6FA")); // Lavender color

        // Group the radio buttons
        roleGroup = new ButtonGroup();
        roleGroup.add(studentRadioButton);
        roleGroup.add(recruiterRadioButton);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.setBackground(Color.decode("#F0F8FF")); // Same as the frame background
        rolePanel.add(studentRadioButton);
        rolePanel.add(recruiterRadioButton);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(rolePanel, gbc);

        // Username and Password Fields
        addLabelAndField(gbc, "Username:", usernameField = new JTextField(20), 1);
        addLabelAndField(gbc, "Password:", passwordField = new JPasswordField(20), 2);

        // Login Button
        loginButton = createModernButton("Login", "#48D1CC", "#20B2AA");
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        // Register Button
        registerButton = createModernButton("Register", "#20B2AA", "#48D1CC");
        registerButton.addActionListener(this);
        gbc.gridy = 4;
        add(registerButton, gbc);

        // Verifier Button
        verifierButton = createModernButton("Verifier", "#FFA500", "#FF8C00");
        verifierButton.addActionListener(this);
        gbc.gridy = 5;
        add(verifierButton, gbc);

        // Cancel Button
        cancelButton = createModernButton("Cancel", "#FF6347", "#FF4500");
        cancelButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 6;
        add(cancelButton, gbc);

        // Center the window and make it visible
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper method to create modern buttons with hover effects
    private JButton createModernButton(String text, String colorHex, String hoverColorHex) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode(colorHex));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(hoverColorHex));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(colorHex));
            }
        });

        return button;
    }

    // Helper method to add label and field in the grid
    private void addLabelAndField(GridBagConstraints gbc, String labelText, JTextField field, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.decode("#2F4F4F"));
        label.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        add(field, gbc);

        // Add tooltip for fields
        field.setToolTipText("Enter your " + labelText.toLowerCase());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            if (roleGroup.getSelection() == null) {
                JOptionPane.showMessageDialog(this, "Please select either Student or Recruiter.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String role = studentRadioButton.isSelected() ? "student" : "recruiter";
            new RegisterWindow(role); // Open register window with role
        } else if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String role = studentRadioButton.isSelected() ? "student" : "recruiter";
            Integer userId = validateCredentials(username, password, role);
            if (userId != null) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the login window

                // Create and display the ResumeBuilder window on the EDT
                SwingUtilities.invokeLater(() -> {
                    ResumeBuilder resumeBuilder = new ResumeBuilder(userId);
                    resumeBuilder.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } // In actionPerformed method, update verifierButton handling
        else if (e.getSource() == verifierButton) {
            // Prompt for ID and password using a custom dialog
            String idNum = JOptionPane.showInputDialog(this, "Enter ID Number:");
            
            if (idNum != null) {
                JPasswordField passwordField = new JPasswordField(10);
                passwordField.setEchoChar('\u2022'); // Set the dot character

                int option = JOptionPane.showConfirmDialog(
                    this, passwordField, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
                if (option == JOptionPane.OK_OPTION) {
                    String password = new String(passwordField.getPassword());

                    // Validate ID and password
                    boolean isValid = validateVerifierCredentials(idNum, password);
                    if (isValid) {
                        JOptionPane.showMessageDialog(this, "Verifier credentials are valid.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Close the login window

                        // Open the VerifierWindow
                        SwingUtilities.invokeLater(VerifierWindow::new);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid ID Number or Password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
       }
    

    private Integer validateCredentials(String username, String password, String role) {
        String tableName = role.equals("student") ? "student_db" : "recruiter_db";
        String query = "SELECT * FROM " + tableName + " WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id"); // Return "id" column value
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    private boolean validateVerifierCredentials(String idNum, String password) {
        String query = "SELECT * FROM verifier WHERE IDnum = ? AND pass = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, idNum);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // Returns true if there's a match
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if there's an error
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}