package domain.model;

public class Classroom {
    private String code;
    private String courseCode;
    private String professorCode;
    private String professorName;

    public Classroom() {
    }

    public Classroom(String code, String courseCode, String professorCode) {
        this.code = code;
        this.courseCode = courseCode;
        this.professorCode = professorCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getProfessorCode() {
        return professorCode;
    }

    public void setProfessorCode(String professorCode) {
        this.professorCode = professorCode;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    @Override
    public String toString() {
        return String.format("Aula [%s] | Curso: %s | Profesor: %s %s",
                code, courseCode, professorCode, professorName != null ? professorName : "");
    }
}
