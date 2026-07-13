package domain.port.output;

import domain.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Student save(Student student);

    Optional<Student> findByCode(String code);

    Optional<Student> findByEmail(String email);

    List<Student> findAll();

    boolean delete(String code);

    boolean update(Student student);

    String generateNextCode();
}
