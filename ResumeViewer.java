package Resume;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResumeViewer extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel nameLabel, emailLabel, phoneLabel, createdAtLabel, cgpaLabel, skillsLabel;
    private JLabel tenthMarkLabel, twelfthMarkLabel, cgpaCertificatePathLabel;
    private JLabel tenthMarksPathLabel, twelfthMarksPathLabel;
    private JLabel hackathonsWonLabel, hackathonsWonProofLabel, hackathonsParticipatedLabel, hackathonsParticipatedProofLabel;
    private JLabel profileImageDisplay;
    private Integer userId;

    public ResumeViewer(Integer userId) {
        this.userId = userId;

        setTitle("Resume Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.decode("#F5F5F5"));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Font
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font sectionFont = new Font("Arial", Font.BOLD, 18);
        Font labelFont = new Font("Arial", Font.PLAIN, 16);

        // Section Headers
        JLabel titleLabel = new JLabel("Resume Details", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        contentPanel.add(createSectionPanel("Personal Details", sectionFont));
        nameLabel = createLabel(contentPanel, "Name:", labelFont);
        emailLabel = createLabel(contentPanel, "Email:", labelFont);
        phoneLabel = createLabel(contentPanel, "Phone:", labelFont);
        createdAtLabel = createLabel(contentPanel, "Account Created At:", labelFont);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between sections

        contentPanel.add(createSectionPanel("Professional Details", sectionFont));
        cgpaLabel = createLabel(contentPanel, "CGPA:", labelFont);
        skillsLabel = createLabel(contentPanel, "Skills:", labelFont);

        profileImageDisplay = new JLabel();
        profileImageDisplay.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(profileImageDisplay);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between sections

        contentPanel.add(createSectionPanel("Academic Records", sectionFont));
        tenthMarkLabel = createLabel(contentPanel, "Tenth Mark:", labelFont);
        twelfthMarkLabel = createLabel(contentPanel, "Twelfth Mark:", labelFont);
        cgpaCertificatePathLabel = createLabel(contentPanel, "CGPA Certificate Path:", labelFont);
        tenthMarksPathLabel = createLabel(contentPanel, "Tenth Marks Path:", labelFont);
        twelfthMarksPathLabel = createLabel(contentPanel, "Twelfth Marks Path:", labelFont);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between sections

        contentPanel.add(createSectionPanel("Hackathon Participation", sectionFont));
        hackathonsWonLabel = createLabel(contentPanel, "Hackathons Won:", labelFont);
        hackathonsWonProofLabel = createLabel(contentPanel, "Proof of Hackathons Won:", labelFont);
        hackathonsParticipatedLabel = createLabel(contentPanel, "Hackathons Participated:", labelFont);
        hackathonsParticipatedProofLabel = createLabel(contentPanel, "Proof of Hackathons Participated:", labelFont);

        loadUserData();

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        setSize(800, 800); // Increased window size for better fit
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createLabel(JPanel panel, String labelText, Font font) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBackground(Color.decode("#F5F5F5"));
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(Color.decode("#333333"));
        labelPanel.add(label);
        panel.add(labelPanel); // Ensure the label is actually added to the main contentPanel
        return label;
    }

    private JPanel createSectionPanel(String title, Font font) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#DDE8F0"));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(font);
        titleLabel.setForeground(Color.decode("#2B3E50"));
        panel.add(titleLabel);
        return panel;
    }

    private void loadUserData() {
        String query = "SELECT name, email, phone, skills, cgpa, profile_image_path, created_at, " +
                       "tenth_mark, twelfth_mark, cgpa_certificate_path, tenth_marks_path, " +
                       "twelfth_marks_path, hackathons_won, hackathons_won_proof, " +
                       "hackathons_participated, hackathons_participated_proof " +
                       "FROM resumes WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Update the labels with data from the database
                nameLabel.setText("Name: " + resultSet.getString("name"));
                emailLabel.setText("Email: " + resultSet.getString("email"));
                phoneLabel.setText("Phone: " + resultSet.getString("phone"));
                createdAtLabel.setText("Account Created At: " + resultSet.getString("created_at"));
                cgpaLabel.setText("CGPA: " + resultSet.getDouble("cgpa"));

                // Handle skills as a list, split by commas
                String skills = resultSet.getString("skills");
                skillsLabel.setText(skills != null ? "<html>" + String.join("<br>", skills.split(",")) + "</html>" : "Not Available");

                // Display academic details
                tenthMarkLabel.setText(resultSet.getString("tenth_mark") != null ? "Tenth Mark: " + resultSet.getInt("tenth_mark") : "Tenth Mark: Not Available");
                twelfthMarkLabel.setText(resultSet.getString("twelfth_mark") != null ? "Twelfth Mark: " + resultSet.getInt("twelfth_mark") : "Twelfth Mark: Not Available");
                cgpaCertificatePathLabel.setText(resultSet.getString("cgpa_certificate_path") != null ? "CGPA Certificate Path: " + resultSet.getString("cgpa_certificate_path") : "CGPA Certificate Path: Not Available");
                tenthMarksPathLabel.setText(resultSet.getString("tenth_marks_path") != null ? "Tenth Marks Path: " + resultSet.getString("tenth_marks_path") : "Tenth Marks Path: Not Available");
                twelfthMarksPathLabel.setText(resultSet.getString("twelfth_marks_path") != null ? "Twelfth Marks Path: " + resultSet.getString("twelfth_marks_path") : "Twelfth Marks Path: Not Available");

                // Handle hackathons data
                hackathonsWonLabel.setText(resultSet.getString("hackathons_won") != null ? "Hackathons Won: " + resultSet.getInt("hackathons_won") : "Hackathons Won: Not Available");
                hackathonsWonProofLabel.setText(resultSet.getString("hackathons_won_proof") != null ? "Proof of Hackathons Won: " + resultSet.getString("hackathons_won_proof") : "Proof of Hackathons Won: Not Available");
                hackathonsParticipatedLabel.setText(resultSet.getString("hackathons_participated") != null ? "Hackathons Participated: " + resultSet.getInt("hackathons_participated") : "Hackathons Participated: Not Available");
                hackathonsParticipatedProofLabel.setText(resultSet.getString("hackathons_participated_proof") != null ? "Proof of Hackathons Participated: " + resultSet.getString("hackathons_participated_proof") : "Proof of Hackathons Participated: Not Available");

                // Handle profile image display
                String profileImagePath = resultSet.getString("profile_image_path");
                if (profileImagePath != null && !profileImagePath.isEmpty()) {
                    ImageIcon profileImageIcon = new ImageIcon(profileImagePath);
                    Image profileImage = profileImageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    profileImageIcon = new ImageIcon(profileImage);
                    profileImageDisplay.setIcon(profileImageIcon);
                } else {
                    profileImageDisplay.setText("No Profile Image Available");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No data found for the specified user.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
        }
    }
}