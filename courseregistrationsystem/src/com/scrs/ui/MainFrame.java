package com.scrs.ui;

import com.scrs.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Student Course Registration System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Student Course Registration System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Manage enrollments, courses, and more", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(new Color(200, 220, 255));
        header.add(subtitleLabel, BorderLayout.SOUTH);

        mainPanel.add(header, BorderLayout.NORTH);

        // Center cards
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(UITheme.BG);
        centerPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Student card
        JPanel studentCard = UITheme.createCard();
        studentCard.setLayout(new BoxLayout(studentCard, BoxLayout.Y_AXIS));
        JLabel studentIcon = new JLabel("👨‍🎓", SwingConstants.CENTER);
        studentIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        studentIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel studentTitle = UITheme.createLabel("Student Portal", UITheme.FONT_SUBTITLE);
        studentTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel studentDesc = new JLabel("<html><center>Login or register<br>to enroll in courses</center></html>", SwingConstants.CENTER);
        studentDesc.setFont(UITheme.FONT_SMALL);
        studentDesc.setForeground(UITheme.TEXT_SECONDARY);
        studentDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton studentBtn = UITheme.createPrimaryButton("Enter as Student");
        studentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        studentBtn.addActionListener(e -> openStudentLogin());

        studentCard.add(studentIcon);
        studentCard.add(Box.createVerticalStrut(10));
        studentCard.add(studentTitle);
        studentCard.add(Box.createVerticalStrut(8));
        studentCard.add(studentDesc);
        studentCard.add(Box.createVerticalStrut(15));
        studentCard.add(studentBtn);

        // Admin card
        JPanel adminCard = UITheme.createCard();
        adminCard.setLayout(new BoxLayout(adminCard, BoxLayout.Y_AXIS));
        JLabel adminIcon = new JLabel("👨‍💼", SwingConstants.CENTER);
        adminIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        adminIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel adminTitle = UITheme.createLabel("Admin Portal", UITheme.FONT_SUBTITLE);
        adminTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel adminDesc = new JLabel("<html><center>Manage students,<br>courses & registrations</center></html>", SwingConstants.CENTER);
        adminDesc.setFont(UITheme.FONT_SMALL);
        adminDesc.setForeground(UITheme.TEXT_SECONDARY);
        adminDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton adminBtn = UITheme.createPrimaryButton("Enter as Admin");
        adminBtn.setBackground(UITheme.SUCCESS);
        adminBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminBtn.addActionListener(e -> openAdminLogin());

        adminCard.add(adminIcon);
        adminCard.add(Box.createVerticalStrut(10));
        adminCard.add(adminTitle);
        adminCard.add(Box.createVerticalStrut(8));
        adminCard.add(adminDesc);
        adminCard.add(Box.createVerticalStrut(15));
        adminCard.add(adminBtn);

        centerPanel.add(studentCard);
        centerPanel.add(adminCard);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("© 2024 Student Course Registration System", SwingConstants.CENTER);
        footer.setFont(UITheme.FONT_SMALL);
        footer.setForeground(UITheme.TEXT_SECONDARY);
        footer.setBorder(new EmptyBorder(0, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void openStudentLogin() {
        new StudentLoginFrame(this).setVisible(true);
        setVisible(false);
    }

    private void openAdminLogin() {
        new AdminLoginFrame(this).setVisible(true);
        setVisible(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
