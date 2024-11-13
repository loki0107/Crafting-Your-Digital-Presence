package Resume;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResumeBuilder extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JTextField nameField, phoneField, tenthMarkField, twelfthMarkField;
    private JSlider cgpaSlider;
    private JLabel cgpaLabel;
    private JButton saveButton, cancelButton, uploadButton, viewResumeButton, uploadCgpaCertificateButton;
    private Integer userId;
    private String profileImagePath;
    private JPanel skillsPanel;
    private ArrayList<JCheckBox> skillCheckboxes;
    private JButton upload10thMarksButton, upload12thMarksButton;
    private JPanel contentPanel;
    private JLabel profileImagePathLabel, cgpaCertificatePathLabel, tenthMarksPathLabel, twelfthMarksPathLabel;
    private JTextField hackathonsWonField, hackathonsParticipatedField;
    private JButton uploadHackathonsWonProofButton, uploadHackathonsParticipatedProofButton;
    private JLabel hackathonsWonProofLabel, hackathonsParticipatedProofLabel;


    public ResumeBuilder(int userId) {
        this.userId = userId;
        setTitle("Craft Your Digital Presence");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set panel for main content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Aligns components vertically
        contentPanel.setBackground(Color.decode("#FFFFFF"));

        // Title label
        JLabel titleLabel = new JLabel("Enter Your Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#00796b"));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20)); // Spacer

        // Initialize buttons first
        saveButton = createModernButton("Save", "#004d40", "#00796b");
        cancelButton = createModernButton("Cancel", "#d32f2f", "#e57373");
        viewResumeButton = createModernButton("ViewResume", "#1976d2", "#64b5f6");

        // Other fields and components
        addLabeledField(contentPanel, "Name:", nameField = new JTextField(25));
        addLabeledField(contentPanel, "Phone:", phoneField = new JTextField(25));

        // CGPA Slider
        addSliderPanel(contentPanel);

        // Skills Panel
        addSkillsPanel(contentPanel);

        // Marks fields
        addLabeledField(contentPanel, "10th Marks (%):", tenthMarkField = new JTextField(25));
        addLabeledField(contentPanel, "12th Marks (%):", twelfthMarkField = new JTextField(25));
        

        addLabeledField(contentPanel, "Hackathons Won:", hackathonsWonField = new JTextField(25));
        hackathonsWonProofLabel = new JLabel("No file selected");
        uploadHackathonsWonProofButton = createUploadButton("Upload Hackathons Won Proof", hackathonsWonProofLabel);
        contentPanel.add(uploadHackathonsWonProofButton);
        contentPanel.add(hackathonsWonProofLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        addLabeledField(contentPanel, "Hackathons Participated:", hackathonsParticipatedField = new JTextField(25));
        hackathonsParticipatedProofLabel = new JLabel("No file selected");
        uploadHackathonsParticipatedProofButton = createUploadButton("Upload Hackathons Participated Proof", hackathonsParticipatedProofLabel);
        contentPanel.add(uploadHackathonsParticipatedProofButton);
        contentPanel.add(hackathonsParticipatedProofLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Initialize labels for file paths
        profileImagePathLabel = new JLabel("No file selected");
        cgpaCertificatePathLabel = new JLabel("No file selected");
        tenthMarksPathLabel = new JLabel("No file selected");
        twelfthMarksPathLabel = new JLabel("No file selected");

        // Upload Buttons and their path labels
        uploadButton = createUploadButton("Upload Profile Image", profileImagePathLabel);
        contentPanel.add(uploadButton);
        contentPanel.add(profileImagePathLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        uploadCgpaCertificateButton = createUploadButton("Upload CGPA Certificate", cgpaCertificatePathLabel);
        contentPanel.add(uploadCgpaCertificateButton);
        contentPanel.add(cgpaCertificatePathLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        upload10thMarksButton = createUploadButton("Upload 10th Marks Proof", tenthMarksPathLabel);
        contentPanel.add(upload10thMarksButton);
        contentPanel.add(tenthMarksPathLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        upload12thMarksButton = createUploadButton("Upload 12th Marks Proof", twelfthMarksPathLabel);
        contentPanel.add(upload12thMarksButton);
        contentPanel.add(twelfthMarksPathLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Button Panel
        addButtonPanel(contentPanel);

        // Scrollable content panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        
       
        loadUserData(); // Load existing data (if any)
        
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        viewResumeButton.addActionListener(this);
        uploadButton.addActionListener(this);
        uploadCgpaCertificateButton.addActionListener(this);
        upload10thMarksButton.addActionListener(this);
        upload12thMarksButton.addActionListener(this);

        setSize(600, 800); // Adjusted window size
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JButton createUploadButton(String text, JLabel pathLabel) {
        JButton button = createModernButton(text, "#005f6a", "#00796b");
        button.addActionListener(e -> openFileChooser(pathLabel));
        return button;
    }

    private void openFileChooser(JLabel pathLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();
            pathLabel.setText(selectedPath); // Update the label with the file path
            JOptionPane.showMessageDialog(this, "File uploaded successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No file selected.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == uploadButton) {
            uploadImage();
        } else if (source == saveButton) {
            saveUserData();
        } else if (source == cancelButton) {
            dispose();
        } else if (source == viewResumeButton) {
            new ResumeViewer(userId); // Open Resume Viewer
        } else if (source == uploadCgpaCertificateButton) {
            uploadMarksProof("CGPA Certificate");
        } else if (source == upload10thMarksButton) {
            uploadMarksProof("10th Marks Proof");
        } else if (source == upload12thMarksButton) {
            uploadMarksProof("12th Marks Proof");
        }
        else if (source == uploadHackathonsWonProofButton) {
            openFileChooser(hackathonsWonProofLabel);
        } else if (source == uploadHackathonsParticipatedProofButton) {
            openFileChooser(hackathonsParticipatedProofLabel);
        }
    }
    
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            profileImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            JOptionPane.showMessageDialog(this, "Profile image uploaded successfully.");

            String query = "UPDATE resumes SET profile_image_path = ? WHERE id = ?";
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, profileImagePath);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving profile image path: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No image selected.");
        }
    }

    private void uploadMarksProof(String markType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select " + markType);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String proofPath = fileChooser.getSelectedFile().getAbsolutePath();
            JOptionPane.showMessageDialog(this, markType + " uploaded successfully.");

            String query = "";
            if (markType.equals("10th Marks Proof")) {
                query = "UPDATE resumes SET tenth_marks_path= ? WHERE id = ?";
            } else if (markType.equals("12th Marks Proof")) {
                query = "UPDATE resumes SET twelfth_marks_path = ? WHERE id = ?";
            } else if (markType.equals("CGPA Certificate")) {
                query = "UPDATE resumes SET cgpa_certificate_path = ? WHERE id = ?";
            }

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, proofPath);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving file path: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file selected.");
        }
    }

    private void addLabeledField(JPanel panel, String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, field.getPreferredSize().height));
        fieldPanel.add(label);
        fieldPanel.add(field);
        panel.add(fieldPanel);
        panel.add(Box.createVerticalStrut(10)); // Spacer
    }

    private void addSliderPanel(JPanel panel) {
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        JLabel sliderLabel = new JLabel("CGPA:");
        sliderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cgpaSlider = new JSlider(0, 100, 0);
        cgpaSlider.setPreferredSize(new Dimension(250, cgpaSlider.getPreferredSize().height));
        cgpaSlider.setMajorTickSpacing(10);
        cgpaSlider.setMinorTickSpacing(1);
        cgpaSlider.setPaintTicks(true);
        cgpaSlider.setPaintLabels(true);
        cgpaLabel = new JLabel("0.0");
        cgpaSlider.addChangeListener(e -> cgpaLabel.setText(String.valueOf(cgpaSlider.getValue() / 10.0)));

        sliderPanel.add(sliderLabel);
        sliderPanel.add(cgpaSlider);
        sliderPanel.add(cgpaLabel);
        panel.add(sliderPanel);
        panel.add(Box.createVerticalStrut(10)); // Spacer
    }

    private void addSkillsPanel(JPanel panel) {
        JPanel skillsPanelContainer = new JPanel();
        skillsPanelContainer.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align to the left

        // Create a "Skills" label with a modern font, bold, and left-aligned
        JLabel skillsLabel = new JLabel("Skills:");
        skillsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Modern bold font
        skillsPanelContainer.add(skillsLabel);

        skillsPanel = new JPanel();
        // Set GridLayout with 3 columns for 3 checkboxes per row
        skillsPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 0 rows (dynamically determined), 3 columns, 10px horizontal and vertical spacing
        skillCheckboxes = new ArrayList<>();
        
        // Expanded list of skills
        String[] skills = {"Java", "Python", "C", "JavaScript", "SQL", "HTML", "CSS", "Node.js", "React", "Git", "Docker", "AWS"};
        
        // Create and add checkboxes for each skill
        for (String skill : skills) {
            JCheckBox skillCheckBox = new JCheckBox(skill);
            skillCheckboxes.add(skillCheckBox);
            skillsPanel.add(skillCheckBox);
        }

        skillsPanelContainer.add(skillsPanel);
        panel.add(skillsPanelContainer);
        panel.add(Box.createVerticalStrut(10)); // Spacer
    }

    private void addButtonPanel(JPanel panel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        viewResumeButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(viewResumeButton);

        buttonPanel.setBackground(Color.decode("#f4f4f4"));
        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(20)); // Spacer
    }

    private JButton createModernButton(String text, String bgColor, String hoverColor) {
        JButton button = new JButton(text);

        // Set a modern background color (soft blue)
        button.setBackground(new Color(0, 123, 255)); // Soft blue background
        button.setForeground(Color.WHITE); // Text color to white

        // Modern font and slightly smaller size for a more compact button
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Slightly smaller font size for compactness

        // Remove the default border and add a subtle custom border
        button.setFocusPainted(false); // Remove focus border
        button.setOpaque(true); // Ensure the button background is visible
        button.setBorderPainted(false); // Remove default border

        // Set a smooth rounded border with padding for modern styling
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 118, 255), 2), // Light blue border
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Less padding for smaller button
        ));

        // Adjust size to a more compact, modern button size
        button.setPreferredSize(new Dimension(140, 35)); // Smaller size for more compact look
        button.setMargin(new Insets(5, 15, 5, 15)); // Less padding inside button for compactness

        // Hover effect for background and text color change
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204)); // Darker blue on hover
                button.setForeground(Color.WHITE); // Keep text white
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255)); // Reset to original blue
                button.setForeground(Color.WHITE); // Keep text white
            }
        });

        return button;
    }

    

    private void saveUserData() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String tenthMarks = tenthMarkField.getText();
        String twelfthMarks = twelfthMarkField.getText();
        double cgpa = cgpaSlider.getValue() / 10.0;
        StringBuilder skills = new StringBuilder();

        // Collect selected skills
        for (JCheckBox checkBox : skillCheckboxes) {
            if (checkBox.isSelected()) {
                skills.append(checkBox.getText()).append(", ");
            }
        }

        // Remove the trailing comma and space
        if (skills.length() > 0) {
            skills.setLength(skills.length() - 2);
        }

        // New fields for hackathons
        String hackathonsWon = hackathonsWonField.getText();
        String hackathonsParticipated = hackathonsParticipatedField.getText();
        String hackathonsWonProofPath = hackathonsWonProofLabel.getText();
        String hackathonsParticipatedProofPath = hackathonsParticipatedProofLabel.getText();

        // Database query for updating the user data
        String query = "UPDATE resumes SET name = ?, phone = ?, tenth_mark = ?, twelfth_mark = ?, cgpa = ?, skills = ?, " +
                       "hackathons_won = ?, hackathons_won_proof = ?, hackathons_participated = ?, hackathons_participated_proof = ? " +
                       "WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            // Set parameters for the query
            stmt.setString(1, name); // Name
            stmt.setString(2, phone); // Phone
            stmt.setString(3, tenthMarks); // Tenth Marks
            stmt.setString(4, twelfthMarks); // Twelfth Marks
            stmt.setDouble(5, cgpa); // CGPA
            stmt.setString(6, skills.toString()); // Skills
            stmt.setInt(7, Integer.parseInt(hackathonsWon)); // Hackathons Won
            stmt.setString(8, hackathonsWonProofPath); // Hackathons Won Proof Path
            stmt.setInt(9, Integer.parseInt(hackathonsParticipated)); // Hackathons Participated
            stmt.setString(10, hackathonsParticipatedProofPath); // Hackathons Participated Proof Path
            stmt.setInt(11, userId); // Assuming `userId` uniquely identifies each user

            // Execute update and show confirmation
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data saved successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No data updated. Please check the user ID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for hackathons won/participated.");
        }
    }

    private void loadUserData() {
        String query = "SELECT * FROM resumes WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);  // Set userId parameter
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Load name, phone, CGPA, tenth mark, and twelfth mark
                nameField.setText(rs.getString("name"));
                phoneField.setText(rs.getString("phone"));
                cgpaSlider.setValue((int) (rs.getDouble("cgpa") * 10));  // Scale CGPA by 10 for the slider
                tenthMarkField.setText(rs.getString("tenth_mark"));
                twelfthMarkField.setText(rs.getString("twelfth_mark"));
                
                // Load skills and set the corresponding checkboxes
                String skillsString = rs.getString("skills");
                if (skillsString != null) {
                    String[] skillsArray = skillsString.split(",\\s*");  // Split skills by comma and optional spaces
                    for (JCheckBox checkBox : skillCheckboxes) {
                        checkBox.setSelected(false); // Reset all checkboxes
                        for (String skill : skillsArray) {
                            if (checkBox.getText().equalsIgnoreCase(skill.trim())) {
                                checkBox.setSelected(true);
                                break;
                            }
                        }
                    }
                }
                
                // Load file paths for profile image and certificates
                String profileImagePath = rs.getString("profile_image_path");
                String cgpaCertificatePath = rs.getString("cgpa_certificate_path");
                String tenthMarksPath = rs.getString("tenth_marks_path");
                String twelfthMarksPath = rs.getString("twelfth_marks_path");

                // Set the paths in the labels
                profileImagePathLabel.setText(profileImagePath != null ? profileImagePath : "No file selected");
                cgpaCertificatePathLabel.setText(cgpaCertificatePath != null ? cgpaCertificatePath : "No file selected");
                tenthMarksPathLabel.setText(tenthMarksPath != null ? tenthMarksPath : "No file selected");
                twelfthMarksPathLabel.setText(twelfthMarksPath != null ? twelfthMarksPath : "No file selected");

                // Load hackathons won, hackathons participated, and proof file paths
                hackathonsWonField.setText(String.valueOf(rs.getInt("hackathons_won")));
                hackathonsParticipatedField.setText(String.valueOf(rs.getInt("hackathons_participated")));
                
                String hackathonsWonProofPath = rs.getString("hackathons_won_proof");
                String hackathonsParticipatedProofPath = rs.getString("hackathons_participated_proof");

                hackathonsWonProofLabel.setText(hackathonsWonProofPath != null ? hackathonsWonProofPath : "No file selected");
                hackathonsParticipatedProofLabel.setText(hackathonsParticipatedProofPath != null ? hackathonsParticipatedProofPath : "No file selected");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResumeBuilder(1));
    }
}