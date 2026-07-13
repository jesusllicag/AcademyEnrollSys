package domain.model;

import java.time.LocalDateTime;

public class EnrollmentQueueEntry implements Comparable<EnrollmentQueueEntry> {
    private String studentCode;
    private String studentName;
    private String studentLastname;
    private double gpa;
    private int position;
    private LocalDateTime registeredAt;

    public EnrollmentQueueEntry() {
    }

    public EnrollmentQueueEntry(String studentCode, String studentName, String studentLastname,
            double gpa, int position, LocalDateTime registeredAt) {
        this.studentCode = studentCode;
        this.studentName = studentName;
        this.studentLastname = studentLastname;
        this.gpa = gpa;
        this.position = position;
        this.registeredAt = registeredAt;
    }

    // Higher GPA first; on tie, alphabetical by lastname then name
    @Override
    public int compareTo(EnrollmentQueueEntry other) {
        int gpaCmp = Double.compare(other.gpa, this.gpa);
        if (gpaCmp != 0)
            return gpaCmp;
        int lastnameCmp = this.studentLastname.compareToIgnoreCase(other.studentLastname);
        if (lastnameCmp != 0)
            return lastnameCmp;
        return this.studentName.compareToIgnoreCase(other.studentName);
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentLastname() {
        return studentLastname;
    }

    public void setStudentLastname(String studentLastname) {
        this.studentLastname = studentLastname;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return String.format("Pos #%d | [%s] %s %s | Promedio: %.2f",
                position, studentCode, studentLastname, studentName, gpa);
    }
}
