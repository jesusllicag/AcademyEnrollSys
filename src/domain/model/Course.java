package domain.model;

public class Course {
    private String code;
    private String name;
    private int maxStudents;
    private boolean enrollmentOpen;

    public Course() {
    }

    public Course(String code, String name, int maxStudents, boolean enrollmentOpen) {
        this.code = code;
        this.name = name;
        this.maxStudents = maxStudents;
        this.enrollmentOpen = enrollmentOpen;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public boolean isEnrollmentOpen() {
        return enrollmentOpen;
    }

    public void setEnrollmentOpen(boolean enrollmentOpen) {
        this.enrollmentOpen = enrollmentOpen;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Max alumnos: %d | Matricula: %s",
                code, name, maxStudents, enrollmentOpen ? "ABIERTA" : "CERRADA");
    }
}
