package Resume;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class VerifierWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private DefaultListModel<String> nameListModel;
    private JList<String> nameList;
    private JPanel detailsPanel;
    private JButton verifyButton;
    private JButton unverifyButton; // Add unverify button


    public VerifierWindow() {
        setTitle("User Verifier");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Name list model and list setup
        nameListModel = new DefaultListModel<>();
        nameList = new JList<>(nameListModel);
        loadNameList(); // Load student names initially

        // Add ListSelectionListener to enable/disable buttons based on selection
        nameList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedName = nameList.getSelectedValue();
                if (selectedName != null) {
                    loadStudentDetails(selectedName);
                    verifyButton.setEnabled(true);  // Enable buttons when a name is selected
                    unverifyButton.setEnabled(true);
                } else {
                    verifyButton.setEnabled(false);  // Disable buttons if no name is selected
                    unverifyButton.setEnabled(false);
                }
            }
        });

        JScrollPane nameScrollPane = new JScrollPane(nameList);
        add(nameScrollPane, BorderLayout.WEST);

        // Details panel for vertical alignment
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(0, 2, 10, 10)); // Two columns for label and value

        // Wrap detailsPanel in JScrollPane to make it scrollable
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setPreferredSize(new Dimension(600, 500)); // Set a preferred size for the scroll pane
        add(detailsScrollPane, BorderLayout.CENTER);

        // Buttons panel for Verify and Unverify buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        verifyButton = new JButton("Verify Selected");
        verifyButton.addActionListener(e -> verifySelectedUsers());
        verifyButton.setEnabled(false); // Initially disable the button
        buttonPanel.add(verifyButton);

        unverifyButton = new JButton("Unverify Selected");
        unverifyButton.addActionListener(e -> unverifySelectedUsers()); // Unverify action
        unverifyButton.setEnabled(false); // Initially disable the button
        buttonPanel.add(unverifyButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadNameList() {
        String query = "SELECT name FROM resumes";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            nameListModel.clear();
            while (rs.next()) {
                String name = rs.getString("name");
                nameListModel.addElement(name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading student names.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentDetails(String name) {
        detailsPanel.removeAll(); // Clear previous details

        String query = "SELECT phone, email, cgpa, cgpa_certificate_path, tenth_mark, tenth_marks_path, twelfth_mark, twelfth_marks_path, " +
                       "hackathons_won, hackathons_won_proof, hackathons_participated, hackathons_participated_proof " +
                       "FROM resumes WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Add basic details
                addDetailRow("Phone", rs.getString("phone"));
                addDetailRow("Email", rs.getString("email"));
                addDetailRow("CGPA", String.valueOf(rs.getDouble("cgpa")));
                addDetailRow("CGPA Cert Path", rs.getString("cgpa_certificate_path"));
                addDetailRow("10th Mark", rs.getString("tenth_mark"));
                addDetailRow("10th Mark Path", rs.getString("tenth_marks_path"));
                addDetailRow("12th Mark", rs.getString("twelfth_mark"));
                addDetailRow("12th Mark Path", rs.getString("twelfth_marks_path"));

                // Instruction label for hackathons
                JLabel topFourLabel = new JLabel("*** Consider Only Top 4 from the total ***");
                topFourLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                detailsPanel.add(topFourLabel);
                detailsPanel.add(new JLabel()); // Empty label for alignment

                // Hackathons details
                addDetailRow("Hackathons Won", rs.getString("hackathons_won"));
                addDetailRow("Hackathons Won Proof", rs.getString("hackathons_won_proof"));
                addDetailRow("Hackathons Participated", rs.getString("hackathons_participated"));
                addDetailRow("Hackathons Participated Proof", rs.getString("hackathons_participated_proof"));

                // Calculate and display hackathon marks
                int hackathonMarks = calculateHackathonMarks(rs.getString("hackathons_won"), rs.getString("hackathons_participated"));
                addDetailRow("Total Hackathon Marks", String.valueOf(hackathonMarks));

                // Calculate and display placement criteria marks for CGPA, 10th, and 12th
                double cgpa = rs.getDouble("cgpa");
                double cgpaMarks = calculateCGPAMarks(cgpa);
                addDetailRow("CGPA Placement Criteria Marks", String.valueOf(cgpaMarks));

                // 10th Mark Placement Criteria
                double tenthMark = 0;
                String tenthMarkStr = rs.getString("tenth_mark");
                if (tenthMarkStr != null && !tenthMarkStr.isEmpty()) {
                    try {
                        tenthMark = Double.parseDouble(tenthMarkStr);
                        double tenthMarkMarks = calculatePlacementCriteria(tenthMark);
                        addDetailRow("Mark in Placement Criteria for 10th", String.valueOf(tenthMarkMarks));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid 10th mark format.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // 12th Mark Placement Criteria
                double twelfthMark = 0;
                String twelfthMarkStr = rs.getString("twelfth_mark");
                if (twelfthMarkStr != null && !twelfthMarkStr.isEmpty()) {
                    try {
                        twelfthMark = Double.parseDouble(twelfthMarkStr);
                        double twelfthMarkMarks = calculatePlacementCriteria(twelfthMark);
                        addDetailRow("Mark in Placement Criteria for 12th", String.valueOf(twelfthMarkMarks));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid 12th mark format.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Calculate total marks
                double totalMarks = hackathonMarks + cgpaMarks + calculatePlacementCriteria(tenthMark) + calculatePlacementCriteria(twelfthMark);

                // Add total placement criteria marks
                addDetailRow("Total Placement Criteria Marks", String.format("%.1f", totalMarks));

                // Update total marks in the database
                updateTotalMarksInDatabase(name, totalMarks);
            }

            detailsPanel.revalidate(); // Refresh the panel
            detailsPanel.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading student details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to update the total marks in the verifieddata table
    private void updateTotalMarksInDatabase(String name, double totalMarks) {
        String query = "UPDATE verifieddata SET PCtotal = ? WHERE id = (SELECT id FROM resumes WHERE name = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDouble(1, totalMarks); // Set the total marks
            stmt.setString(2, name); // Set the name of the selected user
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating total marks in the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateHackathonMarks(String hackathonsWon, String hackathonsParticipated) {
        int marks = 0;

        if (hackathonsWon != null && !hackathonsWon.isEmpty()) {
            marks += 4; // Award 4 marks for winning hackathon(s)
        }
        if (hackathonsParticipated != null && !hackathonsParticipated.isEmpty()) {
            marks += 1; // Award 1 mark for participating in hackathon(s)
        }

        return marks;
    }

    private double calculateCGPAMarks(double cgpa) {
        if (cgpa >= 9) {
            return 5.0; // Award 5 marks for CGPA 9 and above
        } else if (cgpa >= 8) {
            return 4.0; // Award 4 marks for CGPA between 8 and 8.99
        } else if (cgpa >= 7) {
            return 3.0; // Award 3 marks for CGPA between 7 and 7.99
        } else if (cgpa >= 6) {
            return 2.0; // Award 2 marks for CGPA between 6 and 6.99
        } else {
            return 1.0; // Award 1 mark for CGPA less than 6
        }
    }

    private double calculatePlacementCriteria(double marks) {
        if (marks >= 90) {
            return 5.0; // Award 5 marks for 90% and above
        } else if (marks >= 80) {
            return 4.0; // Award 4 marks for marks between 80% and 89.99%
        } else if (marks >= 70) {
            return 3.0; // Award 3 marks for marks between 70% and 79.99%
        } else if (marks >= 60) {
            return 2.0; // Award 2 marks for marks between 60% and 69.99%
        } else {
            return 1.0; // Award 1 mark for marks below 60%
        }
    }
    
    private void addDetailRow(String label, String value) {
        JLabel fieldLabel = new JLabel(label + ":");
        JLabel fieldValue = new JLabel(value);

        fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        fieldValue.setFont(new Font("SansSerif", Font.PLAIN, 14));

        detailsPanel.add(fieldLabel); // Add label to the left
        detailsPanel.add(fieldValue); // Add value to the right

        // If the value is a file path, add a mouse listener to open the file
        if (value != null && !value.isEmpty()) {
            File file = new File(value.replace("\\", "/")); // Convert backslashes to forward slashes
            if (file.exists()) {
                fieldValue.setForeground(Color.BLUE);
                fieldValue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                fieldValue.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            if (file.exists()) {
                                Desktop.getDesktop().open(file); // Attempt to open the file
                            } else {
                                JOptionPane.showMessageDialog(VerifierWindow.this, "File not found.");
                            }
                        } catch (IOException ex) {
                            // Handle the error
                            JOptionPane.showMessageDialog(VerifierWindow.this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            } else {
                // If the file doesn't exist, make the text non-clickable
                fieldValue.setForeground(Color.GRAY);
            }
        }
    }
  

    private void verifySelectedUsers() {
        String selectedName = nameList.getSelectedValue();
        if (selectedName != null) {
            // Check current status in the database
            if (getVerificationStatus(selectedName) == 0) {
                updateVerificationStatus(selectedName, 1); // Verify the user (status 1)
                JOptionPane.showMessageDialog(this, "Selected user has been verified.");
            } else {
                JOptionPane.showMessageDialog(this, "User is already verified.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to verify.");
        }
    }

    private void unverifySelectedUsers() {
        String selectedName = nameList.getSelectedValue();
        if (selectedName != null) {
            // Check current status in the database
            if (getVerificationStatus(selectedName) == 1) {
                updateVerificationStatus(selectedName, 0); // Unverify the user (status 0)
                JOptionPane.showMessageDialog(this, "Selected user has been unverified.");
            } else {
                JOptionPane.showMessageDialog(this, "User is already unverified.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to unverify.");
        }
    }

    private int getVerificationStatus(String name) {
        String query = "SELECT status FROM verifieddata WHERE id = (SELECT id FROM resumes WHERE name = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("status");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking verification status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1; // Return -1 if no status is found
    }

    private void updateVerificationStatus(String name, int status) {
        String query = "UPDATE verifieddata SET status = ? WHERE id = (SELECT id FROM resumes WHERE name = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, status); // Set status to 1 (verified) or 0 (unverified)
            stmt.setString(2, name); // Set name of the selected user
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating verification status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
  
    public static void main(String[] args) {
        new VerifierWindow();
    }
}