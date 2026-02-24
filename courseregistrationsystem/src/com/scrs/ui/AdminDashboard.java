package com.scrs.ui;

import com.scrs.db.*;
import com.scrs.models.*;
import com.scrs.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private Admin admin;
    private AdminDAO        adminDAO   = new AdminDAO();
    private StudentDAO      studentDAO = new StudentDAO();
    private CourseDAO       courseDAO  = new CourseDAO();
    private RegistrationDAO regDAO     = new RegistrationDAO();

    private JLabel statStudents, statCourses, statEnrolled;
    private DefaultTableModel studentModel, courseModel, regModel;
    private JTable studentTable, courseTable, regTable;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getName());
        setSize(1060, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        loadStudents();
        loadCourses();
        loadRegistrations();
    }

    // ── Build UI ───────────────────────────────────────────────────
    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(248, 250, 252));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(22, 163, 74));
        topBar.setBorder(new EmptyBorder(13, 18, 13, 18));
        JLabel title = new JLabel("Admin Dashboard  |  Logged in as: " + admin.getName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);
        JButton logoutBtn = makeBtn("Logout", new Color(220, 38, 38));
        logoutBtn.addActionListener(e -> logout());
        topBar.add(logoutBtn, BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setBackground(new Color(248, 250, 252));
        statsRow.setBorder(new EmptyBorder(14, 14, 0, 14));
        statStudents = addStatCard(statsRow, "Total Students",     new Color(37, 99, 235));
        statCourses  = addStatCard(statsRow, "Total Courses",      new Color(22, 163, 74));
        statEnrolled = addStatCard(statsRow, "Active Enrollments", new Color(220, 38, 38));

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBorder(new EmptyBorder(8, 10, 10, 10));
        tabs.addTab("  Manage Students  ",  buildStudentsTab());
        tabs.addTab("  Manage Courses  ",   buildCoursesTab());
        tabs.addTab("  Registrations  ",    buildRegistrationsTab());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 250, 252));
        center.add(statsRow, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        add(main);
    }

    // ── Stat Card ──────────────────────────────────────────────────
    private JLabel addStatCard(JPanel parent, String label, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(255, 255, 255, 180));
        JLabel val = new JLabel("0");
        val.setFont(new Font("Segoe UI", Font.BOLD, 30));
        val.setForeground(Color.WHITE);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        parent.add(card);
        return val;
    }

    private void refreshStats() {
        statStudents.setText(String.valueOf(adminDAO.getTotalStudents()));
        statCourses .setText(String.valueOf(adminDAO.getTotalCourses()));
        statEnrolled.setText(String.valueOf(adminDAO.getTotalRegistrations()));
    }

    // ── Students Tab ───────────────────────────────────────────────
    private JPanel buildStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        tb.setBackground(new Color(248, 250, 252));
        JButton refresh = makeBtn("Refresh",         new Color(37, 99, 235));
        JButton add     = makeBtn("Add New Student", new Color(22, 163, 74));
        JButton edit    = makeBtn("Edit Selected",   new Color(37, 99, 235));
        JButton delete  = makeBtn("Delete Selected", new Color(220, 38, 38));
        refresh.addActionListener(e -> loadStudents());
        add    .addActionListener(e -> showStudentDialog(null));
        edit   .addActionListener(e -> editStudent());
        delete .addActionListener(e -> deleteStudent());
        tb.add(refresh); tb.add(add); tb.add(edit); tb.add(delete);
        panel.add(tb, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Email", "Phone", "Department", "Semester"};
        studentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentModel);
        styleTable(studentTable, new Color(22, 163, 74));
        studentTable.getColumnModel().getColumn(0).setMaxWidth(45);
        studentTable.getColumnModel().getColumn(5).setMaxWidth(75);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Courses Tab ────────────────────────────────────────────────
    private JPanel buildCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        tb.setBackground(new Color(248, 250, 252));
        JButton refresh = makeBtn("Refresh",         new Color(37, 99, 235));
        JButton add     = makeBtn("Add New Course",  new Color(22, 163, 74));
        JButton edit    = makeBtn("Edit Selected",   new Color(37, 99, 235));
        JButton delete  = makeBtn("Delete Selected", new Color(220, 38, 38));
        refresh.addActionListener(e -> loadCourses());
        add    .addActionListener(e -> showCourseDialog(null));
        edit   .addActionListener(e -> editCourse());
        delete .addActionListener(e -> deleteCourse());
        tb.add(refresh); tb.add(add); tb.add(edit); tb.add(delete);
        panel.add(tb, BorderLayout.NORTH);

        String[] cols = {"ID", "Code", "Course Name", "Instructor", "Credits", "Dept", "Max Seats", "Enrolled", "Available"};
        courseModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        courseTable = new JTable(courseModel);
        styleTable(courseTable, new Color(22, 163, 74));
        courseTable.getColumnModel().getColumn(0).setMaxWidth(45);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Registrations Tab ──────────────────────────────────────────
    private JPanel buildRegistrationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        tb.setBackground(new Color(248, 250, 252));
        JButton refresh = makeBtn("Refresh",      new Color(37, 99, 235));
        JButton grade   = makeBtn("Update Grade", new Color(22, 163, 74));
        refresh.addActionListener(e -> loadRegistrations());
        grade  .addActionListener(e -> updateGrade());
        tb.add(refresh); tb.add(grade);
        panel.add(tb, BorderLayout.NORTH);

        String[] cols = {"Reg ID", "Student Name", "Course Code", "Course Name", "Status", "Date", "Grade"};
        regModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        regTable = new JTable(regModel);
        styleTable(regTable, new Color(22, 163, 74));
        regTable.getColumnModel().getColumn(0).setMaxWidth(60);
        panel.add(new JScrollPane(regTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Load Data ──────────────────────────────────────────────────
    private void loadStudents() {
        studentModel.setRowCount(0);
        for (Student s : studentDAO.getAllStudents()) {
            studentModel.addRow(new Object[]{
                s.getStudentId(), s.getName(), s.getEmail(),
                s.getPhone(), s.getDepartment(), s.getSemester()
            });
        }
        refreshStats();
    }

    private void loadCourses() {
        courseModel.setRowCount(0);
        for (Course c : courseDAO.getAllCourses()) {
            courseModel.addRow(new Object[]{
                c.getCourseId(), c.getCourseCode(), c.getCourseName(),
                c.getInstructor(), c.getCredits(), c.getDepartment(),
                c.getMaxSeats(), c.getEnrolledCount(), c.getAvailableSeats()
            });
        }
        refreshStats();
    }

    private void loadRegistrations() {
        regModel.setRowCount(0);
        for (Registration r : regDAO.getAllRegistrations()) {
            regModel.addRow(new Object[]{
                r.getRegistrationId(), r.getStudentName(), r.getCourseCode(),
                r.getCourseName(), r.getStatus(), r.getRegistrationDate(),
                r.getGrade() != null ? r.getGrade() : "-"
            });
        }
        refreshStats();
    }

    // ── Student Actions ────────────────────────────────────────────
    private void editStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) { error("Please select a student to edit."); return; }
        Student s = studentDAO.getStudentById((int) studentModel.getValueAt(row, 0));
        if (s != null) showStudentDialog(s);
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) { error("Please select a student."); return; }
        int id = (int) studentModel.getValueAt(row, 0);
        String name = (String) studentModel.getValueAt(row, 1);
        if (confirm("Delete student: " + name + "?\nThis will also remove their registrations.")) {
            if (studentDAO.deleteStudent(id)) { success("Student deleted successfully."); loadStudents(); }
            else error("Delete failed. Please try again.");
        }
    }

    // ── Add / Edit Student Dialog ──────────────────────────────────
    private void showStudentDialog(Student existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(this, isEdit ? "Edit Student" : "Add New Student", true);
        dlg.setSize(420, 420);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 7, 7, 7);

        Font f = new Font("Segoe UI", Font.PLAIN, 13);

        JTextField    nameF  = new JTextField();    nameF .setPreferredSize(new Dimension(200, 32)); nameF .setFont(f);
        JTextField    emailF = new JTextField();    emailF.setPreferredSize(new Dimension(200, 32)); emailF.setFont(f);
        JTextField    phoneF = new JTextField();    phoneF.setPreferredSize(new Dimension(200, 32)); phoneF.setFont(f);
        JPasswordField passF = new JPasswordField(); passF.setPreferredSize(new Dimension(200, 32)); passF .setFont(f);

        String[] depts = {"Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Electrical"};
        JComboBox<String> deptBox = new JComboBox<>(depts);
        deptBox.setFont(f);

        JSpinner semSpin = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        semSpin.setFont(f);

        // Pre-fill fields when editing
        if (isEdit) {
            nameF .setText(existing.getName());
            emailF.setText(existing.getEmail());
            phoneF.setText(existing.getPhone());
            passF .setText(existing.getPassword());
            deptBox.setSelectedItem(existing.getDepartment());
            semSpin.setValue(existing.getSemester());
        }

        // Add rows to dialog
        Object[][] rows = {
            {"Full Name *",  nameF},
            {"Email *",      emailF},
            {"Phone",        phoneF},
            {"Password *",   passF},
            {"Department",   deptBox},
            {"Semester",     semSpin}
        };

        for (int i = 0; i < rows.length; i++) {
            g.gridy = i; g.gridx = 0; g.gridwidth = 1;
            JLabel lbl = new JLabel((String) rows[i][0]);
            lbl.setFont(f);
            p.add(lbl, g);
            g.gridx = 1;
            p.add((Component) rows[i][1], g);
        }

        // Save button
        g.gridy = rows.length; g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(14, 7, 7, 7);
        Color btnColor = isEdit ? new Color(37, 99, 235) : new Color(22, 163, 74);
        JButton saveBtn = makeBtn(isEdit ? "Update Student" : "Add Student", btnColor);

        saveBtn.addActionListener(e -> {
            String nm  = nameF .getText().trim();
            String em  = emailF.getText().trim();
            String pw  = new String(passF.getPassword()).trim();

            // Validation
            if (nm.isEmpty() || em.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Name, Email and Password are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!em.contains("@") || !em.contains(".")) {
                JOptionPane.showMessageDialog(dlg, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isEdit && studentDAO.emailExists(em)) {
                JOptionPane.showMessageDialog(dlg, "This email is already registered.", "Duplicate Email", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Build student object
            Student s = isEdit ? existing : new Student();
            s.setName(nm);
            s.setEmail(em);
            s.setPhone(phoneF.getText().trim());
            s.setPassword(pw);
            s.setDepartment((String) deptBox.getSelectedItem());
            s.setSemester((Integer) semSpin.getValue());

            boolean ok = isEdit ? studentDAO.updateStudent(s) : studentDAO.addStudent(s);
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Student " + (isEdit ? "updated" : "added") + " successfully!");
                dlg.dispose();
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(dlg, "Operation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(saveBtn, g);
        dlg.add(p);
        dlg.setVisible(true);
    }

    // ── Course Actions ─────────────────────────────────────────────
    private void editCourse() {
        int row = courseTable.getSelectedRow();
        if (row == -1) { error("Please select a course to edit."); return; }
        Course c = courseDAO.getCourseById((int) courseModel.getValueAt(row, 0));
        if (c != null) showCourseDialog(c);
    }

    private void deleteCourse() {
        int row = courseTable.getSelectedRow();
        if (row == -1) { error("Please select a course."); return; }
        int id = (int) courseModel.getValueAt(row, 0);
        String name = (String) courseModel.getValueAt(row, 2);
        if (confirm("Delete course: " + name + "?")) {
            if (courseDAO.deleteCourse(id)) { success("Course deleted successfully."); loadCourses(); }
            else error("Delete failed. Course may have active enrollments.");
        }
    }

    // ── Add / Edit Course Dialog ───────────────────────────────────
    private void showCourseDialog(Course existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(this, isEdit ? "Edit Course" : "Add New Course", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 7, 7, 7);

        Font f = new Font("Segoe UI", Font.PLAIN, 13);

        JTextField codeF  = new JTextField(); codeF .setPreferredSize(new Dimension(200, 32)); codeF .setFont(f);
        JTextField nameF  = new JTextField(); nameF .setPreferredSize(new Dimension(200, 32)); nameF .setFont(f);
        JTextField instrF = new JTextField(); instrF.setPreferredSize(new Dimension(200, 32)); instrF.setFont(f);
        JSpinner credSpin = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));  credSpin.setFont(f);
        JSpinner seatSpin = new JSpinner(new SpinnerNumberModel(40, 5, 200, 1)); seatSpin.setFont(f);
        String[] depts = {"Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Electrical", "Common"};
        JComboBox<String> deptBox = new JComboBox<>(depts); deptBox.setFont(f);

        if (isEdit) {
            codeF .setText(existing.getCourseCode());
            nameF .setText(existing.getCourseName());
            instrF.setText(existing.getInstructor());
            credSpin.setValue(existing.getCredits());
            seatSpin.setValue(existing.getMaxSeats());
            deptBox.setSelectedItem(existing.getDepartment());
        }

        Object[][] rows = {
            {"Course Code *", codeF},
            {"Course Name *", nameF},
            {"Instructor",    instrF},
            {"Credits",       credSpin},
            {"Max Seats",     seatSpin},
            {"Department",    deptBox}
        };

        for (int i = 0; i < rows.length; i++) {
            g.gridy = i; g.gridx = 0; g.gridwidth = 1;
            JLabel lbl = new JLabel((String) rows[i][0]); lbl.setFont(f);
            p.add(lbl, g);
            g.gridx = 1;
            p.add((Component) rows[i][1], g);
        }

        g.gridy = rows.length; g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(14, 7, 7, 7);
        Color btnColor = isEdit ? new Color(37, 99, 235) : new Color(22, 163, 74);
        JButton saveBtn = makeBtn(isEdit ? "Update Course" : "Add Course", btnColor);

        saveBtn.addActionListener(e -> {
            String code  = codeF.getText().trim();
            String cname = nameF.getText().trim();
            if (code.isEmpty() || cname.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Course Code and Name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Course c = isEdit ? existing : new Course();
            c.setCourseCode(code);
            c.setCourseName(cname);
            c.setInstructor(instrF.getText().trim());
            c.setCredits((Integer) credSpin.getValue());
            c.setMaxSeats((Integer) seatSpin.getValue());
            c.setDepartment((String) deptBox.getSelectedItem());

            boolean ok = isEdit ? courseDAO.updateCourse(c) : courseDAO.addCourse(c);
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Course " + (isEdit ? "updated" : "added") + " successfully!");
                dlg.dispose();
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(dlg, "Operation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(saveBtn, g);
        dlg.add(p);
        dlg.setVisible(true);
    }

    // ── Grade Update ───────────────────────────────────────────────
    private void updateGrade() {
        int row = regTable.getSelectedRow();
        if (row == -1) { error("Please select a registration."); return; }
        int    regId  = (int)    regModel.getValueAt(row, 0);
        String sName  = (String) regModel.getValueAt(row, 1);
        String cName  = (String) regModel.getValueAt(row, 3);
        String[] grades = {"A+", "A", "B+", "B", "C+", "C", "D", "F"};
        String grade = (String) JOptionPane.showInputDialog(this,
            "Assign grade for:\nStudent : " + sName + "\nCourse  : " + cName,
            "Update Grade", JOptionPane.PLAIN_MESSAGE, null, grades, grades[0]);
        if (grade != null) {
            if (regDAO.updateGrade(regId, grade)) { success("Grade updated to " + grade); loadRegistrations(); }
            else error("Failed to update grade.");
        }
    }

    // ── Helpers ────────────────────────────────────────────────────
    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        return b;
    }

    private void styleTable(JTable table, Color headerColor) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    private void success(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void logout() {
        if (confirm("Are you sure you want to logout?")) {
            new MainFrame().setVisible(true);
            dispose();
        }
    }
}
