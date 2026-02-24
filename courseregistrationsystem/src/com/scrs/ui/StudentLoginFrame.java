package com.scrs.ui;

import com.scrs.db.StudentDAO;
import com.scrs.models.Student;
import com.scrs.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentLoginFrame extends JFrame {

    private JFrame parent;
    private JTextField emailField;
    private JPasswordField passwordField;
    private StudentDAO studentDAO = new StudentDAO();

    public StudentLoginFrame(JFrame parent) {
        this.parent = parent;
        setTitle("Student Login");
        setSize(420, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { goBack(); }
        });
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(22, 20, 22, 20));
        JLabel title = new JLabel("Student Login", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Enter your credentials to access the portal", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(196, 220, 255));
        header.add(title);
        header.add(sub);
        main.add(header, BorderLayout.NORTH);

        // Form card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 20, 20, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Icon
        JLabel icon = new JLabel("\uD83D\uDC68\u200D\uD83C\uDF93", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 46));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(icon, gbc);

        // Email
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        JLabel emailLbl = new JLabel("Email:");
        emailLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(emailLbl, gbc);
        gbc.gridx = 1;
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setPreferredSize(new Dimension(210, 34));
        card.add(emailField, gbc);

        // Password
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(passLbl, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(210, 34));
        card.add(passwordField, gbc);

        // Login button
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 6, 8);
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBackground(new Color(37, 99, 235));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(210, 38));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());
        card.add(loginBtn, gbc);

        // Info note - NO register button
        gbc.gridy = 4;
        gbc.insets = new Insets(6, 8, 4, 8);
        JLabel note = new JLabel(
            "<html><center><i>Don't have an account?<br>Contact your Admin to get registered.</i></center></html>",
            SwingConstants.CENTER);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        note.setForeground(new Color(100, 116, 139));
        card.add(note, gbc);

        // Back button
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 8, 4, 8);
        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        backBtn.setBorderPainted(false);
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(new Color(100, 116, 139));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBack());
        card.add(backBtn, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(248, 250, 252));
        wrapper.setBorder(new EmptyBorder(25, 40, 25, 40));
        wrapper.add(card);
        main.add(wrapper, BorderLayout.CENTER);

        getRootPane().setDefaultButton(loginBtn);
        add(main);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Student student = studentDAO.login(email, password);
        if (student != null) {
            JOptionPane.showMessageDialog(this, "Welcome, " + student.getName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new StudentDashboard(student).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid email or password.\nContact Admin if you don't have an account.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        parent.setVisible(true);
        dispose();
    }
}