package com.scrs.db;

import com.scrs.models.Registration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    public boolean enrollStudent(int studentId, int courseId) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            // Check available seats
            String checkSql = "SELECT enrolled_count, max_seats FROM courses WHERE course_id=? FOR UPDATE";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, courseId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                int enrolled = rs.getInt("enrolled_count");
                int max = rs.getInt("max_seats");
                if (enrolled >= max) {
                    conn.rollback();
                    return false; // No seats
                }
            } else {
                conn.rollback();
                return false;
            }

            // Insert registration
            String insertSql = "INSERT INTO registrations (student_id, course_id, status, registration_date) VALUES (?,?,'Enrolled',NOW())";
            PreparedStatement insertPs = conn.prepareStatement(insertSql);
            insertPs.setInt(1, studentId);
            insertPs.setInt(2, courseId);
            insertPs.executeUpdate();

            // Update enrolled count
            String updateSql = "UPDATE courses SET enrolled_count = enrolled_count + 1 WHERE course_id=?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, courseId);
            updatePs.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("Enroll error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            return false;
        }
    }

    public boolean isAlreadyEnrolled(int studentId, int courseId) {
        String sql = "SELECT registration_id FROM registrations WHERE student_id=? AND course_id=? AND status='Enrolled'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean dropCourse(int studentId, int courseId) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String updateReg = "UPDATE registrations SET status='Dropped' WHERE student_id=? AND course_id=? AND status='Enrolled'";
            PreparedStatement ps = conn.prepareStatement(updateReg);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                String updateCourse = "UPDATE courses SET enrolled_count = enrolled_count - 1 WHERE course_id=?";
                PreparedStatement ps2 = conn.prepareStatement(updateCourse);
                ps2.setInt(1, courseId);
                ps2.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("Drop course error: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            return false;
        }
    }

    public List<Registration> getStudentRegistrations(int studentId) {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.*, s.name as student_name, c.course_code, c.course_name " +
                     "FROM registrations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id " +
                     "WHERE r.student_id=? AND r.status='Enrolled' ORDER BY r.registration_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extractRegistration(rs));
        } catch (SQLException e) {
            System.err.println("Fetch reg error: " + e.getMessage());
        }
        return list;
    }

    public List<Registration> getAllRegistrations() {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.*, s.name as student_name, c.course_code, c.course_name " +
                     "FROM registrations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id " +
                     "ORDER BY r.registration_date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(extractRegistration(rs));
        } catch (SQLException e) {
            System.err.println("Fetch all reg error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateGrade(int registrationId, String grade) {
        String sql = "UPDATE registrations SET grade=? WHERE registration_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, grade);
            ps.setInt(2, registrationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private Registration extractRegistration(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setRegistrationId(rs.getInt("registration_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setCourseId(rs.getInt("course_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setCourseCode(rs.getString("course_code"));
        r.setCourseName(rs.getString("course_name"));
        r.setStatus(rs.getString("status"));
        r.setRegistrationDate(rs.getDate("registration_date"));
        r.setGrade(rs.getString("grade"));
        return r;
    }
}
