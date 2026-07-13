package domain.model;

public class Student implements Comparable<Student> {
    private String code;
    private String name;
    private String lastname;
    private String email;
    private double gpa;

    public Student() {
    }

    public Student(String code, String name, String lastname, String email, double gpa) {
        this.code = code;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.gpa = gpa;
    }

    @Override
    public int compareTo(Student other) {
        return this.code.compareTo(other.code);
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s | Email: %s | Promedio: %.2f", code, lastname, name, email, gpa);
    }
}
