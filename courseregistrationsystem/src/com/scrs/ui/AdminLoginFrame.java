package com.scrs.ui;

import com.scrs.db.AdminDAO;
import com.scrs.models.Admin;
import com.scrs.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminLoginFrame extends JFrame {

    private JFrame parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AdminDAO adminDAO = new AdminDAO();

    public AdminLoginFrame(JFrame parent) {
        this.parent = parent;
        setTitle("Admin Login");
        setSize(400, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { goBack(); }
        });
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);

        JPanel header = new JPanel();
        header.setBackground(UITheme.SUCCESS);
        header.setBorder(new EmptyBorder(25, 20, 25, 20));
        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);

        JLabel iconLabel = new JLabel("👨‍💼", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        formCard.add(iconLabel, gbc);

        gbc.gridwidth=1;
        gbc.gridy=1; gbc.gridx=0;
        formCard.add(UITheme.createLabel("Username:", UITheme.FONT_BODY), gbc);
        gbc.gridx=1;
        usernameField = UITheme.createTextField("Username");
        formCard.add(usernameField, gbc);

        gbc.gridy=2; gbc.gridx=0;
        formCard.add(UITheme.createLabel("Password:", UITheme.FONT_BODY), gbc);
        gbc.gridx=1;
        passwordField = UITheme.createPasswordField();
        formCard.add(passwordField, gbc);

        gbc.gridy=3; gbc.gridx=0; gbc.gridwidth=2;
        gbc.insets = new Insets(15, 8, 8, 8);
        JButton loginBtn = UITheme.createSuccessButton("Admin Login");
        loginBtn.setPreferredSize(new Dimension(200, 42));
        loginBtn.addActionListener(e -> doLogin());
        formCard.add(loginBtn, gbc);

        gbc.gridy=4;
        gbc.insets = new Insets(4, 8, 4, 8);
        JButton backBtn = new JButton("← Back to Home");
        backBtn.setFont(UITheme.FONT_SMALL);
        backBtn.setBorderPainted(false);
        backBtn.setBackground(UITheme.BG);
        backBtn.setForeground(UITheme.TEXT_SECONDARY);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBack());
        formCard.add(backBtn, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.setBorder(new EmptyBorder(30, 40, 30, 40));
        wrapper.add(formCard);
        main.add(wrapper, BorderLayout.CENTER);

        getRootPane().setDefaultButton(loginBtn);
        add(main);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showError(this, "Please enter username and password.");
            return;
        }
        Admin admin = adminDAO.login(username, password);
        if (admin != null) {
            UITheme.showSuccess(this, "Welcome, Admin " + admin.getName() + "!");
            new AdminDashboard(admin).setVisible(true);
            dispose();
        } else {
            UITheme.showError(this, "Invalid username or password.");
        }
    }

    private void goBack() {
        parent.setVisible(true);
        dispose();
    }
}
