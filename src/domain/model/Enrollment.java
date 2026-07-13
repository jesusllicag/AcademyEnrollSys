package domain.model;

import java.time.LocalDateTime;

public class Enrollment {
    private String studentCode;
    private String courseCode;
    private LocalDateTime enrolledAt;
    private String courseName;
    private String studentName;

    public Enrollment() {
    }

    public Enrollment(String studentCode, String courseCode, LocalDateTime enrolledAt) {
        this.studentCode = studentCode;
        this.courseCode = courseCode;
        this.enrolledAt = enrolledAt;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return String.format("Matricula: Alumno [%s] -> Curso [%s] %s",
                studentCode, courseCode, courseName != null ? "(" + courseName + ")" : "");
    }
}
