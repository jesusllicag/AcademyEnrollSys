package domain.port.input;

import domain.model.Student;
import java.util.List;

public interface StudentUseCase {
    Student createStudent(String name, String lastname, double gpa);

    Student findByCode(String code);

    Student findByEmail(String email);

    List<Student> findAll();

    boolean deleteStudent(String code);

    boolean updateGpa(String code, double gpa);
}
