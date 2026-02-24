package com.scrs.models;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int credits;
    private String instructor;
    private int maxSeats;
    private int enrolledCount;
    private String department;

    public Course() {}

    public Course(int courseId, String courseCode, String courseName, String description,
                  int credits, String instructor, int maxSeats, int enrolledCount, String department) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.instructor = instructor;
        this.maxSeats = maxSeats;
        this.enrolledCount = enrolledCount;
        this.department = department;
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public int getMaxSeats() { return maxSeats; }
    public void setMaxSeats(int maxSeats) { this.maxSeats = maxSeats; }
    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getAvailableSeats() { return maxSeats - enrolledCount; }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
