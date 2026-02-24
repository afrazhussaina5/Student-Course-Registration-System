package com.scrs.db;

import com.scrs.models.Admin;
import java.sql.*;

public class AdminDAO {

    public Admin login(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username=? AND password=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin a = new Admin();
                a.setAdminId(rs.getInt("admin_id"));
                a.setUsername(rs.getString("username"));
                a.setName(rs.getString("name"));
                a.setEmail(rs.getString("email"));
                return a;
            }
        } catch (SQLException e) {
            System.err.println("Admin login error: " + e.getMessage());
        }
        return null;
    }

    public int getTotalStudents() {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { }
        return 0;
    }

    public int getTotalCourses() {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM courses")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { }
        return 0;
    }

    public int getTotalRegistrations() {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM registrations WHERE status='Enrolled'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { }
        return 0;
    }
}
