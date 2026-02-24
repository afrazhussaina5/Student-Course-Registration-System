package com.scrs.db;

import com.scrs.models.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(extractCourse(rs));
        } catch (SQLException e) {
            System.err.println("Fetch courses error: " + e.getMessage());
        }
        return list;
    }

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, description, credits, instructor, max_seats, enrolled_count, department) VALUES (?,?,?,?,?,?,0,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDescription());
            ps.setInt(4, course.getCredits());
            ps.setString(5, course.getInstructor());
            ps.setInt(6, course.getMaxSeats());
            ps.setString(7, course.getDepartment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add course error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code=?, course_name=?, description=?, credits=?, instructor=?, max_seats=?, department=? WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDescription());
            ps.setInt(4, course.getCredits());
            ps.setString(5, course.getInstructor());
            ps.setInt(6, course.getMaxSeats());
            ps.setString(7, course.getDepartment());
            ps.setInt(8, course.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update course error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete course error: " + e.getMessage());
            return false;
        }
    }

    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractCourse(rs);
        } catch (SQLException e) {
            System.err.println("Get course error: " + e.getMessage());
        }
        return null;
    }

    private Course extractCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDescription(rs.getString("description"));
        c.setCredits(rs.getInt("credits"));
        c.setInstructor(rs.getString("instructor"));
        c.setMaxSeats(rs.getInt("max_seats"));
        c.setEnrolledCount(rs.getInt("enrolled_count"));
        c.setDepartment(rs.getString("department"));
        return c;
    }
}
