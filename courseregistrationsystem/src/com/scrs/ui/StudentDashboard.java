package com.scrs.ui;

import com.scrs.db.CourseDAO;
import com.scrs.db.RegistrationDAO;
import com.scrs.models.Course;
import com.scrs.models.Registration;
import com.scrs.models.Student;
import com.scrs.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private Student student;
    private CourseDAO courseDAO = new CourseDAO();
    private RegistrationDAO registrationDAO = new RegistrationDAO();

    private JTable availableCoursesTable;
    private JTable myCoursesTable;
    private DefaultTableModel availableModel;
    private DefaultTableModel myCoursesModel;

    public StudentDashboard(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getName());
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        loadAvailableCourses();
        loadMyCourses();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + student.getName() + " | " + student.getDepartment() + " | Sem " + student.getSemester());
        welcomeLabel.setFont(UITheme.FONT_SUBTITLE);
        welcomeLabel.setForeground(Color.WHITE);
        topBar.add(welcomeLabel, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        JButton profileBtn = new JButton("👤 Profile");
        profileBtn.setFont(UITheme.FONT_SMALL);
        profileBtn.setBackground(new Color(255, 255, 255, 50));
        profileBtn.setForeground(Color.WHITE);
        profileBtn.setBorderPainted(false);
        profileBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileBtn.addActionListener(e -> showProfile());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UITheme.FONT_SMALL);
        logoutBtn.setBackground(UITheme.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        topRight.add(profileBtn);
        topRight.add(logoutBtn);
        topBar.add(topRight, BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBackground(UITheme.BG);

        tabs.addTab("📚 Available Courses", createAvailableCoursesPanel());
        tabs.addTab("✅ My Registrations", createMyCoursesPanel());

        main.add(tabs, BorderLayout.CENTER);
        add(main);
    }

    private JPanel createAvailableCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(UITheme.BG);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel heading = UITheme.createLabel("All Available Courses", UITheme.FONT_SUBTITLE);
        toolbar.add(heading, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG);
        JButton refreshBtn = UITheme.createPrimaryButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadAvailableCourses());
        JButton enrollBtn = UITheme.createSuccessButton("✅ Enroll in Selected");
        enrollBtn.addActionListener(e -> enrollInCourse());
        btnPanel.add(refreshBtn);
        btnPanel.add(enrollBtn);
        toolbar.add(btnPanel, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Code", "Course Name", "Instructor", "Credits", "Department", "Available Seats"};
        availableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        availableCoursesTable = createStyledTable(availableModel);
        availableCoursesTable.getColumnModel().getColumn(0).setMaxWidth(50);
        availableCoursesTable.getColumnModel().getColumn(4).setMaxWidth(70);
        availableCoursesTable.getColumnModel().getColumn(6).setMaxWidth(110);

        panel.add(new JScrollPane(availableCoursesTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(UITheme.BG);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel heading = UITheme.createLabel("My Enrolled Courses", UITheme.FONT_SUBTITLE);
        toolbar.add(heading, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG);
        JButton refreshBtn = UITheme.createPrimaryButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadMyCourses());
        JButton dropBtn = UITheme.createDangerButton("❌ Drop Course");
        dropBtn.addActionListener(e -> dropCourse());
        btnPanel.add(refreshBtn);
        btnPanel.add(dropBtn);
        toolbar.add(btnPanel, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"Reg ID", "Course Code", "Course Name", "Status", "Registration Date", "Grade"};
        myCoursesModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myCoursesTable = createStyledTable(myCoursesModel);
        myCoursesTable.getColumnModel().getColumn(0).setMaxWidth(60);

        panel.add(new JScrollPane(myCoursesTable), BorderLayout.CENTER);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(UITheme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        return table;
    }

    private void loadAvailableCourses() {
        availableModel.setRowCount(0);
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            availableModel.addRow(new Object[]{
                c.getCourseId(), c.getCourseCode(), c.getCourseName(),
                c.getInstructor(), c.getCredits(), c.getDepartment(), c.getAvailableSeats()
            });
        }
    }

    private void loadMyCourses() {
        myCoursesModel.setRowCount(0);
        List<Registration> regs = registrationDAO.getStudentRegistrations(student.getStudentId());
        for (Registration r : regs) {
            myCoursesModel.addRow(new Object[]{
                r.getRegistrationId(), r.getCourseCode(), r.getCourseName(),
                r.getStatus(), r.getRegistrationDate(), r.getGrade() != null ? r.getGrade() : "-"
            });
        }
    }

    private void enrollInCourse() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select a course to enroll.");
            return;
        }
        int courseId = (Integer) availableModel.getValueAt(selectedRow, 0);
        String courseName = (String) availableModel.getValueAt(selectedRow, 2);
        int seats = (Integer) availableModel.getValueAt(selectedRow, 6);

        if (seats <= 0) {
            UITheme.showError(this, "No seats available for this course.");
            return;
        }
        if (registrationDAO.isAlreadyEnrolled(student.getStudentId(), courseId)) {
            UITheme.showError(this, "You are already enrolled in this course.");
            return;
        }
        if (UITheme.confirmDialog(this, "Enroll in: " + courseName + "?")) {
            if (registrationDAO.enrollStudent(student.getStudentId(), courseId)) {
                UITheme.showSuccess(this, "Successfully enrolled in " + courseName);
                loadAvailableCourses();
                loadMyCourses();
            } else {
                UITheme.showError(this, "Enrollment failed. Please try again.");
            }
        }
    }

    private void dropCourse() {
        int selectedRow = myCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            UITheme.showError(this, "Please select a course to drop.");
            return;
        }
        int courseId = (Integer) myCoursesModel.getValueAt(selectedRow, 0); // reg id -> we need course id
        String courseName = (String) myCoursesModel.getValueAt(selectedRow, 2);

        // We stored registration id in col 0, we need course id
        // Let's get course_id from the registration list
        List<Registration> regs = registrationDAO.getStudentRegistrations(student.getStudentId());
        if (selectedRow >= regs.size()) return;
        int actualCourseId = regs.get(selectedRow).getCourseId();

        if (UITheme.confirmDialog(this, "Drop course: " + courseName + "?")) {
            if (registrationDAO.dropCourse(student.getStudentId(), actualCourseId)) {
                UITheme.showSuccess(this, "Course dropped successfully.");
                loadAvailableCourses();
                loadMyCourses();
            } else {
                UITheme.showError(this, "Failed to drop course.");
            }
        }
    }

    private void showProfile() {
        JDialog dialog = new JDialog(this, "My Profile", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        String[][] info = {
            {"Name", student.getName()},
            {"Email", student.getEmail()},
            {"Phone", student.getPhone()},
            {"Department", student.getDepartment()},
            {"Semester", String.valueOf(student.getSemester())}
        };

        for (int i = 0; i < info.length; i++) {
            gbc.gridy = i; gbc.gridx = 0;
            JLabel lbl = new JLabel(info[i][0] + ":");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            panel.add(lbl, gbc);
            gbc.gridx = 1;
            JLabel val = new JLabel(info[i][1]);
            val.setFont(UITheme.FONT_BODY);
            panel.add(val, gbc);
        }

        gbc.gridy = info.length; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        panel.add(closeBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void logout() {
        if (UITheme.confirmDialog(this, "Are you sure you want to logout?")) {
            new MainFrame().setVisible(true);
            dispose();
        }
    }
}
